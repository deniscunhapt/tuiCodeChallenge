package org.tui.codechallenge.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.tui.codechallenge.services.contracts.IGitHubService;
import org.tui.codechallenge.services.exceptions.ErrorProcessingRepositoryException;
import org.tui.codechallenge.services.exceptions.UserNotFoundException;
import org.tui.codechallenge.services.pojo.BranchDto;
import org.tui.codechallenge.services.pojo.RepositoryDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class  GitHubService implements IGitHubService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String GITHUB_API_URL = "https://api.github.com";

    public GitHubService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<RepositoryDto> getRepositoriesForUser(String username) {
        String url = GITHUB_API_URL + "/users/" + username + "/repos";
        ResponseEntity<String> response;
        try {
           response = restTemplate.getForEntity(url, String.class);
        }catch(HttpClientErrorException e){
            throw new UserNotFoundException("GitHub user not found: " + username);
        }
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode reposNode = objectMapper.readTree(response.getBody());
                List<RepositoryDto> nonForkedRepos = new ArrayList<>();
                for (JsonNode repoNode : reposNode) {
                    if (repoNode.get("fork")!=null && !repoNode.get("fork").asBoolean()) {
                        RepositoryDto repoDto = objectMapper.treeToValue(repoNode, RepositoryDto.class);
                        if(repoDto!=null) {
                            repoDto.setBranches(getBranchesWithLastCommitSha(username, repoDto.getName()));
                            nonForkedRepos.add(repoDto);
                        }
                    }
                }
                return nonForkedRepos;
            } catch (Exception e) {
                throw new ErrorProcessingRepositoryException("Error processing the repositories for user: " + username);
            }
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new UserNotFoundException("GitHub user not found: " + username);
        } else {
            throw new ErrorProcessingRepositoryException("Error fetching repositories for user: " + username);
        }
    }

    private List<BranchDto> getBranchesWithLastCommitSha(String username, String repoName) {
        String url = GITHUB_API_URL + "/repos/" + username + "/" + repoName + "/branches";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                return objectMapper.readValue(response.getBody(), new TypeReference<List<BranchDto>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Error parsing the branches for repository: " + repoName, e);
            }
        } else {
            return new ArrayList<>();
        }
    }
}

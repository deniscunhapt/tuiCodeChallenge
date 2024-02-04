package org.tui.codechallenge.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.tui.codechallenge.services.contracts.IGitHubService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.tui.codechallenge.utils.TestAssets.*;
import static org.mockito.Mockito.*;
import static org.tui.codechallenge.utils.TestAssets.getValidResponseEntityString2;

import org.tui.codechallenge.services.exceptions.ErrorProcessingRepositoryException;
import org.tui.codechallenge.services.exceptions.UserNotFoundException;
import org.tui.codechallenge.services.pojo.RepositoryDto;

import java.util.List;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class GitHubServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private IGitHubService gitHubService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        gitHubService = new GitHubService(restTemplate, objectMapper);
    }

    @Test
    @DisplayName("Get Repositories with user with processing repo problems-> Should return Error Processing Repository Exception")
    void getRepositories_shouldReturnErrorProcessingRepositoryException() {
        //Setup
        String username = "non_existent_user";
        when(restTemplate.getForEntity(anyString(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(testEmptyList(), HttpStatus.OK));

        //Test
        ErrorProcessingRepositoryException exception = assertThrows(
                ErrorProcessingRepositoryException.class,
                () -> gitHubService.getRepositoriesForUser(username));

        //Then
        assertEquals("Error processing the repositories for user: " + username, exception.getErrorMessage());

    }


    @Test
    @DisplayName("Get Repositories for non-existent user -> Should return User Not Found Exception")
    void getRepositories_shouldReturnUserNotFoundException() {
        //Setup
        String username = "non_existent_user";
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Test and Verify
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> gitHubService.getRepositoriesForUser(username),
                "Expected getRepositoriesForUser to throw UserNotFoundException, but it didn't"
        );

        assertEquals("GitHub user not found: " + username, exception.getErrorMessage());

    }

    @Test
    @DisplayName("Get Repositories with user whit http not found-> Should return Error Processing Repository Exception")
    void getRepositories_shouldReturnUserNotFoundExceptionWhenHttpStatusNotFound() {
        //Setup
        String username = "non_existent_user";
        when(restTemplate.getForEntity(anyString(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(testEmptyList(), HttpStatus.NOT_FOUND));

        //Test
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> gitHubService.getRepositoriesForUser(username));

        //Then
        assertEquals("GitHub user not found: " + username, exception.getErrorMessage());

    }

    @Test
    @DisplayName("Get Repositories with user with non expected HttpStatus> Should return Error Processing Repository Exception")
    void getRepositories_shouldReturnErrorProcessingRepositoryException2() {
        //Setup
        String username = "non_existent_user";
        when(restTemplate.getForEntity(anyString(), any(Class.class)))
                .thenReturn(new ResponseEntity<>(testEmptyList(), HttpStatus.BAD_GATEWAY));

        //Test
        ErrorProcessingRepositoryException exception = assertThrows(
                ErrorProcessingRepositoryException.class,
                () -> gitHubService.getRepositoriesForUser(username));

        //Then
        assertEquals("Error fetching repositories for user: " + username, exception.getErrorMessage());

    }

    @Test
    @DisplayName("Get Repositories with user with data> Should return the data correctly")
    public void getRepositories_ShouldReturnValidRepoList() throws Exception {
        //Setup
        String username = "testUser";
        ResponseEntity<String> mockResponse = new ResponseEntity<>(getValidResponseEntityString(), HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockResponse);

        JsonNode jsonNode = new ObjectMapper().readTree(getValidResponseEntityString());

        when(objectMapper.readTree(eq(getValidResponseEntityString()))).thenReturn(jsonNode);


        when(objectMapper.treeToValue(any(JsonNode.class), eq(RepositoryDto.class)))
                .thenReturn(getResponseDto(username));

        //Test
        List<RepositoryDto> result = gitHubService.getRepositoriesForUser(username);

        //Then
        assertEquals(1, result.size());
        assertEquals("non-forked-repo", result.get(0).getName());
        assertEquals(username, result.get(0).getOwner().getLogin());
    }

    @Test
    @DisplayName("Get Repositories with user with data with parse error> Should return Branch Parsing Exception")
    public void getRepositories_ShouldReturnBranchParsingThrowsException() throws Exception {
        //Setup
        String username = "testUser";

        ResponseEntity<String> repoResponse = new ResponseEntity<>(getValidResponseEntityString2(), HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(repoResponse);

        JsonNode repoNode = new ObjectMapper().readTree(getValidResponseEntityString2());
        when(objectMapper.readTree(getValidResponseEntityString2())).thenReturn(repoNode);

        RepositoryDto mockRepoDto = getResponseDto(username);
        when(objectMapper.treeToValue(any(JsonNode.class), eq(RepositoryDto.class))).thenReturn(mockRepoDto);

        when(objectMapper.readValue(anyString(), any(TypeReference.class)))
                .thenThrow(new RuntimeException("Failed to parse branch information"));

        //Test
        ErrorProcessingRepositoryException exception = assertThrows(
                ErrorProcessingRepositoryException.class,
                () -> gitHubService.getRepositoriesForUser(username));

        //Then
        assertEquals("Error processing the repositories for user: " + username, exception.getErrorMessage());

    }

    @Test
    @DisplayName("Get Repositories with user with status not ok in parse branches> Should return Empty Branches")
    public void getRepositories_ShouldReturnEmptyBranchesWhenStatusIsNotOK() throws Exception {
        {
            //Setup
            String username = "testUser";
            ResponseEntity<String> repoResponse = new ResponseEntity<>(getValidResponseEntityString2(), HttpStatus.OK);
            ResponseEntity<String> branchResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

            when(restTemplate.getForEntity(anyString(), eq(String.class)))
                    .thenReturn(repoResponse)
                    .thenReturn(branchResponse);

            JsonNode repoNode = new ObjectMapper().readTree(getValidResponseEntityString2());
            when(objectMapper.readTree(getValidResponseEntityString2())).thenReturn(repoNode);

            RepositoryDto mockRepoDto = getResponseDto(username);
            when(objectMapper.treeToValue(any(JsonNode.class), eq(RepositoryDto.class))).thenReturn(mockRepoDto);

            //Test
            List<RepositoryDto> result = gitHubService.getRepositoriesForUser(username);

            //Then
            assert result != null && !result.isEmpty() && result.get(0).getBranches().isEmpty(); // You may want to assert more details based on your implementation
        }


    }
}

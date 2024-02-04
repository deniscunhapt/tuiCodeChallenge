package org.tui.codechallenge.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.tui.codechallenge.services.contracts.IGitHubService;
import org.tui.codechallenge.services.exceptions.UserNotFoundException;
import org.tui.codechallenge.services.pojo.RepositoryDto;
import java.util.List;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CodeChallangeControllerV1 {

    private final IGitHubService gitHubService;


    @GetMapping("/repositories")
    public ResponseEntity<List<RepositoryDto>> getRepositories(@RequestParam String username) {
            List<RepositoryDto> repos = gitHubService.getRepositoriesForUser(username);
            return ResponseEntity.ok(repos);
    }


}

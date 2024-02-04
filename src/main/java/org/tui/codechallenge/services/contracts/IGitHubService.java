package org.tui.codechallenge.services.contracts;

import org.tui.codechallenge.services.pojo.BranchDto;
import org.tui.codechallenge.services.pojo.RepositoryDto;

import java.util.List;

public interface IGitHubService {
    List<RepositoryDto> getRepositoriesForUser(String username);
}

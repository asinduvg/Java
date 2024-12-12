package com.rockthejvm.service;

import com.rockthejvm.definitions.FindRepositoriesByUserIdPort;
import com.rockthejvm.definitions.FindUserByIdPort;
import com.rockthejvm.domain.GitHubUser;
import com.rockthejvm.domain.UserId;

import java.util.concurrent.ExecutionException;

public class FindGitHubUserSequentialService implements FindGitHubUserService {

    private final FindUserByIdPort findUserByIdPort;
    private final FindRepositoriesByUserIdPort findRepositoriesByUserIdPort;

    public FindGitHubUserSequentialService(
            FindUserByIdPort findUserByIdPort,
            FindRepositoriesByUserIdPort findRepositoriesByUserIdPort
    ) {
        this.findUserByIdPort = findUserByIdPort;
        this.findRepositoriesByUserIdPort = findRepositoriesByUserIdPort;
    }

    @Override
    public GitHubUser findGitHubUser(UserId userId) throws InterruptedException, ExecutionException {
        var user = findUserByIdPort.findUser(userId);
        var repositories = findRepositoriesByUserIdPort.findRepositories(userId);
        return new GitHubUser(user, repositories);
    }
}

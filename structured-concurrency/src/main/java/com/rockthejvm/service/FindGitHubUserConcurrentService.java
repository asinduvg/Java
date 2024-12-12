package com.rockthejvm.service;

import com.rockthejvm.definitions.FindRepositoriesByUserIdPort;
import com.rockthejvm.definitions.FindUserByIdPort;
import com.rockthejvm.domain.GitHubUser;
import com.rockthejvm.domain.UserId;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class FindGitHubUserConcurrentService implements FindGitHubUserService {
    private final FindUserByIdPort findUserByIdPort;
    private final FindRepositoriesByUserIdPort findRepositoriesByUserIdPort;

    public FindGitHubUserConcurrentService(
            FindUserByIdPort findUserByIdPort,
            FindRepositoriesByUserIdPort findRepositoriesByUserIdPort
    ) {
        this.findUserByIdPort = findUserByIdPort;
        this.findRepositoriesByUserIdPort = findRepositoriesByUserIdPort;
    }

    @Override
    public GitHubUser findGitHubUser(UserId userId) throws InterruptedException, ExecutionException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var user = executor.submit(() -> findUserByIdPort.findUser(userId));
            var repositories = executor.submit(() -> findRepositoriesByUserIdPort.findRepositories(userId));
            return new GitHubUser(user.get(), repositories.get()); // returns only when both futures completed - blocking call
        }
    }
}

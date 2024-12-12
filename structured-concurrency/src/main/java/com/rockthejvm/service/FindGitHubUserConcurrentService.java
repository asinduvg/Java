package com.rockthejvm.service;

import com.rockthejvm.definitions.FindRepositoriesByUserIdPort;
import com.rockthejvm.definitions.FindUserByIdPort;
import com.rockthejvm.domain.GitHubUser;
import com.rockthejvm.domain.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.StructuredTaskScope;

public class FindGitHubUserConcurrentService implements FindGitHubUserService {
    private final FindUserByIdPort findUserByIdPort;
    private final FindRepositoriesByUserIdPort findRepositoriesByUserIdPort;
    private static final Logger LOGGER = LoggerFactory.getLogger("GitHubApp");

    public FindGitHubUserConcurrentService(
            FindUserByIdPort findUserByIdPort,
            FindRepositoriesByUserIdPort findRepositoriesByUserIdPort
    ) {
        this.findUserByIdPort = findUserByIdPort;
        this.findRepositoriesByUserIdPort = findRepositoriesByUserIdPort;
    }

    private GitHubUser findUserExecutor(UserId userId) throws InterruptedException, ExecutionException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var user = executor.submit(() -> findUserByIdPort.findUser(userId));
            var repositories = executor.submit(() -> findRepositoriesByUserIdPort.findRepositories(userId));
            return new GitHubUser(user.get(), repositories.get()); // returns only when both futures completed - blocking call
        }
    }

    // structured concurrency
    //  - subtasks DO NOT outlive parents
    //  - (default case) if a child fails, the entire scope fails
    //  - if a child fails, we can say whether we want to cancel other tasks
    private GitHubUser findUserScoped(UserId userId) throws InterruptedException, ExecutionException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // shutdown on failure => if a child fails, the running tasks are cancelled

            // virtual threads
            var user = scope.fork(() -> findUserByIdPort.findUser(userId));
            var repositories = scope.fork(() -> findRepositoriesByUserIdPort.findRepositories(userId));

            // semantic blocking - mandatory before exiting the try resources
            scope.join();
            LOGGER.info("Both forked tasks completed");
            return new GitHubUser(user.get(), repositories.get());
        }
    }

    @Override
    public GitHubUser findGitHubUser(UserId userId) throws InterruptedException, ExecutionException {
        return findUserScoped(userId);
    }
}

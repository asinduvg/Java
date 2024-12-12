package com.rockthejvm;

import com.rockthejvm.definitions.FindRepositoriesByUserIdPort;
import com.rockthejvm.domain.*;
import com.rockthejvm.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StructuredConcurrencyDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger("GitHubApp");

    static void demoConcurrency() throws ExecutionException, InterruptedException {
        // "API"
        final GitHubRepository gitHubRepository = new GitHubRepository();
//        final FindGitHubUserService service = new FindGitHubUserSequentialService(gitHubRepository, gitHubRepository);
        final FindGitHubUserService service = new FindGitHubUserConcurrentService(gitHubRepository, gitHubRepository);
        final GitHubUser gitHubUser = service.findGitHubUser(new UserId(1L));
        LOGGER.info("GitHub user: {}", gitHubUser);
    }

    static void demoSuccessClosure() throws ExecutionException, InterruptedException {
        final GitHubRepository gitHubRepository = new GitHubRepository(); // "old" API
        final FindRepositoriesByUserIdCache cache = new FindRepositoriesByUserIdCache();
        final FindRepositoriesByUserIdPort githubFetch = new GitHubCachedRepository(gitHubRepository, cache);
        final UserId userId = new UserId(42L);
        final List<Repository> repos = githubFetch.findRepositories(userId);

        LOGGER.info("GitHub repos for user {}: {}", userId, repos);
    }

    static void demoTimeout() throws ExecutionException, InterruptedException {
        final GitHubRepository gitHubRepository = new GitHubRepository(); // "old" API
        final FindRepositoriesByUserIdWithTimeout findRepositoriesByUserIdWithTimeout = new FindRepositoriesByUserIdWithTimeout(gitHubRepository);

        final UserId userId = new UserId(1L);
        final List<Repository> repos = findRepositoriesByUserIdWithTimeout.findRepositories(userId, Duration.ofMillis(200));
        LOGGER.info("GitHub repos for user {}: {}", userId, repos);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        demoTimeout();
    }
}
package com.rockthejvm.service;

import com.rockthejvm.definitions.*;
import com.rockthejvm.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GitHubRepository implements FindUserByIdPort, FindRepositoriesByUserIdPort {
    private static final Logger LOGGER = LoggerFactory.getLogger("GitHubApp");

    @Override
    public List<Repository> findRepositories(UserId userId) throws InterruptedException, ExecutionException {
        LOGGER.info("Finding repositories for user with id '{}'", userId);
        delay(Duration.ofSeconds(1L));
//        throw new RuntimeException("web socket error");
        LOGGER.info("Repositories found for user '{}'", userId);
        return List.of(new Repository("raise4s", Visibility.PUBLIC, URI.create("https://github.com/rcardin/raise4s")), new Repository("sus4s", Visibility.PUBLIC, URI.create("https://github.com/rcardin/sus4s")));
    }

    @Override
    public User findUser(UserId userId) throws InterruptedException, ExecutionException {
        LOGGER.info("Finding user with id '{}'", userId);
        delay(Duration.ofMillis(500L));
        LOGGER.info("User '{}' found", userId);
        return new User(userId, new UserName("asindu"), new Email("hello@asindu.com"));
    }

    void delay(Duration duration) throws InterruptedException {
        Thread.sleep(duration);
    }
}

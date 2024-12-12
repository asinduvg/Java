package com.rockthejvm.service;

import com.rockthejvm.definitions.FindRepositoriesByUserIdPort;
import com.rockthejvm.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

public class FindRepositoriesByUserIdCache implements FindRepositoriesByUserIdPort {
    private static final Logger LOGGER = LoggerFactory.getLogger("FindRepositoriesByUserIdCache");
    private final Map<UserId, List<Repository>> cache = new HashMap<>();

    public FindRepositoriesByUserIdCache() {
        cache.put(
                new UserId(42L),
                List.of(
                        new Repository(
                                "rockthejvm.github.io",
                                Visibility.PUBLIC,
                                URI.create("https://github.com/rockthejvm/rockthejvm.github.io")
                        )
                )
        );
    }

    @Override
    public List<Repository> findRepositories(UserId userId) throws InterruptedException, ExecutionException {
        // Simulate access to a distributed cache (Redis?)
        delay(Duration.ofMillis(100L));
        final List<Repository> repositories = cache.get(userId);
        if (repositories == null) {
            LOGGER.info("No cached repositories found for user with id '{}'", userId);
            throw new NoSuchElementException(
                    "No cached repositories found for user with id '%s'".formatted(userId)
            );
        }
        return repositories;
    }

    public void addToCache(UserId userId, List<Repository> repositories) throws InterruptedException {
        // Simulate access to a distributed cache (Redis?)
        delay(Duration.ofMillis(100L));
        cache.put(userId, repositories);
    }

    void delay(Duration duration) throws InterruptedException {
        Thread.sleep(duration);
    }
}

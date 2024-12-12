package com.rockthejvm.service;

import com.rockthejvm.definitions.FindRepositoriesByUserIdPort;
import com.rockthejvm.domain.Repository;
import com.rockthejvm.domain.UserId;
import com.rockthejvm.policy.ShutdownOnResult;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class FindRepositoriesByUserIdWithTimeout {
    final FindRepositoriesByUserIdPort delegate;

    public FindRepositoriesByUserIdWithTimeout(FindRepositoriesByUserIdPort delegate) {
        this.delegate = delegate;
    }

    public List<Repository> findRepositories(UserId userId, Duration timeout) throws InterruptedException, ExecutionException {
        try (var scope = new ShutdownOnResult<List<Repository>>()) { // my own policy
            scope.fork(() -> delegate.findRepositories(userId));
            scope.fork(() -> {
                delay(timeout);
                throw new TimeoutException("Timeout of %s reached".formatted(timeout));
            });
            return scope.join().resultOrThrow();
        }
    }

    void delay(Duration duration) throws InterruptedException {
        Thread.sleep(duration);
    }

}

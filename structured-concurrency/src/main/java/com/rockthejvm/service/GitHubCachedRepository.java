package com.rockthejvm.service;

import com.rockthejvm.definitions.FindRepositoriesByUserIdPort;
import com.rockthejvm.domain.Repository;
import com.rockthejvm.domain.UserId;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class GitHubCachedRepository implements FindRepositoriesByUserIdPort {
    private final FindRepositoriesByUserIdPort repository;
    private final FindRepositoriesByUserIdCache cache;

    public GitHubCachedRepository(
            FindRepositoriesByUserIdPort repository,
            FindRepositoriesByUserIdCache cache
    ) {
        this.repository = repository;
        this.cache = cache;
    }

    /*
        find it in the cache -> if the cache gets you something CANCEL the other one
        call the old API +
     */

    @Override
    public List<Repository> findRepositories(UserId userId) throws InterruptedException, ExecutionException {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<List<Repository>>()) {
            scope.fork(() -> cache.findRepositories(userId)); // fast
            scope.fork(
                    () -> {
                        final List<Repository> repositories = repository.findRepositories(userId); // call the old API
                        cache.addToCache(userId, repositories); // put it in the cache if you fetched something
                        return repositories;
                    }
            );
            return scope.join().result();
            /*
                You can also map the throwable to something else you want to throw if everyone fails
                scope.join.result(t -> some other exception)
             */
        }
    }
}

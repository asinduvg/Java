package com.rockthejvm.definitions;

import com.rockthejvm.domain.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface FindRepositoriesByUserIdPort {
    List<Repository> findRepositories(UserId userId) throws InterruptedException, ExecutionException;
}

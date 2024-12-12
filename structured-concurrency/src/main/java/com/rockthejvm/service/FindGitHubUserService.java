package com.rockthejvm.service;

import com.rockthejvm.domain.*;

import java.util.concurrent.ExecutionException;

public interface FindGitHubUserService {
    GitHubUser findGitHubUser(UserId userId) throws InterruptedException, ExecutionException;
}

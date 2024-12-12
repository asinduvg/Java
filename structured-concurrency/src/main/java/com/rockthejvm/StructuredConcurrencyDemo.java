package com.rockthejvm;

import com.rockthejvm.domain.*;
import com.rockthejvm.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class StructuredConcurrencyDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger("GitHubApp");
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // "API"
        final GitHubRepository gitHubRepository = new GitHubRepository();
//        final FindGitHubUserService service = new FindGitHubUserSequentialService(gitHubRepository, gitHubRepository);
        final FindGitHubUserService service = new FindGitHubUserConcurrentService(gitHubRepository, gitHubRepository);
        final GitHubUser gitHubUser = service.findGitHubUser(new UserId(1L));
        LOGGER.info("GitHub user: {}", gitHubUser);
    }
}
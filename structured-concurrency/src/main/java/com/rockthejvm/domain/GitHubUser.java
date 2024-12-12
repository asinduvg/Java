package com.rockthejvm.domain;

import java.util.List;

public record GitHubUser(User user, List<Repository> repositories) {
}

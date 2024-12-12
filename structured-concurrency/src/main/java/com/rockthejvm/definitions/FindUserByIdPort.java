package com.rockthejvm.definitions;

import com.rockthejvm.domain.*;

import java.util.concurrent.ExecutionException;

public interface FindUserByIdPort {
    User findUser(UserId userId) throws InterruptedException, ExecutionException;
}

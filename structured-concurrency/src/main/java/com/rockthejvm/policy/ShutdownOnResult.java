package com.rockthejvm.policy;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ShutdownOnResult<T> extends StructuredTaskScope<T> {
    private final Lock lock = new ReentrantLock();
    private T firstResult;
    private Throwable firstException;

    @Override
    protected void handleComplete(Subtask<? extends T> subtask) {
        switch (subtask.state()) {
            case FAILED -> {
                lock.lock();
                try {
                    if (firstException == null) {
                        firstException = subtask.exception();
                        shutdown();
                    }
                } finally {
                    lock.unlock();
                }
            }
            case SUCCESS -> {
                lock.lock();
                try {
                    if (firstResult == null) {
                        firstResult = subtask.get();
                        shutdown();
                    }
                } finally {
                    lock.unlock();
                }
            }
            case UNAVAILABLE -> super.handleComplete(subtask);
        }
    }

    @Override
    public ShutdownOnResult<T> join() throws InterruptedException {
        super.join();
        return this;
    }

    public T resultOrThrow() throws ExecutionException {
        if (firstException != null) {
            throw new ExecutionException(firstException);
        }
        return firstResult;
    }
}

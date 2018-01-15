package com.smartscity.ratelimit.inmemory.concurrent;

import com.smartscity.ratelimit.core.limiter.concurrent.Baton;

import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class InMemoryBaton implements Baton {

        private final Semaphore semaphore;
        private final int weight;
        private boolean acquired;

        InMemoryBaton(Semaphore semaphore, int weight) {
            this.semaphore = semaphore;
            this.weight = weight;
            acquired = true;
        }

        InMemoryBaton(int weight) {
            this.semaphore = null;
            this.weight = weight;
        }

        @Override
        public void release() {
            if (semaphore == null) {
                throw new IllegalStateException();
            }
            semaphore.release(weight);
        }

        public boolean hasAcquired() {
            return acquired;
        }

        public <T> Optional<T> get(Supplier<T> action) {
            if (!acquired) {
                return Optional.empty();
            }

            try {
                return Optional.of(action.get());
            } finally {
                release();
                acquired = false;
            }
        }

        @Override
        public void doAction(Runnable action) {
            if (!acquired) {
                return;
            }

            try {
                action.run();
            } finally {
                release();
                acquired = false;
            }
        }
    }

package br.com.severo.retry;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class RetryProcess {

    private static final Duration DEFAULT_DELAY = Duration.ofSeconds(3);

    private final Set<Class<? extends Throwable>> allowedConditions;
    private final Set<Class<? extends Throwable>> abortConditions;

    private Duration maxDelay;
    private Duration delay;
    private int maxAttempts;


    public RetryProcess() {
        this.allowedConditions = new HashSet<>();
        this.abortConditions = new HashSet<>();
        this.delay = DEFAULT_DELAY;
    }


    public RetryProcess delay(Duration delay) {
        this.delay = delay;
        return this;
    }


    public RetryProcess maxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
        return this;
    }


    public RetryProcess allowedIn(Class<? extends Throwable> failure) {
        allowedConditions.add(failure);
        return this;
    }


    public RetryProcess abortIf(Class<? extends Throwable> failure) {
        abortConditions.add(failure);
        return this;
    }


    public RetryProcess maxDelay(Duration maxDelay) {
        this.maxDelay = maxDelay;
        return this;
    }


    public <R> Optional<R> process(CallableRetry<R> retry) throws RetryInterruptedException {
        Optional<Object> returnRetry = Optional.empty();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return Optional.of(retry.call());
            } catch (Exception ex) {
                returnRetry = Optional.ofNullable(ex.getMessage());

                if (abortConditions.contains(ex.getClass())) {
                    break;
                }

                if (isAllowedRetry(attempt, ex)) {
                    Duration delayMillis = calcDelay();
                    sleep(delayMillis);
                }
            }
        }
        return (Optional<R>) returnRetry;
    }

    private boolean isAllowedRetry(int attempt, Throwable ex) {
        return (allowedConditions.isEmpty() || allowedConditions.contains(ex.getClass())) && attempt < maxAttempts;
    }

    private Duration calcDelay() {
        Duration delayMillis = delay;

        if (Objects.nonNull(maxDelay) && maxDelay.toMillis() > 0) {
            delayMillis = min(delayMillis, maxDelay);
        }
        return delayMillis;
    }

    private Duration min(Duration duration1, Duration duration2) {
        if (duration1.compareTo(duration2) < 0) {
            return duration1;
        } else {
            return duration2;
        }
    }

    private void sleep(Duration timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RetryInterruptedException(e);
        }
    }
}

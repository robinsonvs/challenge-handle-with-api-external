package br.com.severo.retry;

public interface CallableRetry<V> {
    V call();
}

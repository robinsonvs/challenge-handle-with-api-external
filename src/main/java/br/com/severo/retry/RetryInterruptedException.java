package br.com.severo.retry;

public class RetryInterruptedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RetryInterruptedException(Throwable cause) {
        super(cause);
    }
}

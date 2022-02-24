package br.com.severo.retry;

public class RetryBreakException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RetryBreakException(String message) {
        super(message);
    }

}

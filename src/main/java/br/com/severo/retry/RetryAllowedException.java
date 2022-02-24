package br.com.severo.retry;

public class RetryAllowedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RetryAllowedException(String message) {
        super(message);
    }

}

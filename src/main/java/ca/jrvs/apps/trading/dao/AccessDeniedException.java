package ca.jrvs.apps.trading.dao;

/**
 * Class created to substitute specific error
 * error 403: forbidden error
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String msg) {
        super(msg);
    }

    public AccessDeniedException(String msg, Exception ex) {
        super(msg, ex);
    }
}

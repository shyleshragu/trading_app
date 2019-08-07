package ca.jrvs.apps.trading.dao;

/**
 * Class created to substitute specific error
 * error 404: ticker not found
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String msg) {
        super(msg);
    }

    public ResourceNotFoundException(String msg, Exception ex) {
        super(msg, ex);
    }
}

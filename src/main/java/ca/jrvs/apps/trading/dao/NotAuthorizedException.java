package ca.jrvs.apps.trading.dao;

/**
 * Class created to substitute specific error
 * error 401: Unorthorized error
 */
public class NotAuthorizedException extends RuntimeException {

    public NotAuthorizedException(String msg){
        super(msg);
    }

    public NotAuthorizedException(String msg, Exception ex){
        super(msg, ex);
    }
}

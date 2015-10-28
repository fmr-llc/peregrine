package com.alliancefoundry.publisher;

/**
 * Created by Paul Bernard on 10/26/15.
 */
public class PublisherException extends Exception {

    public PublisherException() {}

    public PublisherException(String message) {
        super(message);
    }

    public PublisherException(Throwable cause) {
        super(cause);
    }

    public PublisherException(String message, Throwable cause) {
        super(message, cause);
    }

    public PublisherException(String message, Throwable cause,
                        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

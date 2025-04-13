package com.urlShortener.demo.errorhadnling.exceptions;

public class ContentUnavailableInvalidUserException extends RuntimeException {
    public ContentUnavailableInvalidUserException(String message){
        super(message);
    }

    public ContentUnavailableInvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.urlShortener.demo.errorhadnling;

import org.springframework.stereotype.Component;

@Component
public class ErrorMessages {
    private ErrorMessages() {
    }

    public static final String TRY_BEFORE_AUTHENTICATION = "you must login first to do that" ;
    public static final String RECEIVED_EMPTY_ON_EXPECTED_VALUE = "Provided value cannot be empty" ;
    public static final String INVALID_USER = "Please provide the correct user or a value related to your account" ;
    public static final String VALUE_DOESNT_EXIST = "We couldn't find the correct value that you were looking for  in our database" ;
    public static final String ACTIVATE_ACCOUNT = "Please activate your account before that" ;



}

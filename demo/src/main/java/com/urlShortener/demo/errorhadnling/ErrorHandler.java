package com.urlShortener.demo.errorhadnling;

import com.urlShortener.demo.errorhadnling.exceptions.ContentUnavailableInvalidUserException;
import com.urlShortener.demo.errorhadnling.exceptions.EmailException;
import com.urlShortener.demo.errorhadnling.exceptions.InvalidJwtException;
import com.urlShortener.demo.errorhadnling.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<String> handleInvalidJwt(InvalidJwtException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(EmailException.class)
    public ResponseEntity<String> handleEmailException(EmailException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ContentUnavailableInvalidUserException.class)
    public ResponseEntity<String> hamdleContentUnavailableInvalidUserException(ContentUnavailableInvalidUserException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

}

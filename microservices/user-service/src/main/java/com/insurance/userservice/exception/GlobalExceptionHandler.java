package com.insurance.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException; // <-- NEW IMPORT
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {

        return new ResponseEntity<>(
                Map.of("message", "Invalid username or password."),
                HttpStatus.UNAUTHORIZED
        );
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleAllOtherExceptions(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(
                Map.of("general", "An unexpected internal server error occurred. Check the service logs."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
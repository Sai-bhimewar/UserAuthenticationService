package com.scaler.userauthservice.controllers;

import com.scaler.userauthservice.exceptions.PasswordMismatchException;
import com.scaler.userauthservice.exceptions.UserAlreadySignedException;
import com.scaler.userauthservice.exceptions.UserNotRegisteredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler({PasswordMismatchException.class, UserNotRegisteredException.class, UserAlreadySignedException.class})
    public ResponseEntity<String> handleException(Exception exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

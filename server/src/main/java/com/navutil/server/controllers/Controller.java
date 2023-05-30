package com.navutil.server.controllers;

import com.navutil.server.exceptions.AlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base of controller classes, if extended then any
 * {@code ConstraintViolation},
 * {@code MethodArgumentNotValid},
 * {@code AlreadyExistsException}, thrown by controller method will be handled, resulting BAD REQUEST
 */
@org.springframework.stereotype.Controller
public class Controller {
    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class,
            org.hibernate.exception.ConstraintViolationException.class, AlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleException(Exception e) {
        return new ResponseEntity<>("Invalid request: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

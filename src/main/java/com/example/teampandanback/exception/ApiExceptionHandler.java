package com.example.teampandanback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handle(ApiRequestException ex) {

        ApiException apiException = ApiException.builder()
                                        .httpStatus(HttpStatus.BAD_REQUEST)
                                        .message(ex.getMessage())
                                        .build();

        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }
}

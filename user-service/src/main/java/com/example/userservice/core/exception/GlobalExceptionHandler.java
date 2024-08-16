package com.example.userservice.core.exception;

import com.example.userservice.core.utils.ApiUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiUtils.ApiError> handleCustomException(CustomException ex) {
        ApiUtils.ApiError apiResult = ex.body();
        return new ResponseEntity<>(apiResult, HttpStatus.BAD_REQUEST);
    }
}

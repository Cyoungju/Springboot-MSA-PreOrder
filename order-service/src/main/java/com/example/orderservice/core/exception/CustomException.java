package com.example.orderservice.core.exception;


import com.example.orderservice.core.utils.ApiUtils;

public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }

    public ApiUtils.ApiError body(){
        return ApiUtils.error(getMessage());
    }
}

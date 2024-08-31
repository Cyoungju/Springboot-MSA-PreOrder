package com.example.paymentservice.core.exception;


import com.example.paymentservice.core.utils.ApiUtils;

public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }

    public ApiUtils.ApiError body(){
        return ApiUtils.error(getMessage());
    }
}

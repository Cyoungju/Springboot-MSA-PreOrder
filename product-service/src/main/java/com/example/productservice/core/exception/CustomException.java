package com.example.productservice.core.exception;


import com.example.productservice.core.utils.ApiUtils;

public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }

    public ApiUtils.ApiError body(){
        return ApiUtils.error(getMessage());
    }
}

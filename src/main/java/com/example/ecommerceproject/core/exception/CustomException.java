package com.example.ecommerceproject.core.exception;

import com.example.ecommerceproject.core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }

    public ApiUtils.ApiError body(){
        return ApiUtils.error(getMessage());
    }
}

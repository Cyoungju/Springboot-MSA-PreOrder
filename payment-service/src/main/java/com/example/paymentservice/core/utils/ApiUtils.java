package com.example.paymentservice.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ApiUtils {

    public static <T> ApiResult<T> success(T message){
        return new ApiResult<T>(message);
    }

    public static ApiError error(String message){
        return new ApiError(message);
    }

    // ** JSON으로 반환해야할 데이터.
    @AllArgsConstructor
    @Getter
    public static class ApiResult<T>{
        private final T message; // 반환할 실제 데이터

        public String toString(){
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("message", message)
                    .toString();
        }
    }

    @AllArgsConstructor @Getter
    public static class ApiError{
        private final String error;

        public String toString(){
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("error", error)
                    .toString();
        }
    }
}

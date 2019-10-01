package com.inetti.matchnight.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * default response wrapper inspired by
 * https://github.com/omniti-labs/jsend
 */
public class BaseResponse<T> {

    @JsonProperty("status")
    private ResponseStatus status;

    @JsonProperty("error")
    private ResponseError error;

    @JsonProperty("data")
    private Map<String, T> data;


    private BaseResponse(String key, T data) {
        this.data = Collections.singletonMap(key, data);
        this.status = ResponseStatus.SUCCESS;

    }

    private BaseResponse(ResponseStatus status) {
        this.data = null;
        this.status = status;

    }

    private BaseResponse(Integer errorCode, String message, ResponseStatus status) {
        this.error = new ResponseError(errorCode, message);
        this.status = status;
    }

    public ResponseError getError() {
        return error;
    }

    public static <T> BaseResponse<T> with(String key, T data) {

       return new BaseResponse<>(Objects.requireNonNull(key, "cannot create a response with a null key"),
               Objects.requireNonNull(data, "cannot create a response with a null content"));
    }

    public static <T> BaseResponse<T> with(T data) {

        return new BaseResponse<>("content",
                Objects.requireNonNull(data, "cannot create a response with a null content"));
    }

    public static <T> BaseResponse<T> success() {

        return new BaseResponse<>(ResponseStatus.SUCCESS);
    }

    public static BaseResponse withError(Integer errorCode, String message) {
        return new BaseResponse(Objects.requireNonNull(errorCode, "cannot create a response with a null errorCode"),
                Objects.requireNonNull(message, "cannot create a response with a null message"),
                ResponseStatus.ERROR);
    }

    public static BaseResponse fail(Integer errorCode, String message) {
        return new BaseResponse(Objects.requireNonNull(errorCode, "cannot create a response with a null errorCode"),
                Objects.requireNonNull(message, "cannot create a response with a null message"),
                ResponseStatus.FAIL);
    }

    public static class ResponseData<T> {
        private T response;

        public ResponseData(T response) {
            this.response = response;
        }

        public T getResponse() {
            return response;
        }

        @Override
        public String toString() {
            return "ResponseData{" +
                    "response=" + response +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ResponseData<?> that = (ResponseData<?>) o;
            return Objects.equals(response, that.response);
        }

        @Override
        public int hashCode() {
            return Objects.hash(response);
        }
    }


    public static class ResponseError {

        @JsonProperty("code")
        private Integer code;

        @JsonProperty("message")
        private String message;

        public ResponseError(Integer code, String message) {
            this.code = Objects.requireNonNull(code);
            this.message = Objects.requireNonNull(message);
        }

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ResponseError that = (ResponseError) o;
            return Objects.equals(code, that.code) &&
                    Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, message);
        }

        @Override
        public String toString() {
            return "ResponseError{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    '}';
        }
    }


    public enum ResponseStatus {
        SUCCESS, //successful request
        FAIL, //error due to the user input
        ERROR //error on the server side
    }



}

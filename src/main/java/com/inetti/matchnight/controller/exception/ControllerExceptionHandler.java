package com.inetti.matchnight.controller.exception;

import com.inetti.matchnight.data.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({InvalidInputArgumentException.class, UserAlreadyExistException.class, ConstraintViolationException.class})
    public final ResponseEntity<BaseResponse> handleException(Exception ex, WebRequest request) {
        if (ex instanceof InvalidInputArgumentException) {
            return handleInvalidArgumentException((InvalidInputArgumentException) ex);
        } else if (ex instanceof UserAlreadyExistException){
            return handleUserAlreadyExistException((UserAlreadyExistException) ex);
        } else if(ex instanceof ConstraintViolationException) {
            return handleInvalidArgumentException(ex);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponse.withError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    private final ResponseEntity<BaseResponse> handleInvalidArgumentException(Exception ex) {
        return ResponseEntity.badRequest().body(BaseResponse.withError(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    private final ResponseEntity<BaseResponse> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(BaseResponse.withError(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }
}

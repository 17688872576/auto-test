package com.lzb.tester.common;

import com.lzb.tester.dto.BaseResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class BadRequestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        if (result.hasErrors()) {
            BaseResult baseResult = BaseResult.parameterErr();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(baseResult);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getAllErrors().toString());
    }
}

package com.ccsw.tutorial.common;

import com.ccsw.tutorial.client.ClientException;
import com.ccsw.tutorial.loan.LoanException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(LoanException.class)
    public ResponseEntity<Map<String, String>> handleLoanException(LoanException mes) {

        Map<String, String> response = new HashMap<>();

        response.put("message", mes.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<Map<String, String>> handleClientException(ClientException mes) {

        Map<String, String> response = new HashMap<>();

        response.put("message", mes.getMessage());

        return ResponseEntity.badRequest().body(response);
    }
}
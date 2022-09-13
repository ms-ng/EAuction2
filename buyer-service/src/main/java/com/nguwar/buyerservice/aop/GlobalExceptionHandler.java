package com.nguwar.buyerservice.aop;

import com.nguwar.buyerservice.exception.ErrorDetails;
import com.nguwar.buyerservice.exception.ErrorMessageDTO;
import com.nguwar.buyerservice.exception.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorDetails handleExceptions(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDate.now(),ex.getMessage(), "Exception");
        return errorDetails;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<ErrorMessageDTO> validationErrorDetails = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorMessageDTO(error.getObjectName(),error.getField(),error.getDefaultMessage(),error.getRejectedValue().toString()))
                .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse(status.name(), status.value(),validationErrorDetails);
        return new ResponseEntity<>(response,status );

    }



}
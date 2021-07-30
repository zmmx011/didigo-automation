package com.invenia.excel.web.controller;

import com.invenia.excel.web.dto.ErrorResponse;
import java.nio.file.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriverException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException e) {
    log.error(e.getMessage(), e);
    ErrorResponse response = ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(e.toString()).build();
    return ResponseEntity.internalServerError().body(response);
  }

  @ExceptionHandler(WebDriverException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(WebDriverException e) {
    log.error(e.getMessage(), e);
    ErrorResponse response = ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(e.toString()).build();
    return ResponseEntity.internalServerError().body(response);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
    log.error(e.getMessage(), e);
    ErrorResponse response = ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(e.toString()).build();
    return ResponseEntity.internalServerError().body(response);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error(e.getMessage(), e);
    ErrorResponse response = ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(e.toString()).build();
    return ResponseEntity.internalServerError().body(response);
  }
}

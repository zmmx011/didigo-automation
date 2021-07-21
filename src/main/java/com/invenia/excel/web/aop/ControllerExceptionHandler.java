package com.invenia.excel.web.aop;

import java.nio.file.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> handleBadRequestException(final RuntimeException e) {
    log.error(e.getLocalizedMessage(), e);
    return ResponseEntity.badRequest().body(e.getLocalizedMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(final AccessDeniedException e) {
    log.error(e.getLocalizedMessage(), e);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getLocalizedMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleException(final Exception e) {
    log.error(e.getLocalizedMessage(), e);
    return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
  }
}

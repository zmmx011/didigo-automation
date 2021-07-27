package com.invenia.excel.web.controller;

import com.invenia.excel.web.entity.BatchEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test")
public class TestController {
  @GetMapping("/1/{condition}")
  public ResponseEntity<String> test1(@PathVariable String condition) {
    getName(condition);
    return ResponseEntity.ok().build();
  }

  private String getName(String condition) {
    if (condition.equals("1")) {
      throw new RuntimeException("TEST");
    }
    return condition;
  }
}

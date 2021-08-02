package com.invenia.excel.web.controller;

import com.invenia.excel.batch.BatchScheduler;
import com.invenia.excel.web.entity.BatchEnvironment;
import com.invenia.excel.web.repository.BatchEnvironmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class BatchSettingController {
  private final BatchScheduler scheduler;
  private final BatchEnvironmentRepository batchEnvironmentRepository;

  @GetMapping("/settings/batch/{type}")
  public ResponseEntity<BatchEnvironment> batchSettings(@PathVariable("type") String type) {
    return batchEnvironmentRepository
        .findById(type)
        .map(env -> ResponseEntity.ok().body(env))
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping(value = "/settings/batch")
  public ResponseEntity<?> batchSettings(@RequestBody BatchEnvironment env) {
    batchEnvironmentRepository.save(env);
    scheduler.clear();
    scheduler.start();
    return ResponseEntity.ok().body(env);
  }
}

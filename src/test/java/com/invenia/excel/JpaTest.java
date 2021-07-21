package com.invenia.excel;

import com.invenia.excel.web.repository.BatchJobExecutionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
public class JpaTest {

  private final BatchJobExecutionRepository batchJobExecutionRepository;

  public JpaTest(BatchJobExecutionRepository batchJobExecutionRepository) {
    this.batchJobExecutionRepository = batchJobExecutionRepository;
  }

  @Test
  public void test() {
    System.out.println(batchJobExecutionRepository.findAll());
  }
}

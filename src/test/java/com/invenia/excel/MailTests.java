package com.invenia.excel;

import com.invenia.excel.batch.BatchMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
public class MailTests {
  @Autowired
  private BatchMail batchMail;

}









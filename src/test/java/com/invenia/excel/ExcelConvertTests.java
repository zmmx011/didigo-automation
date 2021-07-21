package com.invenia.excel;

import com.invenia.excel.converter.ExcelConverter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExcelConvertTests {

  static Logger log = LoggerFactory.getLogger(ExcelConvertTests.class);

  @Autowired
  private ExcelConverter excelConverter;

  @Test
  void 수주변환() throws Exception {
    excelConverter.contractOrderConvert("mall");
  }
}

package com.invenia.excel;

import com.invenia.excel.converter.ExcelConverter;
import com.invenia.excel.converter.dto.Item;
import com.invenia.excel.converter.dto.ItemCode;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.jxls.common.Context;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.XLSReader;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

@SpringBootTest
class ExcelConvertTests {

  static Logger log = LoggerFactory.getLogger(ExcelConvertTests.class);

  @Autowired
  private ExcelConverter excelConverter;

  @Test
  void makeItemExcel() throws Exception {
    excelConverter.init();
    excelConverter.itemConvert("mall");
    //excelConverter.unregisteredCustomerCheck("mall");
  }
}

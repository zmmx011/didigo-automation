package com.invenia.excel;

import com.invenia.excel.converter.ExcelConverter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Stream;

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

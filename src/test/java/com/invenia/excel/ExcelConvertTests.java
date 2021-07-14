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
	void clearDirectory() throws Exception {
		try (Stream<Path> walk = Files.walk(Paths.get("C:/excel/output/"))) {
			walk.sorted(Comparator.reverseOrder())
					.forEach(x-> {
						try {
							Files.delete(x);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
		}
	}

	@Test
	void a() {
		System.out.println(LocalDate.now().minusDays(1));
		System.out.println(LocalDate.now().minusDays(8));
	}
}

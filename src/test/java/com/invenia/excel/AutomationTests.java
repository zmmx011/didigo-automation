package com.invenia.excel;

import com.invenia.excel.batch.BatchJob;
import com.invenia.excel.batch.BatchJobLauncher;
import com.invenia.excel.selenium.Automation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AutomationTests {
	@Autowired
	private Automation automation;

	@BeforeEach
	void init() {
		automation.setup();
	}

	@AfterEach
	void close() {
		automation.closeAutomation();
	}

	@Test
	void run() throws InterruptedException {
		automation.runContractOrderDownload();
	}
}

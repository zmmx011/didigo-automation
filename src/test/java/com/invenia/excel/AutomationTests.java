package com.invenia.excel;

import com.invenia.excel.batch.BatchJob;
import com.invenia.excel.batch.BatchJobLauncher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AutomationTests {
	@Autowired
	private BatchJobLauncher launcher;

	@Test
	void runAll() {
		launcher.executeJob(BatchJob::allProcessJob);
	}

	@Test
	void runItem() {
		launcher.executeJob(BatchJob::itemCodeJob);
	}

	@Test
	void runCustomer() {
		launcher.executeJob(BatchJob::customerCheckJob);
	}

	@Test
	void runContract() {
		launcher.executeJob(BatchJob::contractOrderJob);
	}

	@Test
	void runPurchase() {
		launcher.executeJob(BatchJob::purchaseOrderJob);
	}
}

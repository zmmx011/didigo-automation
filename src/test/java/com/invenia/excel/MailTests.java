package com.invenia.excel;

import com.invenia.excel.batch.BatchMail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
public class MailTests {

	@Autowired
	private BatchMail batchMail;

	@Test
	void sendMail() {
		batchMail.sendMail();
	}
}

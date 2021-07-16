package com.invenia.excel.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchMail {
	private final JavaMailSender mailSender;

	public void sendMail() {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("damu@inveniacorp.com");
		message.setTo("damu@inveniacorp.com");
		message.setSubject("Test");
		message.setText("Test");
		mailSender.send(message);
	}
}

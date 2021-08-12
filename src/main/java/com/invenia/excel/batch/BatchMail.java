package com.invenia.excel.batch;

import com.invenia.excel.batch.config.MailConfig;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchMail {

  private final JavaMailSender mailSender;
  private final MailConfig mailConfig;

  public void sendJobFailureMail(JobExecution jobExecution) {
    log.info("자동화 실패 메일 발송 시작");
    String htmlPath = "mail/배치 실패.html";
    ClassPathResource htmlResource = new ClassPathResource(htmlPath);

    if (!htmlResource.exists()) {
      log.error("Invalid filePath : {}", htmlPath);
      throw new IllegalArgumentException();
    }
    log.debug("{} exists : {}", htmlPath, htmlResource.exists());
    try {
      MimeMessage message = mailSender.createMimeMessage();
      // 본문
      Document doc = Jsoup.parse(htmlResource.getInputStream(), "UTF-8", "");
      String jobName = jobExecution.getJobInstance().getJobName();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      // 배치 번호
      doc.getElementById("no").text(String.valueOf(jobExecution.getId()));
      // 배치 종류
      doc.getElementById("jobName").text(jobName);
      // 시작 시간
      doc.getElementById("startTime").text(dateFormat.format(jobExecution.getStartTime()));
      // 종료 시간
      doc.getElementById("endTime").text(dateFormat.format(jobExecution.getEndTime()));
      // 조회 기간
      String period =
          String.join(
              " ~ ",
              jobExecution.getJobParameters().getString("fromDateStr"),
              jobExecution.getJobParameters().getString("toDateStr"));
      doc.getElementById("period").text(period);
      // 실패 스텝
      String failedStepName =
          Objects.requireNonNull(
                  jobExecution.getStepExecutions().parallelStream()
                      .filter(
                          step ->
                              step.getExitStatus()
                                  .getExitCode()
                                  .equals(ExitStatus.FAILED.getExitCode()))
                      .findFirst()
                      .orElse(null))
              .getStepName();
      doc.getElementById("failedStep").text(failedStepName);
      message.setText(doc.toString(), "UTF-8", "html");
      // 제목
      String subject = "[ERP Automation] " + jobName + " 실패 알림";
      message.setSubject(subject, "UTF-8");

      this.sendMimeMail(message);
      log.info("자동화 실패 메일 발송 완료");
    } catch (MessagingException | IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }

  public void sendUnregisteredCustomerMail(String filePath, int size) {
    log.info("미등록 거래처 메일 발송 시작");

    String htmlPath = "mail/배치 실패.html";
    ClassPathResource htmlResource = new ClassPathResource(htmlPath);

    if (!htmlResource.exists()) {
      log.error("Invalid filePath : {}", htmlPath);
      throw new IllegalArgumentException();
    }
    log.debug("{} exists : {}", htmlPath, htmlResource.exists());

    try {
      // 본문
      MimeMessage message = mailSender.createMimeMessage();
      MimeBodyPart htmlPart = new MimeBodyPart();
      Document doc = Jsoup.parse(htmlResource.getInputStream(), "UTF-8", "");
      doc.getElementById("count").text(String.valueOf(size)); // 미등록 거래처 Count
      htmlPart.setText(doc.toString(), "UTF-8", "html");

      // 파일첨부
      MimeBodyPart filePart = new MimeBodyPart();
      FileDataSource fileDataSource = new FileDataSource(filePath);
      filePart.setDataHandler(new DataHandler(fileDataSource));
      filePart.setFileName(fileDataSource.getName());

      Multipart multipart = new MimeMultipart();
      multipart.addBodyPart(htmlPart);
      multipart.addBodyPart(filePart);
      message.setContent(multipart);

      // 제목
      String subject = "[ERP Automation] 미등록 거래처 알림";
      message.setSubject(subject, "UTF-8");

      this.sendMimeMail(message);
      log.info("미등록 거래처 메일 발송 완료");
    } catch (MessagingException | IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }

  private void sendMimeMail(MimeMessage message) throws MessagingException {
    // 보내는 사람
    message.setFrom("itsecurity@inveniacorp.com");
    // 받는 사람
    message.addRecipients(RecipientType.TO, mailConfig.getDefaultRecipients());
    // 참조
    message.addRecipients(RecipientType.CC, mailConfig.getDefaultCarbonCopies());
    log.debug("메일 발송 대상 : {}", mailConfig);
    mailSender.send(message);
  }
}


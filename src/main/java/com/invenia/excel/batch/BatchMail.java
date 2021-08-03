package com.invenia.excel.batch;

import com.invenia.excel.batch.config.BatchConfig;
import java.text.SimpleDateFormat;
import java.util.Objects;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchMail {

  private final JavaMailSender mailSender;
  private final BatchConfig batchConfig;

  public void sendJobFailureMail(JobExecution jobExecution) {
    if (!batchConfig.getRunSendMail()) {
      return;
    }
    log.info("자동화 실패 메일 발송 시작");

    // 템플릿
    String html =
        "<table style=\"font-size: 12pt;\">\n"
            + "  <tbody>\n"
            + "  <tr>\n"
            + "    <td style=\"padding-left:20px\">\n"
            + "      <div style=\"border:0 solid #FFFFFF;width:660px;padding:10px 0 10px 15px;\n"
            + "      background-color:#4F5258;font-family:Trebuchet MS,serif;color:#FFFFFF;\">\n"
            + "        <span style=\"font-size:14px;font-weight:bold;\">Automation Failure</span>\n"
            + "      </div>\n"
            + "    </td>\n"
            + "  </tr>\n"
            + "  <tr>\n"
            + "    <td style=\"padding: 30px 0 0 30px;\">\n"
            + "      <div style=\"font-family:Trebuchet MS, arial, 맑은 고딕,serif;font-size:12px;color:#666666;\">\n"
            + "        <p>배치 번호 : <span id=\"no\"></span></p>\n"
            + "        <p>배치 종류 : <span id=\"jobName\"></span></p>\n"
            + "        <p>시작 시간 : <span id=\"startTime\"></span></p>\n"
            + "        <p>종료 시간 : <span id=\"endTime\"></span></p>\n"
            + "        <p>조회 기간 : <span id=\"period\"></span></p>\n"
            + "        <p>실패 스텝 : <span id=\"failedStep\"></span></p>\n"
            + "        <br/>\n"
            + "        <p><a href=\"http://192.168.11.158:8080/\" target=\"_blank\">관리 페이지 바로가기</a></p>\n"
            + "      </div>\n"
            + "    </td>\n"
            + "  </tr>\n"
            + "  </tbody>\n"
            + "</table>";

    MimeMessage message = mailSender.createMimeMessage();
    try {
      // 본문
      Document doc = Jsoup.parse(html, "UTF-8");
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
      // 보내는 사람
      message.setFrom("damu@inveniacorp.com");
      // 받는 사람
      message.addRecipient(RecipientType.TO, new InternetAddress("ssu@didigo.com"));
      // 참조
      message.addRecipient(RecipientType.CC, new InternetAddress("damu@inveniacorp.com"));

      mailSender.send(message);
      log.info("자동화 실패 메일 발송 완료");
    } catch (MessagingException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }

  public void sendUnregisteredCustomerMail(String filePath, int size) {
    if (!batchConfig.getRunSendMail()) {
      return;
    }
    log.info("미등록 거래처 메일 발송 시작");

    // 템플릿
    String html =
        "<table style=\"font-size: 12pt;\">\n"
            + "  <tbody>\n"
            + "  <tr>\n"
            + "    <td style=\"padding-left:20px\">\n"
            + "      <div style=\"border:0 solid #FFFFFF;width:660px;padding:10px 0 10px 15px;\n"
            + "      background-color:#4F5258;font-family:Trebuchet MS,serif;color:#FFFFFF;\">\n"
            + "        <span style=\"font-size:14px;font-weight:bold;\">Unregistered Customer</span>\n"
            + "      </div>\n"
            + "    </td>\n"
            + "  </tr>\n"
            + "  <tr>\n"
            + "    <td style=\"padding: 30px 0 0 30px;\">\n"
            + "      <div style=\"font-family:Trebuchet MS, arial, 맑은 고딕,serif;font-size:12px;color:#666666;\">\n"
            + "        <br/>\n"
            + "        <p>미등록 거래처 : <span id=\"count\"></span>건</p>\n"
            + "        <br/>\n"
            + "        <p><a href=\"http://192.168.11.158:8080/\" target=\"_blank\">관리 페이지 바로가기</a></p>\n"
            + "      </div>\n"
            + "    </td>\n"
            + "  </tr>\n"
            + "  </tbody>\n"
            + "</table>";

    MimeMessage message = mailSender.createMimeMessage();
    try {
      // 본문
      MimeBodyPart htmlPart = new MimeBodyPart();
      Document doc = Jsoup.parse(html, "UTF-8");
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
      // 보내는 사람
      message.setFrom("damu@inveniacorp.com");
      // 받는 사람
      message.addRecipient(RecipientType.TO, new InternetAddress("ssu@didigo.com"));
      // 참조
      message.addRecipient(RecipientType.CC, new InternetAddress("damu@inveniacorp.com"));

      mailSender.send(message);
      log.info("미등록 거래처 메일 발송 완료");
    } catch (MessagingException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }
}

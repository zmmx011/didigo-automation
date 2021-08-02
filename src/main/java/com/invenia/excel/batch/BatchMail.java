package com.invenia.excel.batch;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.batch.core.JobExecution;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchMail {

  private final JavaMailSender mailSender;

  public void sendJobFailureMail(JobExecution jobExecution) {
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
            + "        <p id=\"errorMessage\" style=\"padding:1em 0;font-size:16px;font-weight:bold\"></p>\n"
            + "        <p><a href=\"http://192.168.11.158:8080/\" target=\"_blank\">관리 페이지 바로가기</a></p>\n"
            + "      </div>\n"
            + "    </td>\n"
            + "  </tr>\n"
            + "  </tbody>\n"
            + "</table>";

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    try {
      // 본문
      String jobName = jobExecution.getJobInstance().getJobName();
      Document doc = Jsoup.parse(html, "UTF-8");
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      doc.getElementById("no").text(String.valueOf(jobExecution.getId()));
      doc.getElementById("jobName").text(jobName);
      doc.getElementById("startTime").text(dateFormat.format(jobExecution.getStartTime()));
      doc.getElementById("endTime").text(dateFormat.format(jobExecution.getEndTime()));
      String errorMessage =
          jobExecution.getFailureExceptions().stream()
              .map(Throwable::getMessage)
              .collect(Collectors.joining("\\n"));
      doc.getElementById("errorMessage").text(errorMessage);
      mimeMessage.setText(doc.toString(), "UTF-8", "html");
      // 제목
      String subject = "[ERP Automation] " + jobName + " 실패 알림";
      mimeMessage.setSubject(subject, "UTF-8");
      //
      mimeMessage.setFrom("damu@inveniacorp.com");
      mimeMessage.addRecipient(RecipientType.TO, new InternetAddress("damu@inveniacorp.com"));
      mailSender.send(mimeMessage);
      log.info("실패 메일 발송 완료");
    } catch (MessagingException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }
}

package com.invenia.excel.converter;

import com.invenia.excel.converter.dto.ConvertResult;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;

@Data
@ConfigurationProperties("convert.config")
public class ConvertConfig {

  private String downloadPath;
  private String backupPath;
  private String outputPath;
  private String customerFileName;
  private String itemCodeFileName;
  private String itemPriceFileName;
  private String contractOrderFileName;
  private String templatePath;
  private Map<String, ConvertResult> convertResult;

  public String getTemplatePath(String... paths) {
    return new ClassPathResource(templatePath + String.join("/", paths)).getPath();
  }

  public String getOutputPath(String... paths) {
    return outputPath + String.join("/", paths);
  }

  public String getBackupPath(String... paths) {
    return backupPath + String.join("/", paths);
  }

  public String getKdFilePath() {
    return downloadPath + "구매발주품목조회_" + getErpDownloadFileDate() + ".xlsx";
  }

  public String getCozyFilePath() {
    return downloadPath
        + "Sales_by_period_"
        + LocalDate.of(2021, 7, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        + "_"
        + LocalDate.of(2021, 7, 11).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        + ".xls";
  }

  public String getMallFilePath() {
    return downloadPath + "MemberOrderList.xls";
  }

  public String getItemCodeFilePath() {
    return downloadPath + "품목조회_" + getErpDownloadFileDate() + ".xlsx";
  }

  public String getPriceFilePath() {
    return downloadPath + "구매단가등록_" + getErpDownloadFileDate() + ".xlsx";
  }

  public String getCustomerFilePath() {
    return downloadPath + "거래처조회_" + getErpDownloadFileDate() + ".xlsx";
  }

  public String getContractOrderFilePath() {
    return downloadPath + "수주조회_" + getErpDownloadFileDate() + ".xlsx";
  }

  // ERP 오전 9시 이전 다운로드시 이전 날짜로 파일 명 생성됨.
  private String getErpDownloadFileDate() {
    if (LocalTime.now().isBefore(LocalTime.of(9, 0, 0))) {
      return LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    } else {
      return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
  }
}

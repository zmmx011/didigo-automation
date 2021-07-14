package com.invenia.excel.converter;

import com.invenia.excel.converter.dto.ConvertResult;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
@ConfigurationProperties("convert.config")
public class ConvertConfig {
	private String chromeDownloadPath;
	private String ieDownloadPath;
	private String backupPath;
	private String outputPath;
	private String customerFileName;
	private String itemFileName;
	private String contractOrderFileName;
	private String purchaseOrderFileName;
	private String templatePath;
	private Map<String, ConvertResult> convertResult;

	public String getKdFilePath() {
		return chromeDownloadPath + "구매발주품목조회_" +
				LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
	}

	public String getCozyFilePath() {
		return chromeDownloadPath + "Sales_by_period_" +
				LocalDate.of(2021, 7, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "_" +
				LocalDate.of(2021, 7, 11).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +".xls";
	}

	public String getMallFilePath() {
		return ieDownloadPath + "MemberOrderList.xls";
	}

	public String getItemFilePath() {
		return chromeDownloadPath + "품목조회_" +
				LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
	}

	public String getCustomerFilePath() {
		return chromeDownloadPath + "거래처조회_" +
				LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
	}
}

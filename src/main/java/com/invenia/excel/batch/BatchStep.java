package com.invenia.excel.batch;

import com.invenia.excel.converter.ExcelConverter;
import com.invenia.excel.selenium.Automation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchStep {
	private final ExcelConverter excelConverter;
	private final Automation automation;
	private final StepBuilderFactory stepBuilderFactory;
	private final BatchConfig batchConfig;

	public Step initStep() {
		return stepBuilderFactory.get("초기화")
				.tasklet((contribution, chunkContext) -> {
					excelConverter.init();
					return RepeatStatus.FINISHED;
				}).build();
	}

	public Step itemCodeDownloadStep(boolean login) {
		return stepBuilderFactory.get("품목 다운로드")
				.tasklet((contribution, chunkContext) -> {
					try {
						automation.runItemCodeDownload(batchConfig.getDidigoWebUrl(), login);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
					}
					return RepeatStatus.FINISHED;
				}).build();
	}

	public Step contractOrderDownloadStep() {
		return stepBuilderFactory.get("수주 다운로드")
				.tasklet((contribution, chunkContext) -> {
					try {
						automation.runContractOrderDownload();
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
					}
					return RepeatStatus.FINISHED;
				}).build();
	}

	public Step customerDownloadStep(boolean login) {
		return stepBuilderFactory.get("거래처 다운로드")
				.tasklet((contribution, chunkContext) -> {
					try {
						automation.runCustomerDownload(batchConfig.getDidigoWebUrl(), login);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
					}
					return RepeatStatus.FINISHED;
				}).build();
	}

	public Step siteDownloadStep() {
		return stepBuilderFactory.get("데이터 수집")
				.tasklet((contribution, chunkContext) -> {
					LocalDate fromDate = LocalDate.of(2021, 7, 1);
					LocalDate toDate = LocalDate.of(2021, 7, 11);
					try {
						if (batchConfig.getRunKd()) {
							automation.newTab();
							automation.changeTab(batchConfig.getTabIndex() + 1);

							automation.runKdErpDownload(fromDate, toDate, batchConfig.getKdWebUrl());
						}
						if (batchConfig.getRunCozy()) {
							automation.runCozyDownload(fromDate, toDate, batchConfig.getCozyWebUrl());
						}
						if (batchConfig.getRunMall()) {
							automation.runMallDownload(fromDate, toDate, batchConfig.getMallWebUrl());
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
					}
					return RepeatStatus.FINISHED;
				}).build();
	}

	public Step itemCodeConvertStep() {
		return stepBuilderFactory.get("품목 변환")
				.tasklet((contribution, chunkContext) -> convertRunCheck(ExcelConverter::itemCodeConvert, contribution))
				.build();
	}

	public Step customerCheckStep() {
		return stepBuilderFactory.get("거래처 체크")
				.tasklet((contribution, chunkContext) -> convertRunCheck(ExcelConverter::unregisteredCustomerCheck, contribution))
				.build();
	}

	public Step contractOrderConvertStep() {
		return stepBuilderFactory.get("수주 변환")
				.tasklet((contribution, chunkContext) -> convertRunCheck(ExcelConverter::contractOrderConvert, contribution))
				.build();
	}

	public Step purchaseOrderConvertStep() {
		return stepBuilderFactory.get("발주 변환")
				.tasklet((contribution, chunkContext) -> convertRunCheck(ExcelConverter::purchaseOrderConvert, contribution))
				.build();
	}

	public Step itemCodeUploadStep() {
		return stepBuilderFactory.get("품목 업로드")
				.tasklet((contribution, chunkContext) -> automationRunCheck(Automation::runItemCodeUpload, contribution))
				.build();
	}

	public Step contractOrderUploadStep() {
		return stepBuilderFactory.get("수주 업로드")
				.tasklet((contribution, chunkContext) -> automationRunCheck(Automation::runContractOrderUpload, contribution))
				.build();
	}

	public Step sendMailStep() {
		return stepBuilderFactory.get("메일 발송")
				.tasklet((contribution, chunkContext) -> automationRunCheck(Automation::runContractOrderUpload, contribution))
				.build();
	}

	private RepeatStatus convertRunCheck(ThrowsBiConsumer<ExcelConverter, String> func, StepContribution contribution) {
		try {
			if (batchConfig.getRunKd()) {
				func.accept(excelConverter, "kd");
			}
			if (batchConfig.getRunCozy()) {
				func.accept(excelConverter, "cozy");
			}
			if (batchConfig.getRunMall()) {
				func.accept(excelConverter, "mall");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
		}
		return RepeatStatus.FINISHED;
	}

	private RepeatStatus automationRunCheck(ThrowsBiConsumer<Automation, String> func, StepContribution contribution) {
		try {
			if (batchConfig.getTabIndex() != 0) {
				batchConfig.setTabIndex(0);
				automation.changeTab(batchConfig.getTabIndex());
			}
			if (batchConfig.getRunKd()) {
				func.accept(automation, "kd");
			}
			if (batchConfig.getRunCozy()) {
				func.accept(automation, "cozy");
			}
			if (batchConfig.getRunMall()) {
				func.accept(automation, "mall");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
		}
		return RepeatStatus.FINISHED;
	}
}

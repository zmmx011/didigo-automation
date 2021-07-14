package com.invenia.excel.web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@SequenceGenerator(
		name = "BATCH_JOB_EXECUTION_SEQ_GENERATOR",
		sequenceName = "BATCH_JOB_EXECUTION_SEQ",
		allocationSize = 1)
@Table(name = "BATCH_JOB_EXECUTION")
public class BatchJobExecution implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BATCH_JOB_EXECUTION_SEQ_GENERATOR")
	private Long jobExecutionId;
	private Long version;
	@ManyToOne
	@JoinColumn(name = "JOB_INSTANCE_ID")
	private BatchJobInstance jobInstanceId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime createTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime startTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime endTime;
	private String status;
	private String exitCode;
	@Column(length = 5000)
	private String exitMessage;
	private LocalDateTime lastUpdated;
	private String jobConfigurationLocation;

	@OneToMany(mappedBy = "jobExecutionId")
	@ToString.Exclude
	private List<BatchStepExecution> batchStepExecutions;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		BatchJobExecution that = (BatchJobExecution) o;

		return Objects.equals(jobExecutionId, that.jobExecutionId);
	}

	@Override
	public int hashCode() {
		return 446044055;
	}
}

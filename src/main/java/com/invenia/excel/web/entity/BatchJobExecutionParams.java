package com.invenia.excel.web.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "BATCH_JOB_EXECUTION_PARAMS")
public class BatchJobExecutionParams implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@ManyToOne
	@JoinColumn(name = "JOB_EXECUTION_ID")
	private BatchJobExecution jobExecutionId;
	private String typeCd;
	private String keyName;
	private String stringVal;
	private LocalDateTime dateVal;
	private Long longVal;
	private Double doubleVal;
	private String identifying;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		BatchJobExecutionParams that = (BatchJobExecutionParams) o;

		return Objects.equals(jobExecutionId, that.jobExecutionId);
	}

	@Override
	public int hashCode() {
		return 1009468259;
	}
}


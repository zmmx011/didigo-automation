package com.invenia.excel.web.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@SequenceGenerator(
		name = "BATCH_JOB_SEQ_GENERATOR",
		sequenceName = "BATCH_JOB_SEQ",
		allocationSize = 1)
@Table(name = "BATCH_JOB_INSTANCE")
public class BatchJobInstance implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BATCH_JOB_SEQ")
	private Long jobInstanceId;
	private Long version;
	private String jobName;
	private String jobKey;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		BatchJobInstance that = (BatchJobInstance) o;

		return Objects.equals(jobInstanceId, that.jobInstanceId);
	}

	@Override
	public int hashCode() {
		return 262323999;
	}
}


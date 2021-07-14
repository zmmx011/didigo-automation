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
@Table(name = "BATCH_STEP_EXECUTION_CONTEXT")
public class BatchStepExecutionContext implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@ManyToOne
	@JoinColumn(name = "STEP_EXECUTION_ID")
	private BatchStepExecution stepExecutionId;
	private String shortContext;
	private String serializedContext;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		BatchStepExecutionContext that = (BatchStepExecutionContext) o;

		return Objects.equals(stepExecutionId, that.stepExecutionId);
	}

	@Override
	public int hashCode() {
		return 1711249125;
	}
}


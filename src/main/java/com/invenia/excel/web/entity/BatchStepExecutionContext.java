package com.invenia.excel.web.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

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
		if (this == o) {
			return true;
		}
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
    BatchStepExecutionContext that = (BatchStepExecutionContext) o;

    return Objects.equals(stepExecutionId, that.stepExecutionId);
  }

  @Override
  public int hashCode() {
    return 1711249125;
  }
}


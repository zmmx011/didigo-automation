package com.invenia.excel.web.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
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
@SequenceGenerator(
    name = "BATCH_STEP_EXECUTION_SEQ_GENERATOR",
    sequenceName = "BATCH_STEP_EXECUTION_SEQ",
    allocationSize = 1)
@Table(name = "BATCH_STEP_EXECUTION")
public class BatchStepExecution implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BATCH_STEP_EXECUTION_SEQ")
  private Long stepExecutionId;
  private Long version;
  private String stepName;
  @ManyToOne
  @JoinColumn(name = "JOB_EXECUTION_ID")
  private BatchJobExecution jobExecutionId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String status;
  private Long commitCount;
  private Long readCount;
  private Long filterCount;
  private Long writeCount;
  private Long readSkipCount;
  private Long writeSkipCount;
  private Long processSkipCount;
  private Long rollbackCount;
  private String exitCode;
  private String exitMessage;
  private LocalDateTime lastUpdated;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    BatchStepExecution that = (BatchStepExecution) o;

    return Objects.equals(stepExecutionId, that.stepExecutionId);
  }

  @Override
  public int hashCode() {
    return 1940114563;
  }
}


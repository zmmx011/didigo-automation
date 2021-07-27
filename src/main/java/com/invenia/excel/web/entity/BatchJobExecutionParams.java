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
@Table(name = "BATCH_JOB_EXECUTION_PARAMS")
public class BatchJobExecutionParams implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long jobExecutionParamsId;
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
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    BatchJobExecutionParams that = (BatchJobExecutionParams) o;

    return Objects.equals(jobExecutionId, that.jobExecutionId);
  }

  @Override
  public int hashCode() {
    return 1009468259;
  }
}


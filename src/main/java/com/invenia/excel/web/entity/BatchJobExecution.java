package com.invenia.excel.web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.engine.jdbc.batch.spi.Batch;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "BATCH_JOB_EXECUTION")
public class BatchJobExecution implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
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
  @Column(length = 10)
  private String status;
  @Column(length = 2500)
  private String exitCode;
  @Column(length = 5000)
  private String exitMessage;
  private LocalDateTime lastUpdated;
  private String jobConfigurationLocation;

  @OneToMany(mappedBy = "jobExecutionId")
  @ToString.Exclude
  private List<BatchStepExecution> batchStepExecutions;

  @OneToMany(mappedBy = "jobExecutionId")
  @ToString.Exclude
  private List<BatchJobExecutionParams> batchJobExecutionParams;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    BatchJobExecution that = (BatchJobExecution) o;

    return Objects.equals(jobExecutionId, that.jobExecutionId);
  }

  @Override
  public int hashCode() {
    return 446044055;
  }
}

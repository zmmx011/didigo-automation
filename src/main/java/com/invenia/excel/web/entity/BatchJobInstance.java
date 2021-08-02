package com.invenia.excel.web.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "BATCH_JOB_INSTANCE")
public class BatchJobInstance implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long jobInstanceId;

  private Long version;
  private String jobName;
  private String jobKey;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    BatchJobInstance that = (BatchJobInstance) o;

    return Objects.equals(jobInstanceId, that.jobInstanceId);
  }

  @Override
  public int hashCode() {
    return 262323999;
  }
}

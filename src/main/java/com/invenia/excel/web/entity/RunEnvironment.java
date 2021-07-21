package com.invenia.excel.web.entity;

import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
public class RunEnvironment {

  @Id
  private String type;
  private String cron;
  private int period;
  private LocalDate fromDate;
  private LocalDate toDate;

  @Override
  public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}
    RunEnvironment that = (RunEnvironment) o;

    return Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return 976460746;
  }
}

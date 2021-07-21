package com.invenia.excel.web.repository;

import com.invenia.excel.web.entity.BatchJobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface BatchJobExecutionRepository extends JpaRepository<BatchJobExecution, Long>,
    JpaSpecificationExecutor<BatchJobExecution> {

}

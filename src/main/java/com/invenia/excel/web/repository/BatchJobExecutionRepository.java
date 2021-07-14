package com.invenia.excel.web.repository;

import com.invenia.excel.web.entity.BatchJobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RepositoryRestResource
public interface BatchJobExecutionRepository extends JpaRepository<BatchJobExecution, Long> {
}

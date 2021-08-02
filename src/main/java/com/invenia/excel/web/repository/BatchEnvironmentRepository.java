package com.invenia.excel.web.repository;

import com.invenia.excel.web.entity.BatchEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface BatchEnvironmentRepository extends JpaRepository<BatchEnvironment, String> {}

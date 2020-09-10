package com.nnam.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.nnam.project.model.Assessment;
import com.nnam.project.model.Employee;

public interface AssessmentRepository extends CrudRepository<Assessment, Integer> {

	@Query("SELECT assessment FROM Assessment assessment WHERE assessment.employee=?1 ORDER BY assessment.createdDate DESC")
	List<Assessment> findByEmployee (@Param ("employee") Employee employee);
	
}

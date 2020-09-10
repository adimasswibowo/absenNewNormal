package com.nnam.project.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.nnam.project.model.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
	
	Optional<Employee> findByName (@Param ("name") String name);
	Optional<Employee> findByCode (@Param ("code") String code);

}

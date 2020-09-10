package com.nnam.project.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.nnam.project.model.Absen;
import com.nnam.project.model.Employee;

public interface AbsenRepository extends CrudRepository<Absen, Integer> {

	List<Absen> findByEmployee (@Param ("employee") Employee employee);
	
	@Query("SELECT absen FROM Absen absen WHERE absen.clockIn>=?1 AND  absen.clockIn<=?2")
	List<Absen> findByDateTime (@Param ("clockIn") Date startDate, @Param ("clockIn") Date endDate);
	
	@Query("SELECT absen FROM Absen absen WHERE absen.employee=?1 AND absen.clockIn>=?2 AND  absen.clockIn<=?3")
	List<Absen> findByEmployeeAndClockIn (@Param ("employee") Employee employee, @Param ("clockIn") Date startDate, @Param ("clockIn") Date endDate);
	
	
}

package com.nnam.project.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="tblassessment")
public class Assessment {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column
	private Integer id;
	@Column(name="created_date")
	private Date createdDate;
	@ManyToOne
	@JoinColumn(name="employee")
	private Employee employee;
	@Column
	private String result;
	@Column
	private Integer score;
	@Column
	private String note;
	@Column(name="validated_result")
	private String validatedResult;
	@Column
	private Date validatedDate;
	@Column
	private Date expiredDate;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getValidatedResult() {
		return validatedResult;
	}
	public void setValidatedResult(String validatedResult) {
		this.validatedResult = validatedResult;
	}
	public Date getValidatedDate() {
		return validatedDate;
	}
	public void setValidatedDate(Date validatedDate) {
		this.validatedDate = validatedDate;
	}
	public Date getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}
	
	

}

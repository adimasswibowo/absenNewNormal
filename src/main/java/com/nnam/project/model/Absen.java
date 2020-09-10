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
@Table(name="tblabsen")
public class Absen {
		
		@Id
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		@Column
		private Integer id;
		@ManyToOne
		@JoinColumn(name="employee")
		private Employee employee;
		@Column(name="clock_in")
		private Date clockIn;
		@Column(name="clock_out")
		private Date clockOut;
		@Column
		private String note;
		
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Employee getEmployee() {
			return employee;
		}
		public void setEmployee(Employee employee) {
			this.employee = employee;
		}
		
		
		public Date getClockIn() {
			return clockIn;
		}
		public void setClockIn(Date clockIn) {
			this.clockIn = clockIn;
		}
		public Date getClockOut() {
			return clockOut;
		}
		public void setClockOut(Date clockOut) {
			this.clockOut = clockOut;
		}
		public String getNote() {
			return note;
		}
		public void setNote(String note) {
			this.note = note;
		}
		
		

}

package com.nnam.project.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nnam.project.model.Absen;
import com.nnam.project.model.Assessment;
import com.nnam.project.model.Employee;
import com.nnam.project.repository.AbsenRepository;
import com.nnam.project.repository.AssessmentRepository;
import com.nnam.project.repository.EmployeeRepository;
import com.nnam.project.util.BaseUtil;


@RestController
@RequestMapping(value= {"/v1/Absen"})
public class AbsenController {
	
	@Autowired 
	AbsenRepository absenRepository;
	@Autowired 
	EmployeeRepository employeeRepository;
	@Autowired
	AssessmentRepository assessmentRepository;
	
	
	@GetMapping("/getListAll")
	public ResponseEntity<Map> getListAll(){
		Map responseMap=new HashMap();
		List<Absen> absenList=new LinkedList<>();
		try {
			absenList=(List<Absen>) absenRepository.findAll();
			if (absenList.size()<1) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMessage", "failed");
				return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMessage", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMessage", "success");
		responseMap.put("absen", absenList);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	
	@PostMapping("/getByParams")
	public ResponseEntity<Map> getAbsenByParams(@RequestBody Map paramMap) {
		Map responseMap=new HashMap();
		List<Absen> absenList=null;
		try {
			Integer employeeId=null;
			Date startDate=null;
			Date endDate=null;
			if(paramMap.get("employee")!=null) {
				employeeId=(Integer) paramMap.get("employee");
				Optional<Employee> employeeOptional=employeeRepository.findById(employeeId);
				if(!employeeOptional.isPresent()) {
					employeeId=(Integer) paramMap.get("employee");
					responseMap.put("responseCode", "01");
					responseMap.put("responseMessage", "Employee with Id:"+employeeId+" not found");
					return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
				}
				Employee employeeObj=employeeOptional.get();
				
				if(paramMap.get("startDate")!=null && paramMap.get("endDate")!=null) {
					startDate=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse((String)paramMap.get("startDate"));  
					endDate=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse((String)paramMap.get("endDate"));  
					absenList= absenRepository.findByEmployeeAndClockIn(employeeObj, startDate, endDate);
				}else {
					absenList= absenRepository.findByEmployee(employeeObj);
				}
				
			}else if(paramMap.get("startDate")!=null && paramMap.get("endDate")!=null) {
				startDate=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse((String)paramMap.get("startDate"));  
				endDate=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse((String)paramMap.get("endDate"));  
				absenList= absenRepository.findByDateTime(startDate,endDate);
			}
			
			if(absenList.size()<1) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMessage", "Absen not found");
				return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMessage", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMessage", "success");
		responseMap.put("absen", absenList);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	
	@PostMapping("/recordAbsen")
	public ResponseEntity<Map> recordAbsen(@RequestBody Map paramMap){
		Map responseMap=new HashMap();
		Employee employeeObj=new Employee();
		Absen absenToday=new Absen();
		
		try {
			Integer employeeId=null;
			if(paramMap.get("employee")!=null) {
				employeeId=(Integer) paramMap.get("employee");
				Optional<Employee> employeeOptional=employeeRepository.findById(employeeId);
				if(!employeeOptional.isPresent()) {
					responseMap.put("responseCode", "01");
					responseMap.put("responseMessage", "Employee with Id:"+employeeId+" not found");
					return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
				}
				employeeObj=employeeOptional.get();
			}
			
			//TODAY
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.MILLISECOND, 0);
			Date todayBegin=now.getTime();
			now.set(Calendar.HOUR_OF_DAY, 23);
			now.set(Calendar.MINUTE, 59);
			now.set(Calendar.SECOND, 59);
			now.set(Calendar.MILLISECOND, 999);
			Date todayEnd=now.getTime();
			
			//Cek sudah ada assessment belum
			List<Assessment> assessmentList=assessmentRepository.findByEmployee(employeeObj);
			if(assessmentList.size()<1) {
				//Belum ada assessment
				responseMap.put("responseCode", "01");
				responseMap.put("responseMessage", "You no have an assessment (no record), please do assessment first");
				return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
			}else {
				//Sudah ada assessment
				Assessment assessmentActive=assessmentList.get(0);
				Date expiredDateAssessment=assessmentActive.getExpiredDate();
				if(expiredDateAssessment==null) {
					//Sudah ada assessment tetapi perlu diputuskan oleh supervisor WFH atau WFO
					responseMap.put("responseCode", "01");
					responseMap.put("responseMessage", "You have an uncompleted assessment, please contact your supervisor");
					return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
				if(expiredDateAssessment!=null && todayBegin.after(expiredDateAssessment)) {
					//Sudah ada assessment tetapi expired, isi assessment terlebih dahulu
					responseMap.put("responseCode", "01");
					responseMap.put("responseMessage", "You no have an assessment (expired), please do assessment first");
					return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
				if(!"WFO".equalsIgnoreCase(assessmentActive.getValidatedResult())) {
					//Sudah ada assessment tetapi diharuskan WFH berdasarkan hasil assessment
					responseMap.put("responseCode", "01");
					responseMap.put("responseMessage", "You must be WORK FROM HOME");
					return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
			}
			
			//PART ABSEN
			//Cek sudah ada atau belum ditanggal tsb
			//Cek sudah ada clock in belum
			List<Absen> absenList= absenRepository.findByEmployeeAndClockIn(employeeObj, todayBegin, todayEnd);
			
			if(absenList.size()>0) {
				//Jika sudah ada isi clock out
				absenToday=absenList.get(0);
				absenToday.setClockOut(new Date());
				absenToday.setNote(BaseUtil.constructNoteAbsen(absenToday));
			}else {
				//Jika belum ada isi clock in
				absenToday.setEmployee(employeeObj);
				absenToday.setClockIn(new Date());
				absenToday.setNote(BaseUtil.constructNoteAbsen(absenToday));
			}
			
			absenRepository.save(absenToday);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMessage", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMessage", "Success add Absen");
		responseMap.put("absen", absenToday);
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	

}

package com.nnam.project.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nnam.project.model.Assessment;
import com.nnam.project.model.Employee;
import com.nnam.project.model.Params;
import com.nnam.project.repository.AssessmentRepository;
import com.nnam.project.repository.EmployeeRepository;
import com.nnam.project.repository.ParamsRepository;
import com.nnam.project.util.BaseUtil;

@RestController
@RequestMapping(value= {"/v1/Assessment"})
public class AssessmentController {
	
	@Autowired 
	AssessmentRepository assessmentRepository;
	@Autowired 
	EmployeeRepository employeeRepository;
	@Autowired 
	ParamsRepository paramsRepository;
	
	
	@GetMapping("/getListAll")
	public ResponseEntity<Map> getListAll(){
		Map responseMap=new HashMap();
		List<Assessment> assessmentList=new LinkedList<>();
		try {
			assessmentList=(List<Assessment>) assessmentRepository.findAll();
			if (assessmentList.size()<1) {
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
		responseMap.put("assessment", assessmentList);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	
	@PostMapping("/getByParams")
	public ResponseEntity<Map> getAssessmentByParams(@RequestBody Map paramMap) {
		Map responseMap=new HashMap();
		List<Assessment> assessmentList=null;
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
				assessmentList= assessmentRepository.findByEmployee(employeeObj);
			}
			
			if(assessmentList.size()<1) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMessage", "Assessment not found");
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
		responseMap.put("assessment", assessmentList);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	
	@PostMapping("/recordAssessmentByEmployee")
	public ResponseEntity<Map> recordAssessmentByEmployee(@RequestBody Map paramMap){
		Map responseMap=new HashMap();
		Employee employeeObj=new Employee();
		Assessment assessmentActive=new Assessment();
		
		try {
			Boolean checkMandatoryKey=BaseUtil.checkMandatoryKey("employee|result", paramMap);
			if (!checkMandatoryKey) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMessage", "parameters cannot be empty");
				return new ResponseEntity<Map>(responseMap,HttpStatus.BAD_REQUEST);
			}
			
			Integer employeeId=null;
			String result=(String) paramMap.get("paramMap");
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
			Date today=now.getTime();
			
			now.add(Calendar.DATE, 14);
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.MILLISECOND, 00);
			Date expiredDate=now.getTime();

			
			List<Assessment> assessmentList= assessmentRepository.findByEmployee(employeeObj);

			if(assessmentList.size()>0) {
				//Jika sudah ada lihat assessment terakhir apakah masih valid(tidak expired)
				assessmentActive=assessmentList.get(0);
				Date expiredDateAssessment=assessmentActive.getExpiredDate();
				if(expiredDateAssessment==null) {
					responseMap.put("responseCode", "01");
					responseMap.put("responseMessage", "You have an uncompleted assessment, please contact your supervisor");
					return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				now.setTime(expiredDateAssessment);
				now.add(Calendar.DATE, -3);
				Date toleranceDate=now.getTime();
				Date toleranceDated=now.getTime();
				if(expiredDateAssessment!=null && today.before(toleranceDate)) {
					responseMap.put("responseCode", "01");
					responseMap.put("responseMessage", "You have already assessment");
					return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			
			assessmentActive=new Assessment();
			assessmentActive.setEmployee(employeeObj);
			assessmentActive.setCreatedDate(today);
				
			ObjectMapper mapper = new ObjectMapper();
			Map resultMap=(Map) paramMap.get("result");
			String resultString=null;
			try {
				resultString = mapper.writeValueAsString(resultMap);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			assessmentActive.setResult(resultString);
			
			List<Params> questionList=paramsRepository.findByType("QUESTIONS_ASSESSMENT_NN");
			assessmentActive.setScore(BaseUtil.constructScoreAssessment(questionList, resultMap));
			assessmentActive.setNote(BaseUtil.constructNoteAssessment(assessmentActive.getScore()));
			
			if(assessmentActive.getScore()==0 || assessmentActive.getScore()>=5 ) {
				assessmentActive.setValidatedResult(BaseUtil.constructResultAssessment(assessmentActive.getScore()));
				assessmentActive.setValidatedDate(today);
				assessmentActive.setExpiredDate(expiredDate);
			}
			
			assessmentRepository.save(assessmentActive);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMessage", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMessage", "Success add Assessment");
		responseMap.put("assessment", assessmentActive);
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	
	@PostMapping("/validateAssessmentByAdmin")
	public ResponseEntity<Map> validateAssessmentByAdmin(@RequestBody Map paramMap){
		Map responseMap=new HashMap();
		Employee employeeObj=new Employee();
		Assessment assessmentActive=new Assessment();
		
		try {
			Boolean checkMandatoryKey=BaseUtil.checkMandatoryKey("employee|result|note", paramMap);
			if (!checkMandatoryKey) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMessage", "parameters cannot be empty");
				return new ResponseEntity<Map>(responseMap,HttpStatus.BAD_REQUEST);
			}
			
			Integer employeeId=null;
			String result=(String) paramMap.get("paramMap");
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
			Date today=now.getTime();
			
			now.add(Calendar.DATE, 14);
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			Date expiredDate=now.getTime();

			
			List<Assessment> assessmentList= assessmentRepository.findByEmployee(employeeObj);
			if(assessmentList.size()<1) {
				//Belum memiliki assessment
				responseMap.put("responseCode", "01");
				responseMap.put("responseMessage", "Employee with id :"+employeeId+ "no have an uncompleted assessment (no record)");
				return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			if(assessmentList.size()>0) {
				//Jika sudah ada lihat assessment terakhir apakah sudah divalidasi atau belum
				assessmentActive=assessmentList.get(0);
				Date expiredDateAssessment=assessmentActive.getExpiredDate();
				if(expiredDateAssessment!=null) {
					responseMap.put("responseCode", "01");
					responseMap.put("responseMessage", "Employee with id :"+employeeId+ " no have an uncompleted assessment");
					return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
				
			
			assessmentActive.setNote((String) paramMap.get("note"));
			if ("WFO".equalsIgnoreCase((String) paramMap.get("result"))) {
				assessmentActive.setValidatedResult("WFO");
			}else {
				assessmentActive.setValidatedResult("WFH");
			}
			assessmentActive.setValidatedDate(today);
			assessmentActive.setExpiredDate(expiredDate);
			
			assessmentRepository.save(assessmentActive);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMessage", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMessage", "Success validate Assessment");
		responseMap.put("assessment", assessmentActive);
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	
}
package com.nnam.project.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nnam.project.model.Employee;
import com.nnam.project.repository.EmployeeRepository;
import com.nnam.project.util.BaseUtil;


@RestController
@RequestMapping(value= {"/v1/Employee"})
public class EmployeeController {
	
	@Autowired EmployeeRepository employeeRepository;
	
	@GetMapping("/getListAll")
	public ResponseEntity<Map> getListAll(){
		Map responseMap=new HashMap();
		List<Employee> employeeList=new LinkedList<>();
		try {
			employeeList=(List<Employee>) employeeRepository.findAll();
			if (employeeList.size()<1) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMap", "failed");
				return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMap", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMap", "success");
		responseMap.put("employee", employeeList);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	@GetMapping("/getById/{id}")
	public ResponseEntity<Map> getEmployeeById(@PathVariable(value="id") Integer employeeId){
		Map responseMap=new HashMap();
		Employee employeeObj=new Employee();
		
		try {
			Optional<Employee> employeeOptional=employeeRepository.findById(employeeId);
			if(!employeeOptional.isPresent()) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMap", "Employee with Id="+employeeId+" not found");
				return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
			}
			employeeObj=employeeOptional.get();
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMap", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMap", "success");
		responseMap.put("employee", employeeObj);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	@PostMapping("/getByParams")
	public ResponseEntity<Map> getEmployeeByParams (@RequestBody Map paramMap) {
		Map responseMap=new HashMap();
		Employee employeeObj=new Employee();
		String paramKey="";
		String paramValue="";
		try {
			Optional<Employee> employeeOptional=null;
			if((String)paramMap.get("code")!=null) {
				employeeOptional= employeeRepository.findByCode((String)paramMap.get("code"));
				paramKey="code";
				paramValue=(String)paramMap.get("code");
			}else if((String)paramMap.get("name")!=null) {
				employeeOptional= employeeRepository.findByName((String)paramMap.get("name"));
				paramKey="name";
				paramValue=(String)paramMap.get("name");
			}

			if(!employeeOptional.isPresent()) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMap", "Employee with "+paramKey+"="+paramValue+" not found");
				return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
			}
			employeeObj=employeeOptional.get();
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMap", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMap", "success");
		responseMap.put("employee", employeeObj);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	@PostMapping("/add")
	public ResponseEntity<Map> create(@RequestBody Employee EmployeeObj){
		Map responseMap=new HashMap();
		try {
			employeeRepository.save(EmployeeObj);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMap", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMap", "Success add Employee");
		responseMap.put("employee", EmployeeObj);
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	
	@GetMapping("/deleteById/{id}")
	public ResponseEntity<Map> deleteEmployee (@PathVariable(value="id") Integer employeeId) {
		Map responseMap=new HashMap();
		Employee employeeObj=new Employee();
		try {
			Optional<Employee> employeeOptional=employeeRepository.findById(employeeId);
			if(!employeeOptional.isPresent()) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMap", "Employee with Id:"+employeeId+" not found");
				return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
			}
			employeeObj=employeeOptional.get();
			employeeRepository.deleteById(employeeId);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMap", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMap", "Success delete Employee");
		responseMap.put("employee", employeeObj);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	@PostMapping("/updateById/{id}")
	public ResponseEntity<Map> updateEmployeeById (@RequestBody Employee employeeNew, @PathVariable(value="id") Integer employeeId) {
		Map responseMap=new HashMap();
		Employee employeeFromDB = new Employee();
		try {
			Optional<Employee> employeeOptional=employeeRepository.findById(employeeId);
			if(!employeeOptional.isPresent()) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMap", "Employee with Id:"+employeeId+" not found");
				return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
			}
			employeeFromDB=employeeOptional.get();
			BaseUtil.setObjectFromObject(employeeFromDB, employeeNew);
			employeeRepository.save(employeeFromDB);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMap", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		responseMap.put("responseCode", "00");
		responseMap.put("responseMap", "Success update Employee");
		responseMap.put("employee", employeeFromDB);
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	

}

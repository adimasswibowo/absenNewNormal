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

import com.nnam.project.model.Params;
import com.nnam.project.repository.ParamsRepository;
import com.nnam.project.util.BaseUtil;

@RestController
@RequestMapping(value= {"/v1/Params"})
public class ParamsController {
	
	@Autowired ParamsRepository paramsRepository;
	
	@GetMapping("/getListAll")
	public ResponseEntity<Map> getListAll(){
		Map responseMap=new HashMap();
		List<Params> paramsList=new LinkedList<>();
		try {
			paramsList=(List<Params>) paramsRepository.findAll();
			if (paramsList.size()<1) {
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
		responseMap.put("params", paramsList);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	@GetMapping("/getById/{id}")
	public ResponseEntity<Map> getParamsById(@PathVariable(value="id") Integer paramsId){
		Map responseMap=new HashMap();
		Params paramsObj=new Params();
		
		try {
			Optional<Params> paramsOptional=paramsRepository.findById(paramsId);
			if(!paramsOptional.isPresent()) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMap", "params with Id="+paramsId+" not found");
				return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
			}
			paramsObj=paramsOptional.get();
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMap", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMap", "success");
		responseMap.put("params", paramsObj);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	
	@PostMapping("/getByType")
	public ResponseEntity<Map> getByType (@RequestBody Map paramMap) {
		Map responseMap=new HashMap();
		List<Params> paramsList= paramsRepository.findByType((String)paramMap.get("type"));
		try {
			if(paramsList.size()<1) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMap", "params with type="+(String)paramMap.get("type")+" not found");
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
		responseMap.put("params", paramsList);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	@PostMapping("/getByTypeAndCode")
	public ResponseEntity<Map> getparamsByParams (@RequestBody Map paramMap) {
		Map responseMap=new HashMap();
		Params paramsObj=new Params();
		String paramKey="";
		String paramValue="";
		try {
			Optional<Params> paramsOptional= paramsRepository.findByTypeAndCode((String)paramMap.get("type"), (String)paramMap.get("code"));
			if(!paramsOptional.isPresent()) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMap", "params with type="+(String)paramMap.get("type")+" and code="+(String)paramMap.get("code")+" not found");
				return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
			}
			paramsObj=paramsOptional.get();
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMap", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMap", "success");
		responseMap.put("params", paramsObj);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	@PostMapping("/add")
	public ResponseEntity<Map> create(@RequestBody Params paramsObj){
		Map responseMap=new HashMap();
		try {
			paramsRepository.save(paramsObj);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMap", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMap", "Success add params");
		responseMap.put("params", paramsObj);
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	
	@GetMapping("/deleteById/{id}")
	public ResponseEntity<Map> deleteparams (@PathVariable(value="id") Integer paramsId) {
		Map responseMap=new HashMap();
		Params paramsObj=new Params();
		try {
			Optional<Params> paramsOptional=paramsRepository.findById(paramsId);
			if(!paramsOptional.isPresent()) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMap", "params with Id:"+paramsId+" not found");
				return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
			}
			paramsObj=paramsOptional.get();
			paramsRepository.deleteById(paramsId);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMap", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		responseMap.put("responseCode", "00");
		responseMap.put("responseMap", "Success delete params");
		responseMap.put("params", paramsObj);
		
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	@PostMapping("/updateById/{id}")
	public ResponseEntity<Map> updateparamsById (@RequestBody Params paramsNew, @PathVariable(value="id") Integer paramsId) {
		Map responseMap=new HashMap();
		Params paramsFromDB = new Params();
		try {
			Optional<Params> paramsOptional=paramsRepository.findById(paramsId);
			if(!paramsOptional.isPresent()) {
				responseMap.put("responseCode", "01");
				responseMap.put("responseMap", "params with Id:"+paramsId+" not found");
				return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
			}
			paramsFromDB=paramsOptional.get();
			BaseUtil.setObjectFromObject(paramsFromDB, paramsNew);
			paramsRepository.save(paramsFromDB);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			responseMap.put("responseCode", "99");
			responseMap.put("responseMap", "failed");
			return new ResponseEntity(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		responseMap.put("responseCode", "00");
		responseMap.put("responseMap", "Success update params");
		responseMap.put("params", paramsFromDB);
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	

}

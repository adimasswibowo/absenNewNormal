package com.nnam.project.util;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import com.nnam.project.NewNormalAbsenApplication;
import com.nnam.project.model.Absen;
import com.nnam.project.model.Employee;
import com.nnam.project.model.Params;
import com.nnam.project.repository.EmployeeRepository;

public class BaseUtil {
	
	protected static BaseUtil instance = null;
	protected BaseUtil() {}

	/**
	 * @return  the instance
	 * @uml.property  name="instance"
	 */
	public static synchronized BaseUtil getInstance() {
		if (instance == null) {
			instance = new BaseUtil();
		}
		return instance;
	}
	
	public static void setObjectFromObject(Object obj1, Object obj2) {
		Field [] fields = obj2.getClass().getDeclaredFields();
		System.out.println("obj2 length : "+fields.length);
		for (int i=1; i<fields.length; i++) {
			Field field = fields[i];
			try {
				Object fieldValue = FieldUtils.readField(field, obj2, true);
				if(fieldValue!=null) {
					System.out.println("fieldValue : "+fieldValue);
					FieldUtils.writeField(obj1, field.getName(), fieldValue, true);
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				System.out.println("ex : "+e.getMessage());
			}
		}
		System.out.println("result object : "+obj1);
	}
	
	public static String constructNoteAbsen(Absen absenObj) {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		now.set(Calendar.HOUR_OF_DAY, 7);
		now.set(Calendar.MINUTE, 30);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		Date todayBegin=now.getTime();
		now.set(Calendar.HOUR_OF_DAY, 16);
		now.set(Calendar.MINUTE, 30);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		Date todayEnd=now.getTime();
		
		String note="";
		
		if(absenObj.getClockIn().before(todayBegin)){
			note="EARLY CLOCK IN";
		}else if(absenObj.getClockIn().after(todayBegin)){
			note="LATE CLOCK IN";
		}else {
			note="ONTIME CLOCK IN";
		}
		
		if (absenObj.getClockOut()!=null){
			if(absenObj.getClockOut().before(todayEnd)){
				note+=" - EARLY CLOCK OUT";
			}else if(absenObj.getClockOut().after(todayEnd)){
				note+=" - LATE CLOCK OUT";
			}else {
				note+=" - ONTIME CLOCK OUT";
			}
		}
		
		return note;
	}
	
	
	public static Boolean checkMandatoryKey(String mandatoryKey, Map dataMap) {
		Boolean result = false;
		if (mandatoryKey != null) {
		    String[] mandatoryKeyArray = mandatoryKey.split("\\|");
		    for (int i = 0; i < mandatoryKeyArray.length; i++) {
				if(dataMap.get(mandatoryKeyArray[i]) instanceof Double){
				    Double value = (Double) dataMap.get(mandatoryKeyArray[i]);
				    if (value!=null && value>0) result=true;
				}else if(dataMap.get(mandatoryKeyArray[i]) instanceof Integer){
					Integer value = (Integer) dataMap.get(mandatoryKeyArray[i]);
					if (value!=null && value>0) result=true;
				}else if(dataMap.get(mandatoryKeyArray[i]) instanceof Boolean){
					Boolean value = (Boolean) dataMap.get(mandatoryKeyArray[i]);
					if (value!=null) result=true;
				}else if(dataMap.get(mandatoryKeyArray[i]) instanceof String){
					String value = (String) dataMap.get(mandatoryKeyArray[i]);
					value=value.trim();
					if (value!=null && !"".equalsIgnoreCase(value)) result=true;
				}else if(dataMap.get(mandatoryKeyArray[i]) instanceof List){
					List value = (List) dataMap.get(mandatoryKeyArray[i]);
					if (value!=null && value.size()>0) result=true;
				}else if(dataMap.get(mandatoryKeyArray[i]) instanceof Map){
					Map value = (Map) dataMap.get(mandatoryKeyArray[i]);
					if (value!=null && value.size()>0) result=true;
				}else if(dataMap.get(mandatoryKeyArray[i]) instanceof Long){
					Long value = (Long) dataMap.get(mandatoryKeyArray[i]);
					if (value!=null && value>0) result=true;
				}else {
					result=false;
				}
				
				if (!result) return result;
		    }
		}
		
		return result;
	}
	
	
	public static Integer constructScoreAssessment(List<Params> questionList, Map resultMap) {
		Integer score=0;
		for(Params paramsObj : questionList) {
			Integer point=Integer.parseInt(paramsObj.getAdditionalInfo());
			if("Y".equalsIgnoreCase((String) resultMap.get(paramsObj.getCode()))) score+=point;
		}
		
		System.out.println("score :"+score);
		return score;
	}
	
	public static String constructNoteAssessment(Integer score) {
		String note=null;
		if(score==0) {
			note="LOW RISK - NO SUGGESTION";
		}else if(score>0 && score<5) {
			note="MEDIUM RISK - RAPPID TEST";
		}else if(score>=5) {
			note="HIGH RISK - SWAB TEST";
		}
		
		System.out.println("note :"+note);
		return note;
	}
	
	public static String constructResultAssessment(Integer score) {
		String result=null;
		if(score==0) {
			result="WFO";
		}else if(score>=5) {
			result="WFH";
		}
		
		System.out.println("result :"+result);
		return result;
	}
	
	public static void main(String[] args) {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		now.set(Calendar.HOUR_OF_DAY, 7);
		now.set(Calendar.MINUTE, 30);
		now.set(Calendar.SECOND, 0);
		Date todayBegin=now.getTime();
		now.set(Calendar.HOUR_OF_DAY, 16);
		now.set(Calendar.MINUTE, 30);
		now.set(Calendar.SECOND, 0);
		Date todayEnd=now.getTime();
		Absen absen=new Absen();
		try {
			absen.setClockIn(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("10/09/2020 07:30:00"));
//			absen.setClockOut(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("10/09/2020 16:30:00"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		System.out.println(absen.getClockIn());
		System.out.println(absen.getClockOut());
		System.out.println(todayBegin);
		System.out.println(todayEnd);
		String note=constructNoteAbsen(absen);
		System.out.println("note :"+note);
	}
		
		
}

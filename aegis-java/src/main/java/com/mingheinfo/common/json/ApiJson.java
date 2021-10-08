package com.mingheinfo.common.json;

import java.io.Serializable;

import org.slf4j.Logger;

import com.mingheinfo.common.status.StatusCode;

public class ApiJson implements Serializable{

	protected Boolean success=true;
	protected String code="200";
	protected String msg;
	protected Long timestamp;
	
	private Object result = null;// 返回的数据
	
	public Boolean getSuccess() {
		return success;
	} 

	public void setSuccess(Boolean success) {
		this.success = success;
	} 
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}



	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public void setError(StatusCode sc)
	{
		this.success = false;
		this.code = sc.getCode();
		this.msg = sc.getMessage();
		
	}
	
	public void setError(String code,String message)
	{
		this.success = false;
		this.code = code;
		this.msg = message;
	}	
	
	public void setError(Logger logger,String code,String message)
	{
		logger.error(message);
		this.success = false;
		this.code = code;
		this.msg = message;
	}		
	
}

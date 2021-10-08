package com.mingheinfo.common.exception;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mingheinfo.common.json.ApiJson;

@Slf4j
@RestControllerAdvice
public class RestCtrlExceptionHandler {
	
    Logger log = LoggerFactory.getLogger(RestCtrlExceptionHandler.class);
    
    @ExceptionHandler(MaxDataException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public ApiJson handleXCloudException(MaxDataException e) {
    	ApiJson ajaxJson = new ApiJson();
    	
        String errorMsg="MaxData exception";
        if (e!=null){
            errorMsg = e.getMsg();
            log.error(e.toString(), e);
        }
        
        ajaxJson.setError("500", errorMsg);
        return ajaxJson;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.OK)
    public ApiJson handleException(Exception e) {
    	ApiJson ajaxJson = new ApiJson();
    	
        String errorMsg="Exception";
        if (e!=null){
            errorMsg = e.getMessage();
            log.error(e.toString(), e);
        }
        ajaxJson.setError("500", errorMsg);
        
        return ajaxJson;
    }
}

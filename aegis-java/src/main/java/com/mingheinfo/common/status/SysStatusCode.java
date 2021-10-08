package com.mingheinfo.common.status;


public enum SysStatusCode implements StatusCode{

    DB_ERROR("001", "数据库操作错误"),
    NETWORK_ANOMALY_ERROR("002", "网络异常"),
	
	NO_INFO_EXISTS_ERROR("201", "信息不存在"),
	PARAM_INVALID_ERROR("201", "参数错误"),
	USER_PWD_ERROR("901", "用户密码错误");
	
	
	SysStatusCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private String code;
    private String message;
    
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
   

}

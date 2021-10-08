package com.mingheinfo.common.status;


public enum JwtStatusCode implements StatusCode{

    /**
     * token异常
     */
    TOKEN_ERROR("200", "token验证失败,请退出重新登录"),
    TOKEN_EXPIRED("201", "token过期,,请退出重新登录"),

    /**
     * 签名异常
     */
    SIGN_ERROR("203", "签名验证失败,请退出重新登录"),
	IP_NO_VISIT_ERROR("204", "该IP不可访问");

	JwtStatusCode(String code, String message) {
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

package com.mingheinfo.common.json;

import com.mingheinfo.common.status.JwtStatusCode;

public class JwtErrorJson extends ApiJson {
	
	public JwtErrorJson(JwtStatusCode statusEnum)
	{
		this.success = false;
		this.code = statusEnum.getCode();
		this.msg = statusEnum.getMessage();
		
	}

}

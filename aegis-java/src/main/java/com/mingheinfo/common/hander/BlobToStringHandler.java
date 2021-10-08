package com.mingheinfo.common.hander;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;


public class BlobToStringHandler extends BaseTypeHandler{
	
	//指定字符集
	private static final String DEFAULT_CHARSET = "gb2312";
	
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
			throws SQLException {
		try{
			String para = parameter.toString();
			ByteArrayInputStream bis = new ByteArrayInputStream(para.getBytes(DEFAULT_CHARSET));
			ps.setBinaryStream(i, bis, para.length());
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		
	}
	
	public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Blob blob = rs.getBlob(columnName);
		byte[] returnValue = null;
		String result = null;
		if (null != blob) {
			returnValue = blob.getBytes(1L, (int)blob.length());
		}
		try {
			if (returnValue != null) {
				int length = returnValue.length;
				for (int i = 0; i < returnValue.length; i++) {
					if (returnValue[i] == 0) {
						length = i;
						break;
					}
				}
				result = new String(returnValue,0,length, DEFAULT_CHARSET);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result; 
	}

	public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException{
		Blob blob = rs.getBlob(columnIndex);
		byte[] returnValue = null;
		String result = null;
		if (null != blob) {
			returnValue = blob.getBytes(1L, (int)blob.length());
		}
		try{
			if (returnValue != null) {
				result = new String(returnValue, DEFAULT_CHARSET);
			}
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	} 

	
	public String getNullableResult(CallableStatement cs,int columnIndex) throws SQLException {
		Blob blob = cs.getBlob(columnIndex);
		byte[] returnValue = null;
		String result = null;
		if (null != blob) {
			returnValue = blob.getBytes(1L, (int)blob.length());
		}
		try{
			if (returnValue != null) {
				result = new String(returnValue, DEFAULT_CHARSET);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		return result;
	}




}

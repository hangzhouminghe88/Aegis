package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.maxdata.base.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//主机信息
@Data
@TableName("host")
public class Host extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //主机名称
    private String name;
    //操作系统类型
    private String osType;
    //操作系统版本
    private String osVersion;
    
    //数据库类型
    private Integer dbType;
    //数据库名称
    private String dbName;
    //数据库角色
    private Integer dbRole;
    
    private String upDays;
    private Float upLoad;
    
    private String ipAddr;
    //用户名
    private String userName;
    //密码
    private String password;

    //private Date checkTime;
    
    //监控级别
    private Integer monLevel;
    //阈值自定义
    private String threshold;
    
    private String service;
    
    private Integer groupId;
    //数据库端口
    private String port;
    //数据库身份
    private String identity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public Integer getDbType() {
		return dbType;
	}

	public void setDbType(Integer dbType) {
		this.dbType = dbType;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public Integer getDbRole() {
		return dbRole;
	}

	public void setDbRole(Integer dbRole) {
		this.dbRole = dbRole;
	}

	public String getUpDays() {
		return upDays;
	}

	public void setUpDays(String upDays) {
		this.upDays = upDays;
	}

	public Float getUpLoad() {
		return upLoad;
	}

	public void setUpLoad(Float upLoad) {
		this.upLoad = upLoad;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

//	public Date getCheckTime() {
//		return checkTime;
//	}

//	public void setCheckTime(Date checkTime) {
//		this.checkTime = checkTime;
//	}

	public Integer getMonLevel() {
		return monLevel;
	}

	public void setMonLevel(Integer monLevel) {
		this.monLevel = monLevel;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public String getPort() {
		return this.port;
	}
	
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	public String getIdentity() {
		return this.identity;
	}
}
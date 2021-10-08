package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.maxdata.base.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//表空间
@Data
@TableName("table_space")
public class TableSpace implements Serializable{

    private static final long serialVersionUID = 1L;
    
    //创建时间
    @JsonFormat( pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    private String service;
    private String name;
    //存储总数(MB)
    private Float totalSpace;
    //使用(MB)
    private Float usedSpace;
    //空间(MB)
    private Float freeSpace;

    //使用率(%)
    private Float usedRate;

	public Date getGreateTime() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		System.out.println("create_time=====" + gmtCreate);
		this.gmtCreate = gmtCreate;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(Float totalSpace) {
		this.totalSpace = totalSpace;
	}

	public Float getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(Float usedSpace) {
		this.usedSpace = usedSpace;
	}

	public Float getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(Float freeSpace) {
		this.freeSpace = freeSpace;
	}

	public Float getUsedRate() {
		return usedRate;
	}

	public void setUsedRate(Float usedRate) {
		this.usedRate = usedRate;
	}
    
  
}
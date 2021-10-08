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

//段空间
@Data
@TableName("segment_space")
public class SegmentSpace implements Serializable{

    private static final long serialVersionUID = 1L;
    
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private String service;
    private String owner;
    
    private String segmentName;

    //使用(MB)
    private Float usedSpace;
    //增量(MB)
    private Float diffSpace;

    private String partitionName;
    private String segmentType;
    
    @JsonFormat( pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    
    @TableField(fill = FieldFill.INSERT)  
    private Date lastDdlTime;
    
    private String tableSpaceName;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getSegmentName() {
		return segmentName;
	}

	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}

	public Float getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(Float usedSpace) {
		this.usedSpace = usedSpace;
	}

	public Float getDiffSpace() {
		return diffSpace;
	}

	public void setDiffSpace(Float diffSpace) {
		this.diffSpace = diffSpace;
	}

	public String getPartitionName() {
		return partitionName;
	}

	public void setPartitionName(String partitionName) {
		this.partitionName = partitionName;
	}

	public String getSegmentType() {
		return segmentType;
	}

	public void setSegmentType(String segmentType) {
		this.segmentType = segmentType;
	}

	public Date getLastDdlTime() {
		return lastDdlTime;
	}

	public void setLastDdlTime(Date lastDdlTime) {
		this.lastDdlTime = lastDdlTime;
	}

	public String getTableSpaceName() {
		return tableSpaceName;
	}

	public void setTableSpaceName(String tableSpaceName) {
		this.tableSpaceName = tableSpaceName;
	}
    
    
  
}
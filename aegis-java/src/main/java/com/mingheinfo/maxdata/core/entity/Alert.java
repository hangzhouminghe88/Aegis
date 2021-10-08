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

//告警明细
@Data
@TableName("alert")
public class Alert implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId(value="id",type=IdType.AUTO)
    private Integer id;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    private String service;
    private String hostName;
    private String name;
    private String sname;
    private String type;
    private String errMsg;
    private String errType;
    private String errCode;
    private Integer errTimes;
    private Integer errLevel;
    private String sendUsers;
    //发送范围
    private String sendGroup;
    //异常处理人
    private String fixedBy;
    private Integer hostId;
    private String sourceMsg;

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrType() {
        return errType;
    }

    public void setErrType(String errType) {
        this.errType = errType;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public Integer getErrTimes() {
        return errTimes;
    }

    public void setErrTimes(Integer errTimes) {
        this.errTimes = errTimes;
    }

    public Integer getErrLevel() {
        return errLevel;
    }

    public void setErrLevel(Integer errLevel) {
        this.errLevel = errLevel;
    }

    public String getSendUsers() {
        return sendUsers;
    }

    public void setSendUsers(String sendUsers) {
        this.sendUsers = sendUsers;
    }

    public String getSendGroup() {
        return sendGroup;
    }

    public void setSendGroup(String sendGroup) {
        this.sendGroup = sendGroup;
    }

    public String getFixedBy() {
        return fixedBy;
    }

    public void setFixedBy(String fixedBy) {
        this.fixedBy = fixedBy;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public String getSourceMsg() {
        return sourceMsg;
    }

    public void setSourceMsg(String sourceMsg) {
        this.sourceMsg = sourceMsg;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }
}
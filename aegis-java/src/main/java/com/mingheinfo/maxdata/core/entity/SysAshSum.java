package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
@TableName("sys_ash_sum")
public class SysAshSum implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String service;
  //创建时间
  @JsonFormat(pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT)
  private Date gmtCreate;
  private String owner;
  private String objectName;
  private String objectType;
  private String waitClass;
  private String event;
  private Long totalWaits;
  private Long timeWaited;
  private Integer snapId;
  //创建时间
  @JsonFormat(pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT)
  private Date snapTime;

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public String getWaitClass() {
    return waitClass;
  }

  public void setWaitClass(String waitClass) {
    this.waitClass = waitClass;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public Long getTotalWaits() {
    return totalWaits;
  }

  public void setTotalWaits(Long totalWaits) {
    this.totalWaits = totalWaits;
  }

  public Long getTimeWaited() {
    return timeWaited;
  }

  public void setTimeWaited(Long timeWaited) {
    this.timeWaited = timeWaited;
  }

  public Integer getSnapId() {
    return snapId;
  }

  public void setSnapId(Integer snapId) {
    this.snapId = snapId;
  }

  public Date getSnapTime() {
    return snapTime;
  }

  public void setSnapTime(Date snapTime) {
    this.snapTime = snapTime;
  }
}

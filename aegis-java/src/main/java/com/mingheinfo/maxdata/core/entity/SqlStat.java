package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sql_stat")
public class SqlStat implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String service;
  private Integer snapId;
  @JsonFormat(pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT)
  private Date snapTime;
  private String sqlId;
  private Integer vc;
  private Float sortDelta;
  private Integer execDelta;
  private Float diskDelta;
  private Float buffDelta;
  private Float cpuTime;
  private Float elapTime;
  private Float fetchDelta;
  private Float rowsDelta;
  private Float iowaitDelta;
  private Float ccwaitDelta;
  private Float apwaitDelta;
  private String sqlFulltext;
  private Integer exec_1d;
  private Integer exec_7d;
  private String schemaName;
  private String module;
  private String scanType;
  private Float disk_1d;
  private Float disk_7d;
  private Float buff_1d;
  private Float buff_7d;
  private Float elap_1d;
  private Float elap_7d;
  private System isnew;
  private Float gfact;
  private Float execPer;
  private Float buffPer;
  private Float diskPer;
  private Float cputPer;
  private Float buffGfact;
  private Float sortPer;
  private Float parseDelta;
  private Float parsePer;
  private String appName;
  private Integer childNumber;
  private Float score;
  private Integer times;
  private Integer superby;

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
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

  public String getSqlId() {
    return sqlId;
  }

  public void setSqlId(String sqlId) {
    this.sqlId = sqlId;
  }

  public Integer getVc() {
    return vc;
  }

  public void setVc(Integer vc) {
    this.vc = vc;
  }

  public Float getSortDelta() {
    return sortDelta;
  }

  public void setSortDelta(Float sortDelta) {
    this.sortDelta = sortDelta;
  }

  public Integer getExecDelta() {
    return execDelta;
  }

  public void setExecDelta(Integer execDelta) {
    this.execDelta = execDelta;
  }

  public Float getDiskDelta() {
    return diskDelta;
  }

  public void setDiskDelta(Float diskDelta) {
    this.diskDelta = diskDelta;
  }

  public Float getBuffDelta() {
    return buffDelta;
  }

  public void setBuffDelta(Float buffDelta) {
    this.buffDelta = buffDelta;
  }

  public Float getCpuTime() {
    return cpuTime;
  }

  public void setCpuTime(Float cpuTime) {
    this.cpuTime = cpuTime;
  }

  public Float getElapTime() {
    return elapTime;
  }

  public void setElapTime(Float elapTime) {
    this.elapTime = elapTime;
  }

  public Float getFetchDelta() {
    return fetchDelta;
  }

  public void setFetchDelta(Float fetchDelta) {
    this.fetchDelta = fetchDelta;
  }

  public Float getRowsDelta() {
    return rowsDelta;
  }

  public void setRowsDelta(Float rowsDelta) {
    this.rowsDelta = rowsDelta;
  }

  public Float getIowaitDelta() {
    return iowaitDelta;
  }

  public void setIowaitDelta(Float iowaitDelta) {
    this.iowaitDelta = iowaitDelta;
  }

  public Float getCcwaitDelta() {
    return ccwaitDelta;
  }

  public void setCcwaitDelta(Float ccwaitDelta) {
    this.ccwaitDelta = ccwaitDelta;
  }

  public Float getApwaitDelta() {
    return apwaitDelta;
  }

  public void setApwaitDelta(Float apwaitDelta) {
    this.apwaitDelta = apwaitDelta;
  }

  public String getSqlFulltext() {
    return sqlFulltext;
  }

  public void setSqlFulltext(String sqlFulltext) {
    this.sqlFulltext = sqlFulltext;
  }

  public Integer getExec1d() {
    return exec_1d;
  }

  public void setExec1d(Integer exec1d) {
    this.exec_1d = exec1d;
  }

  public Integer getExec7d() {
    return exec_7d;
  }

  public void setExec7d(Integer exec7d) {
    this.exec_7d = exec7d;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getModule() {
    return module;
  }

  public void setModule(String module) {
    this.module = module;
  }

  public String getScanType() {
    return scanType;
  }

  public void setScanType(String scanType) {
    this.scanType = scanType;
  }

  public Float getDisk1d() {
    return disk_1d;
  }

  public void setDisk1d(Float disk1d) {
    this.disk_1d = disk1d;
  }

  public Float getDisk7d() {
    return disk_7d;
  }

  public void setDisk7d(Float disk7d) {
    this.disk_7d = disk7d;
  }

  public Float getBuff1d() {
    return buff_1d;
  }

  public void setBuff1d(Float buff1d) {
    this.buff_1d = buff1d;
  }

  public Float getBuff7d() {
    return buff_7d;
  }

  public void setBuff7d(Float buff7d) {
    this.buff_7d = buff7d;
  }

  public Float getElap1d() {
    return elap_1d;
  }

  public void setElap1d(Float elap1d) {
    this.elap_1d = elap1d;
  }

  public Float getElap7d() {
    return elap_7d;
  }

  public void setElap7d(Float elap7d) {
    this.elap_7d = elap7d;
  }

  public System getIsnew() {
    return isnew;
  }

  public void setIsnew(System isnew) {
    this.isnew = isnew;
  }

  public Float getGfact() {
    return gfact;
  }

  public void setGfact(Float gfact) {
    this.gfact = gfact;
  }

  public Float getExecPer() {
    return execPer;
  }

  public void setExecPer(Float execPer) {
    this.execPer = execPer;
  }

  public Float getBuffPer() {
    return buffPer;
  }

  public void setBuffPer(Float buffPer) {
    this.buffPer = buffPer;
  }

  public Float getDiskPer() {
    return diskPer;
  }

  public void setDiskPer(Float diskPer) {
    this.diskPer = diskPer;
  }

  public Float getCputPer() {
    return cputPer;
  }

  public void setCputPer(Float cputPer) {
    this.cputPer = cputPer;
  }

  public Float getBuffGfact() {
    return buffGfact;
  }

  public void setBuffGfact(Float buffGfact) {
    this.buffGfact = buffGfact;
  }

  public Float getSortPer() {
    return sortPer;
  }

  public void setSortPer(Float sortPer) {
    this.sortPer = sortPer;
  }

  public Float getParseDelta() {
    return parseDelta;
  }

  public void setParseDelta(Float parseDelta) {
    this.parseDelta = parseDelta;
  }

  public Float getParsePer() {
    return parsePer;
  }

  public void setParsePer(Float parsePer) {
    this.parsePer = parsePer;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public Integer getChildNumber() {
    return childNumber;
  }

  public void setChildNumber(Integer childNumber) {
    this.childNumber = childNumber;
  }

  public Float getScore() {
    return score;
  }

  public void setScore(Float score) {
    this.score = score;
  }

  public Integer getTimes() {
    return times;
  }

  public void setTimes(Integer times) {
    this.times = times;
  }

  public Integer getSuperby() {
    return superby;
  }

  public void setSuperby(Integer superby) {
    this.superby = superby;
  }
}

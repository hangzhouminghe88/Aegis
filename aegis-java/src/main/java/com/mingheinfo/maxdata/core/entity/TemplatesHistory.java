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
@TableName("templates_history")
public class TemplatesHistory implements Serializable
{
  public static final long serialVersionUID = 1L;
  //创建时间
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT)
  private Date gmtModified;
  private String dbName;
  private String name;
  private String sname;
  private Float currval;
  private Float lastval;
  private String service;
  private Integer superby;
  private Integer times;
  private String type;
  private Float last1d;
  private Float last7d;
  private Float score;
  private Float ratio;
  private Float minLimit;
  private Float maxLimit;
  private Float avgRatio;
  private String tags;

  public Date getGmtModified() {
    return gmtModified;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }

  public String getDbName() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
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

  public Float getCurrval() {
    return currval;
  }

  public void setCurrval(Float currval) {
    this.currval = currval;
  }

  public Float getLastval() {
    return lastval;
  }

  public void setLastval(Float lastval) {
    this.lastval = lastval;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public Integer getSuperby() {
    return superby;
  }

  public void setSuperby(Integer superby) {
    this.superby = superby;
  }

  public Integer getTimes() {
    return times;
  }

  public void setTimes(Integer times) {
    this.times = times;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Float getLast1d() {
    return last1d;
  }

  public void setLast1d(Float last1d) {
    this.last1d = last1d;
  }

  public Float getLast7d() {
    return last7d;
  }

  public void setLast7d(Float last7d) {
    this.last7d = last7d;
  }

  public Float getScore() {
    return score;
  }

  public void setScore(Float score) {
    this.score = score;
  }

  public Float getRatio() {
    return ratio;
  }

  public void setRatio(Float ratio) {
    this.ratio = ratio;
  }

  public Float getMinLimit() {
    return minLimit;
  }

  public void setMinLimit(Float minLimit) {
    this.minLimit = minLimit;
  }

  public Float getMaxLimit() {
    return maxLimit;
  }

  public void setMaxLimit(Float maxLimit) {
    this.maxLimit = maxLimit;
  }

  public Float getAvgRatio() {
    return avgRatio;
  }

  public void setAvgRatio(Float avgRatio) {
    this.avgRatio = avgRatio;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }
}

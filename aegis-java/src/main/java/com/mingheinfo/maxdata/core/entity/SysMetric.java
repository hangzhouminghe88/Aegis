package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_metric")
public class SysMetric implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String service;
  private Integer snapId;
  //创建时间
  @JsonFormat( pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT)
  private Date gmtCreate;
  private String metricName;
  private Double avg;
  private Double max;
  private Integer metricId;
  private Double sdval;
  private Double last1w;
  private Double last1wPer;
  private Double last1m;
  private Double last1mPer;
  private Double last1q;
  private Double last1qPer;

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

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public String getMetricName() {
    return metricName;
  }

  public void setMetricName(String metricName) {
    this.metricName = metricName;
  }

  public Double getAvg() {
    return avg;
  }

  public void setAvg(Double avg) {
    this.avg = avg;
  }

  public Double getMax() {
    return max;
  }

  public void setMax(Double max) {
    this.max = max;
  }

  public Integer getMetricId() {
    return metricId;
  }

  public void setMetricId(Integer metricId) {
    this.metricId = metricId;
  }

  public Double getSdval() {
    return sdval;
  }

  public void setSdval(Double sdval) {
    this.sdval = sdval;
  }

  public Double getLast1w() {
    return last1w;
  }

  public void setLast1w(Double last1w) {
    this.last1w = last1w;
  }

  public Double getLast1wPer() {
    return last1wPer;
  }

  public void setLast1wPer(Double last1wPer) {
    this.last1wPer = last1wPer;
  }

  public Double getLast1m() {
    return last1m;
  }

  public void setLast1m(Double last1m) {
    this.last1m = last1m;
  }

  public Double getLast1mPer() {
    return last1mPer;
  }

  public void setLast1mPer(Double last1mPer) {
    this.last1mPer = last1mPer;
  }

  public Double getLast1q() {
    return last1q;
  }

  public void setLast1q(Double last1q) {
    this.last1q = last1q;
  }

  public Double getLast1qPer() {
    return last1qPer;
  }

  public void setLast1qPer(Double last1qPer) {
    this.last1qPer = last1qPer;
  }
}

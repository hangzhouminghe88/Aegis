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
@TableName("sql_plan")
public class SqlPlan implements Serializable
{
  private static final long serialVersionUID = 1L;

  private String service;
  private String sqlId;
  private Long planHashValue;
  private Integer planId;
  private String planLine;
  @JsonFormat(pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT)
  private Date timestamp;
  private String planType;
  @JsonFormat(pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT)
  private Date gmtCreate;
  private Integer childNumber;
  private String objectName;
  private Integer depth;
  private String operation;
  private String options;
  private String cardinality;
  private String bytes;
  private String cost;
  private Long cpu_cost;
  private Long io_cost;
  private String temp_space;
  private String objectType;

  public String getService()
  {
    return service;
  }

  public void setService(String service)
  {
    this.service = service;
  }

  public String getSqlId()
  {
    return sqlId;
  }

  public void setSqlId(String sqlId)
  {
    this.sqlId = sqlId;
  }

  public Long getPlanHashValue()
  {
    return planHashValue;
  }

  public void setPlanHashValue(Long planHashValue)
  {
    this.planHashValue = planHashValue;
  }

  public Integer getPlanId()
  {
    return planId;
  }

  public void setPlanId(Integer planId)
  {
    this.planId = planId;
  }

  public String getPlanLine()
  {
    return planLine;
  }

  public void setPlanLine(String planLine)
  {
    this.planLine = planLine;
  }

  public Date getTimestamp()
  {
    return timestamp;
  }

  public void setTimestamp(Date timestamp)
  {
    this.timestamp = timestamp;
  }

  public String getPlanType()
  {
    return planType;
  }

  public void setPlanType(String planType)
  {
    this.planType = planType;
  }

  public Date getGmtCreate()
  {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate)
  {
    this.gmtCreate = gmtCreate;
  }

  public Integer getChildNumber()
  {
    return childNumber;
  }

  public void setChildNumber(Integer childNumber)
  {
    this.childNumber = childNumber;
  }

  public String getObjectName()
  {
    return objectName;
  }

  public void setObjectName(String objectName)
  {
    this.objectName = objectName;
  }

  public Integer getDepth()
  {
    return depth;
  }

  public void setDepth(Integer depth)
  {
    this.depth = depth;
  }

  public String getOperation()
  {
    return operation;
  }

  public void setOperation(String operation)
  {
    this.operation = operation;
  }

  public String getOptions()
  {
    return options;
  }

  public void setOptions(String options)
  {
    this.options = options;
  }

  public String getCardinality()
  {
    return cardinality;
  }

  public void setCardinality(String cardinality)
  {
    this.cardinality = cardinality;
  }

  public String getBytes()
  {
    return bytes;
  }

  public void setBytes(String bytes)
  {
    this.bytes = bytes;
  }

  public String getCost()
  {
    return cost;
  }

  public void setCost(String cost)
  {
    this.cost = cost;
  }

  public Long getCpu_cost()
  {
    return cpu_cost;
  }

  public void setCpu_cost(Long cpu_cost)
  {
    this.cpu_cost = cpu_cost;
  }

  public Long getIo_cost()
  {
    return io_cost;
  }

  public void setIo_cost(Long io_cost)
  {
    this.io_cost = io_cost;
  }

  public String getTemp_space()
  {
    return temp_space;
  }

  public void setTemp_space(String temp_space)
  {
    this.temp_space = temp_space;
  }

  public String getObjectType()
  {
    return objectType;
  }

  public void setObjectType(String objectType)
  {
    this.objectType = objectType;
  }
}

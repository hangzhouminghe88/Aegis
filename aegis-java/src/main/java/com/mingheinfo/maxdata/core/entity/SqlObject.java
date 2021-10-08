package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sql_object")
public class SqlObject implements Serializable
{
  private static final long serialVersionUID = 1L;

  private String service;
  private String objectName;
  private String objectType;
  private String planType;

  public String getService()
  {
    return service;
  }

  public void setService(String service)
  {
    this.service = service;
  }

  public String getObjectName()
  {
    return objectName;
  }

  public void setObjectName(String objectName)
  {
    this.objectName = objectName;
  }

  public String getObjectType()
  {
    return objectType;
  }

  public void setObjectType(String objectType)
  {
    this.objectType = objectType;
  }

  public String getPlanType()
  {
    return planType;
  }

  public void setPlanType(String planType)
  {
    this.planType = planType;
  }
}

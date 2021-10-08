package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("templates_summary")
public class TemplatesSummary implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Integer id;
  private Date gmtCreate;
  private String service;
  private String name;
  private String type;
  private Float currval;
  private Float currmax;
  private Float baseval;
  private Float basemax;
  private Float avgrate;
  private String dayname;

  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public Date getGmtCreate()
  {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate)
  {
    this.gmtCreate = gmtCreate;
  }

  public String getService()
  {
    return service;
  }

  public void setService(String service)
  {
    this.service = service;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public Float getCurrval()
  {
    return currval;
  }

  public void setCurrval(Float currval)
  {
    this.currval = currval;
  }

  public Float getCurrmax()
  {
    return currmax;
  }

  public void setCurrmax(Float currmax)
  {
    this.currmax = currmax;
  }

  public Float getBaseval()
  {
    return baseval;
  }

  public void setBaseval(Float baseval)
  {
    this.baseval = baseval;
  }

  public Float getBasemax()
  {
    return basemax;
  }

  public void setBasemax(Float basemax)
  {
    this.basemax = basemax;
  }

  public Float getAvgrate()
  {
    return avgrate;
  }

  public void setAvgrate(Float avgrate)
  {
    this.avgrate = avgrate;
  }

  public String getDayname()
  {
    return dayname;
  }

  public void setDayname(String dayname)
  {
    this.dayname = dayname;
  }
}

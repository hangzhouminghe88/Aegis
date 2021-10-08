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
@TableName("sql_machine")
public class SqlMachine implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String service;
  private String machine;
  private Integer cnt;
  //创建时间
  @JsonFormat(pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT)
  private Date gmtCreate;
  private String application;
  private String type;

  public String getService()
  {
    return service;
  }

  public void setService(String service)
  {
    this.service = service;
  }

  public String getMachine()
  {
    return machine;
  }

  public void setMachine(String machine)
  {
    this.machine = machine;
  }

  public Integer getCnt()
  {
    return cnt;
  }

  public void setCnt(Integer cnt)
  {
    this.cnt = cnt;
  }

  public Date getGmtCreate()
  {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate)
  {
    this.gmtCreate = gmtCreate;
  }

  public String getApplication()
  {
    return application;
  }

  public void setApplication(String application)
  {
    this.application = application;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }
}

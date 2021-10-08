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
@TableName("sql_module")
public class SqlModule implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Integer id;
  //创建时间
  @JsonFormat(pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT)
  private Date gmtCreate;
  private String service;
  private String application;
  private Integer moduleCnt;
  private Integer moduleMin;
  private Integer moduleMax;
  private Integer moduleSum;
  private Integer sqlTotal;
  private Integer execTotal;
  private Float execAvg;
  private Float moduleQps;
  private Float moduleAvg;
  private String type;

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

  public String getApplication()
  {
    return application;
  }

  public void setApplication(String application)
  {
    this.application = application;
  }

  public Integer getModuleCnt()
  {
    return moduleCnt;
  }

  public void setModuleCnt(Integer moduleCnt)
  {
    this.moduleCnt = moduleCnt;
  }

  public Integer getModuleMin()
  {
    return moduleMin;
  }

  public void setModuleMin(Integer moduleMin)
  {
    this.moduleMin = moduleMin;
  }

  public Integer getModuleMax()
  {
    return moduleMax;
  }

  public void setModuleMax(Integer moduleMax)
  {
    this.moduleMax = moduleMax;
  }

  public Integer getModuleSum()
  {
    return moduleSum;
  }

  public void setModuleSum(Integer moduleSum)
  {
    this.moduleSum = moduleSum;
  }

  public Integer getSqlTotal()
  {
    return sqlTotal;
  }

  public void setSqlTotal(Integer sqlTotal)
  {
    this.sqlTotal = sqlTotal;
  }

  public Integer getExecTotal()
  {
    return execTotal;
  }

  public void setExecTotal(Integer execTotal)
  {
    this.execTotal = execTotal;
  }

  public Float getExecAvg()
  {
    return execAvg;
  }

  public void setExecAvg(Float execAvg)
  {
    this.execAvg = execAvg;
  }

  public Float getModuleQps()
  {
    return moduleQps;
  }

  public void setModuleQps(Float moduleQps)
  {
    this.moduleQps = moduleQps;
  }

  public Float getModuleAvg()
  {
    return moduleAvg;
  }

  public void setModuleAvg(Float moduleAvg)
  {
    this.moduleAvg = moduleAvg;
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

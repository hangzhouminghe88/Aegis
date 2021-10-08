package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("alerts_results")
public class AlertsResults implements Serializable
{
  public static long serialVersionUID = 1L;
  @TableId(value="id",type=IdType.AUTO)
  @TableField
  public Integer id;
  @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Date gmtCreate;
  private Integer alertId;
  private String kind;
  private String subKind;
  private String fixedBy;
  private String fixedInfo;

  public Date getGmtCreate()
  {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate)
  {
    this.gmtCreate = gmtCreate;
  }

  public Integer getAlertId() {
    return alertId;
  }

  public void setAlertId(Integer alertId)
  {
    this.alertId = alertId;
  }

  public String getKind()
  {
    return kind;
  }

  public void setKind(String kind)
  {
    this.kind = kind;
  }

  public String getSubKind()
  {
    return subKind;
  }

  public void setSubKind(String subKind)
  {
    this.subKind = subKind;
  }

  public String getFixedBy()
  {
    return fixedBy;
  }

  public void setFixedBy(String fixedBy)
  {
    this.fixedBy = fixedBy;
  }

  public String getFixedInfo()
  {
    return fixedInfo;
  }

  public void setFixedInfo(String fixedInfo)
  {
    this.fixedInfo = fixedInfo;
  }
}

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
@TableName("templates_alerts")
public class TemplatesAlerts implements Serializable
{
  //创建时间
  @JsonFormat( pattern = "yyyy-MM-dd hh:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @TableField(fill = FieldFill.INSERT)
  private Date gmtCreate;
  private Integer hostId;
  private String hostName;
  private String dbName;
  private String name;
  private String sname;
  private Integer superby;
  private Integer times;
  private String service;
  private Float currval;
  private Float lastval;
  private Float last1d;
  private Float last7d;
  private Float score;
  private Float ratio;
  private Float mlimit;
}

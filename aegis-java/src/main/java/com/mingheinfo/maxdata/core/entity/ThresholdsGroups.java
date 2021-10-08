package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("thresholds_groups")
public class ThresholdsGroups implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Date gmtCreate;
  private Integer groupId;
  private String groupName;
  private Integer thresholdId;
  private String thresholdName;
  private Float baseval;

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Integer getGroupId() {
    return groupId;
  }

  public void setGroupId(Integer groupId) {
    this.groupId = groupId;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public Integer getThresholdId() {
    return thresholdId;
  }

  public void setThresholdId(Integer thresholdId) {
    this.thresholdId = thresholdId;
  }

  public String getThresholdName() {
    return thresholdName;
  }

  public void setThresholdName(String thresholdName) {
    this.thresholdName = thresholdName;
  }

  public Float getBaseval() {
    return baseval;
  }

  public void setBaseval(Float baseval) {
    this.baseval = baseval;
  }
}

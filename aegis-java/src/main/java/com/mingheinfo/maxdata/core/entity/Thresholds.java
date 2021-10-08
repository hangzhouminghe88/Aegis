package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("thresholds")
public class Thresholds  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Integer id;
  private Date gmtCreate;
  private String name;
  private String kind;
  private String define;
  private String comment;
  private String owner;
  private Float baseval;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Date getGmtCreate() {
    return gmtCreate;
  }

  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getDefine() {
    return define;
  }

  public void setDefine(String define) {
    this.define = define;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public Float getBaseval() {
    return baseval;
  }

  public void setBaseval(Float baseval) {
    this.baseval = baseval;
  }
}

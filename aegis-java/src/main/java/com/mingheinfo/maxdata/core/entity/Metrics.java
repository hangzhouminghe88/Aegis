package com.mingheinfo.maxdata.core.entity;

import java.io.Serializable;

public class Metrics implements Serializable
{
  public Long time;
  public Float currval;
  public Float lastval;
  public String name;
  public String type;
  public String service;
  public String sname;
  public Float last1d;
  public Float times;
  public Float score;

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public Float getCurrval() {
    return currval;
  }

  public void setCurrval(Float currval) {
    this.currval = currval;
  }

  public Float getLastval() {
    return lastval;
  }

  public void setLastval(Float lastval) {
    this.lastval = lastval;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getSname() {
    return sname;
  }

  public void setSname(String sname) {
    this.sname = sname;
  }

  public Float getLast1d() {
    return last1d;
  }

  public void setLast1d(Float last1d) {
    this.last1d = last1d;
  }

  public Float getTimes() {
    return times;
  }

  public void setTimes(Float times) {
    this.times = times;
  }

  public Float getScore() {
    return score;
  }

  public void setScore(Float scores) {
    this.score = scores;
  }
}

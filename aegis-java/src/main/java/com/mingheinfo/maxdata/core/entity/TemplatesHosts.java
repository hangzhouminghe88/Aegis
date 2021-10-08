package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
@TableName("templates_hosts")
public class TemplatesHosts implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat( pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;
    @JsonFormat( pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtModified;
    private String service;
    private String dbName;
    private Integer tempId;
    private String name;
    private String sname;
    private BigDecimal currval;
    private BigDecimal lastval;
    private Integer sortBy;
    private Integer superBy;
    private Integer times;
    private Integer hostId;
    private String hostName;
    private BigInteger value;
    private String type;
    private BigInteger timeWaited;
    private BigDecimal last1d;
    private BigDecimal last7d;
    private BigDecimal score;
    private BigDecimal ratio;
    private String tags;
    private Integer flag;
    private Integer level;
    private String kind;
    private Integer groupId;
    private String groupName;
    private BigDecimal minLimit;
    private BigDecimal maxLimit;
    private BigDecimal maxValue;
    private BigDecimal avgRatio;

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Integer getTempId() {
        return tempId;
    }

    public void setTempId(Integer tempId) {
        this.tempId = tempId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public BigDecimal getCurrval() {
        return currval;
    }

    public void setCurrval(BigDecimal currval) {
        this.currval = currval;
    }

    public BigDecimal getLastval() {
        return lastval;
    }

    public void setLastval(BigDecimal lastval) {
        this.lastval = lastval;
    }

    public Integer getSortBy() {
        return sortBy;
    }

    public void setSortBy(Integer sortBy) {
        this.sortBy = sortBy;
    }

    public Integer getSuperBy() {
        return superBy;
    }

    public void setSuperBy(Integer superBy) {
        this.superBy = superBy;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigInteger getTimeWaited() {
        return timeWaited;
    }

    public void setTimeWaited(BigInteger timeWaited) {
        this.timeWaited = timeWaited;
    }

    public BigDecimal getLast1d() {
        return last1d;
    }

    public void setLast1d(BigDecimal last1d) {
        this.last1d = last1d;
    }

    public BigDecimal getLast7d() {
        return last7d;
    }

    public void setLast7d(BigDecimal last7d) {
        this.last7d = last7d;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
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

    public BigDecimal getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(BigDecimal minLimit) {
        this.minLimit = minLimit;
    }

    public BigDecimal getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(BigDecimal maxLimit) {
        this.maxLimit = maxLimit;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public BigDecimal getAvgRatio() {
        return avgRatio;
    }

    public void setAvgRatio(BigDecimal avgRatio) {
        this.avgRatio = avgRatio;
    }
}

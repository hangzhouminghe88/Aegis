package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class TableMonHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private String owner;
    private String indexName;
    private String indexType;
    private String tableName;
    private String uniqueness;
    private String includeColumn;
    private String service;
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;
    private String usedStats;
    private Integer sqlTotal;
    private Integer colTotal;
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;
    private String reduStats;
    private  float segSize;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(String uniqueness) {
        this.uniqueness = uniqueness;
    }

    public String getIncludeColumn() {
        return includeColumn;
    }

    public void setIncludeColumn(String includeColumn) {
        this.includeColumn = includeColumn;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getUsedStats() {
        return usedStats;
    }

    public void setUsedStats(String usedStats) {
        this.usedStats = usedStats;
    }

    public Integer getSqlTotal() {
        return sqlTotal;
    }

    public void setSqlTotal(Integer sqlTotal) {
        this.sqlTotal = sqlTotal;
    }

    public Integer getColTotal() {
        return colTotal;
    }

    public void setColTotal(Integer colTotal) {
        this.colTotal = colTotal;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getReduStats() {
        return reduStats;
    }

    public void setReduStats(String reduStats) {
        this.reduStats = reduStats;
    }

    public float getSegSize() {
        return segSize;
    }

    public void setSegSize(float segSize) {
        this.segSize = segSize;
    }
}

package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("table_mon")
public class TableMon implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @TableId(value="id",type=IdType.AUTO)
    private Long id;
    
    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @JsonFormat( pattern = "yyyy-MM-dd hh:mm:ss")
    private Date gmtCreate;

    private String service;
    private String owner;
    private String tableName;
    private Long inss;
    private Long upds;
    private Long dels;
    private Long totalDmls;
    private String truncated;
    private Long numRows;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getInss() {
        return inss;
    }

    public void setInss(Long inss) {
        this.inss = inss;
    }

    public Long getUpds() {
        return upds;
    }

    public void setUpds(Long upds) {
        this.upds = upds;
    }

    public Long getDels() {
        return dels;
    }

    public void setDels(Long dels) {
        this.dels = dels;
    }

    public Long getTotalDmls() {
        return totalDmls;
    }

    public void setTotalDmls(Long totalDmls) {
        this.totalDmls = totalDmls;
    }

    public String getTruncated() {
        return truncated;
    }

    public void setTruncated(String truncated) {
        this.truncated = truncated;
    }

    public Long getNumRows() {
        return numRows;
    }

    public void setNumRows(Long numRows) {
        this.numRows = numRows;
    }
}
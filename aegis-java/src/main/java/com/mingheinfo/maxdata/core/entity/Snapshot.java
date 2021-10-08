package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.maxdata.base.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@TableName("snapshot")
public class Snapshot implements Serializable{

    private static final long serialVersionUID = 1L;
    
    //创建时间
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    
    private String service;
    private String reportName;
    private String dbName;
    private Integer beginSnap;
    private Integer endSnap;
    private Integer fscan;
    private String addmRptName;
    private Integer isNew;
    private Integer ins;
    private Integer upd;
    private Integer del;
    private Float lgrd;
    private Float phrd;
    private Float exec;
    private Integer finish;
    private Integer id;
    
    
}
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

//表空间统计
@Data
@TableName("table_space_sum")
public class TableSpaceSum implements Serializable{

    private static final long serialVersionUID = 1L;
    
    //创建时间
    @JsonFormat( pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    private String service;
    private String name;
    //存储总数(MB)
    private Float totalSpace;
    //使用(MB)
    private Float usedSpace;
    //空间(MB)
    private Float freeSpace;
    //增量(MB)
    private Float diffSpace;
    //使用率(%)
    private Float usedRate;
    //可用天数
    private Float usedDays;
    //均值(MB)
    private Float avgSpace;
    private Float growFact;
  
}
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
@TableName("table_mon_inds")
public class TableMonInds implements Serializable{

    private static final long serialVersionUID = 1L;
    
    //创建时间
    @JsonFormat( pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    private String service;
    private String owner;
    private String tableName;
    private Integer sqlTotal;
    private Integer indTotal;
    private Integer indUsed;
    private Integer indUnused;
    private Integer colMax;
    private Float colAvg;
    private Float totalSize;
    private Integer reduTotal;
    
}
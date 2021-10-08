package com.mingheinfo.maxdata.manage.entity;

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

//用户操作日志
@Data
@TableName("t_user_oper_log")
public class UserOperLog implements Serializable{

    private static final long serialVersionUID = 1L;
    @TableId(value="id",type=IdType.AUTO)
    private Integer id;
    
    private String remoteIp;

    private String reqUrl;
    
    private String reqType;
    
    private String type;
    
    private String param;
    
    private String description;
    
    private Integer result;
    
    //创建者
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;    
}
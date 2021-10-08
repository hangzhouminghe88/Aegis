package com.mingheinfo.maxdata.manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mingheinfo.maxdata.base.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;

//字典
@Data
@TableName("t_dict")
public class Dict implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @TableId(value="id",type=IdType.AUTO)
    private Integer id;
    
    //字典名称
    private String title;

    //字典类型
    private String type;

    //备注
    private String description;

    //排序值
    private BigDecimal sortOrder;
}
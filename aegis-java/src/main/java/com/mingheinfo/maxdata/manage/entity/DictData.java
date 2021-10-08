package com.mingheinfo.maxdata.manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mingheinfo.common.constant.CommonConstant;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("t_dict_data")

public class DictData implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId(value="id",type=IdType.AUTO)
    private Integer id;
    
    //数据名称
    private String title;

    //数据值
    private String value;

    //排序值
    private BigDecimal sortOrder;

    //是否启用 0启用 -1禁用
    private Integer status = CommonConstant.STATUS_NORMAL;

    //备注
    private String description;

    //所属字典
    private Integer dictId;
}
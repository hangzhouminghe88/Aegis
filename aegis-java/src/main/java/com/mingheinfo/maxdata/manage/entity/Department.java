package com.mingheinfo.maxdata.manage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.maxdata.base.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;

//部门
@Data
@TableName("t_department")
public class Department extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //部门名称
    private String title;

    //父id
    private Integer parentId;

    //是否为父节点(含子节点) 默认false
    private Boolean isParent = false;

    //排序值
    private BigDecimal sortOrder;

    //是否启用 0启用 -1禁用
    private Integer status = CommonConstant.STATUS_NORMAL;

    //父节点名称
    @TableField(exist=false)
    private String parentTitle;

    //主负责人
    @TableField(exist=false)
    private List<String> mainHeader;

    //副负责人
    @TableField(exist=false)
    private List<String> viceHeader;
}
package com.mingheinfo.maxdata.manage.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.maxdata.base.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 部门负责人
 */
@Data
@TableName("t_department_header")
public class DepartmentHeader extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //关联部门id
    private Integer departmentId;

    //关联部门负责人
    private String userId;

    //负责人类型 默认0主要 1副职
    private Integer type = CommonConstant.HEADER_TYPE_MAIN;
}
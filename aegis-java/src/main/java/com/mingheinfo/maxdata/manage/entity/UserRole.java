package com.mingheinfo.maxdata.manage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mingheinfo.maxdata.base.BaseEntity;

import lombok.Data;


/**
 * 用户角色
 */
@Data
@TableName("t_user_role")
public class UserRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //用户唯一id
    private Integer userId;

    //角色唯一id
    private Integer roleId;

    //角色名
    @TableField(exist=false)
    private String roleName;
}

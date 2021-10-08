package com.mingheinfo.maxdata.manage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.maxdata.base.BaseEntity;

import lombok.Data;

import java.util.List;

/**
 * 角色
 */
@Data
@TableName("t_role")
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //角色名 以ROLE_开头
    private String name;

    //是否为注册默认角色
    private Boolean defaultRole;

    private Integer dataType = CommonConstant.DATA_TYPE_ALL;

    //备注
    private String description;

    //拥有权限    
    @TableField(exist=false)
    private List<RolePermission> permissions;

    //拥有数据权限
    //@TableField(exist=false)

    //private List<RoleDepartment> departments;
}

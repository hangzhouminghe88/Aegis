package com.mingheinfo.maxdata.manage.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mingheinfo.maxdata.base.BaseEntity;

import lombok.Data;

//角色权限
@Data
@TableName("t_role_permission")
public class RolePermission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //角色id
    private Integer roleId;

    //权限id
    private Integer permissionId;
}
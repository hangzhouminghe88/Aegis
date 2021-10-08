package com.mingheinfo.maxdata.manage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.maxdata.base.BaseEntity;

import lombok.Data;

import java.util.List;

/**
 * 用户信息
 */
@Data
@TableName("t_user")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //用户名
    private String username;

    //密码
    private String password;

    //昵称
    private String nickName;

    //手机
    private String mobile;

    //邮件
    private String email;

    //省市县地址
    private String address;

    //街道地址
    private String street;

    //性别
    private String sex;

    //密码强度
    private String passStrength;

    //用户头像
    private String avatar = CommonConstant.USER_DEFAULT_AVATAR;

    //用户类型 0普通用户 1管理员
    private Integer type = CommonConstant.USER_TYPE_NORMAL;

    //状态 默认0正常 -1拉黑
    private Integer status = CommonConstant.USER_STATUS_NORMAL;

    //描述/详情/备注
    private String description;
    
    //部门ID
    private Integer departmentId;
    
    //所属部门名称
    @TableField(exist=false)
    private String departmentTitle;    

    //用户拥有角色
    @TableField(exist=false)
    private List<Role> roles;

    //用户拥有的权限    
    @TableField(exist=false)
    private List<Permission> permissions;
}

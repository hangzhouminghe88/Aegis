package com.mingheinfo.maxdata.manage.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mingheinfo.maxdata.base.BaseEntity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 菜单/权限
 * @author fuzy
 */
@Data
@TableName("t_permission")
public class Permission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //菜单名称
    private String name;

    //始终显示 默认是
    private Boolean showAlways = true;

    //层级
    private Integer level;

    //类型 0页面 1具体操作
    private Integer type;

    //菜单标题
    private String title;

    //页面路径/资源链接url
    private String path;

    //前端组件
    private String component;

    //图标
    private String icon;

    //按钮权限类型
    private String buttonType;

    //父id
    private Integer parentId;

    //说明备注
    private String description;

    //排序值
    private BigDecimal sortOrder;

    //是否启用 0启用 -1禁用
    private Integer status = 0;

    //网页链接
    private String url;

    //子菜单/权限
    @TableField(exist=false)
    private List<Permission> children;

    //页面拥有的权限类型
    @TableField(exist=false)
    private List<String> permTypes;

    //节点展开 前端所需
    @TableField(exist=false)
    private Boolean expand = true;

    //是否勾选 前端所需
    @TableField(exist=false)
    private Boolean checked = false;

    //是否选中 前端所需    
    @TableField(exist=false)
    private Boolean selected = false;
}
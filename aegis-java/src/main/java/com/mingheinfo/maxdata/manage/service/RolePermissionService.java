package com.mingheinfo.maxdata.manage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.manage.entity.RolePermission;
import com.mingheinfo.maxdata.manage.mapper.RolePermissionMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolePermissionService extends ServiceImpl<RolePermissionMapper, RolePermission> {

    public List<RolePermission> findByPermissionId(Integer permissionId) {

        return this.baseMapper.findByPermissionId(permissionId);
    }

    public List<RolePermission> findByRoleId(Integer roleId) {

        return this.baseMapper.findByRoleId(roleId);
    }

    public void deleteByRoleId(Integer roleId) {

    	this.baseMapper.deleteByRoleId(roleId);
    }
	
}

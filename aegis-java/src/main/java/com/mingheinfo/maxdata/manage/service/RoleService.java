package com.mingheinfo.maxdata.manage.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.manage.entity.Role;
import com.mingheinfo.maxdata.manage.mapper.RoleMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {

	public List<Role> getAll()
	{
		return this.baseMapper.getAll();
	}
	
    public List<Role> findByDefaultRole(Boolean defaultRole) {
        return this.baseMapper.findByDefaultRole(defaultRole);
    }
}

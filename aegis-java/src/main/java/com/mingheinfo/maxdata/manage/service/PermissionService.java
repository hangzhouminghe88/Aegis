package com.mingheinfo.maxdata.manage.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.manage.entity.Permission;
import com.mingheinfo.maxdata.manage.mapper.PermissionMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> {
    /**
     * 通过用户id获取
     * @param userId
     * @return
     */
    public List<Permission> findByUserId(Integer userId)
    {
    	return this.baseMapper.findByUserId(userId);
    }
    
    public List<Permission> findByLevelOrderBySortOrder(Integer level) {

        return this.baseMapper.findByLevelOrderBySortOrder(level);
    }

    public List<Permission> findByParentIdOrderBySortOrder(Integer parentId) {

        return this.baseMapper.findByParentIdOrderBySortOrder(parentId);
    }

    public List<Permission> findByTypeAndStatusOrderBySortOrder(Integer type, Integer status) {

        return this.baseMapper.findByTypeAndStatusOrderBySortOrder(type, status);
    }

    public List<Permission> findByTitle(String title) {

        return this.baseMapper.findByTitle(title);
    }

    public List<Permission> findByTitleLikeOrderBySortOrder(String title) {

        return this.baseMapper.findByTitleLikeOrderBySortOrder(title);
    }    
}

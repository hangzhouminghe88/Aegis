package com.mingheinfo.maxdata.manage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.manage.entity.Dict;
import com.mingheinfo.maxdata.manage.entity.Permission;
import com.mingheinfo.maxdata.manage.mapper.DictMapper;
import com.mingheinfo.maxdata.manage.mapper.PermissionMapper;

import java.util.List;

/**
 * 字典接口实现
 * 
 */
@Slf4j
@Service
@Transactional
public class DictService extends ServiceImpl<DictMapper, Dict> {

    public List<Dict> findAllOrderBySortOrder() {

        return this.baseMapper.findAllOrderBySortOrder();
    }

    public Dict findByType(String type) {

        return this.baseMapper.findByType(type);
    }

    public List<Dict> findByTitleOrTypeLike(String key) {

        return this.baseMapper.findByTitleOrTypeLike(key);
    }
}
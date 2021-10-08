package com.mingheinfo.maxdata.manage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.manage.entity.DepartmentHeader;
import com.mingheinfo.maxdata.manage.entity.DictData;
import com.mingheinfo.maxdata.manage.mapper.DepartmentHeaderMapper;
import com.mingheinfo.maxdata.manage.mapper.DictDataMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门负责人接口实现
 * 
 */
@Slf4j
@Service
@Transactional
public class DepartmentHeaderService extends ServiceImpl<DepartmentHeaderMapper, DepartmentHeader> {

    public List<String> findHeaderByDepartmentId(Integer departmentId, Integer type) {

        List<String> list = new ArrayList<>();
        List<DepartmentHeader> headers = this.baseMapper.findByDepartmentIdAndType(departmentId, type);
        headers.forEach(e->{
            list.add(e.getUserId());
        });
        return list;
    }

    public void deleteByDepartmentId(Integer departmentId) {

    	this.baseMapper.deleteByDepartmentId(departmentId);
    }

    public void deleteByUserId(Integer userId) {

    	this.baseMapper.deleteByUserId(userId);
    }
}
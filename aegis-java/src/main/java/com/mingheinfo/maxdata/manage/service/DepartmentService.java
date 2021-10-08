package com.mingheinfo.maxdata.manage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.common.utils.SecurityUtil;
import com.mingheinfo.maxdata.manage.entity.Department;
import com.mingheinfo.maxdata.manage.entity.DepartmentHeader;
import com.mingheinfo.maxdata.manage.mapper.DepartmentHeaderMapper;
import com.mingheinfo.maxdata.manage.mapper.DepartmentMapper;

import java.util.List;

/**
 * 部门接口实现
 * 
 */
@Slf4j
@Service
@Transactional
public class DepartmentService extends ServiceImpl<DepartmentMapper, Department> {
    @Autowired
    private SecurityUtil securityUtil;


    public List<Department> findByParentIdOrderBySortOrder(Integer parentId, Boolean openDataFilter) {

        // 数据权限
        List<String> depIds = securityUtil.getDeparmentIds();
        if(depIds!=null&&depIds.size()>0&&openDataFilter){
        	String ids = String.join(",", depIds.toArray(new String[depIds.size()]));
            return this.baseMapper.findByParentIdAndIdInOrderBySortOrder(parentId, ids);
        }
        return this.baseMapper.findByParentIdOrderBySortOrder(parentId);
    }

    public List<Department> findByParentIdAndStatusOrderBySortOrder(Integer parentId, Integer status) {

        return this.baseMapper.findByParentIdAndStatusOrderBySortOrder(parentId, status);
    }

    public List<Department> findByTitleLikeOrderBySortOrder(String title, Boolean openDataFilter) {

        // 数据权限
        List<String> depIds = securityUtil.getDeparmentIds();
        if(depIds!=null&&depIds.size()>0&&openDataFilter){
        	String ids = String.join(",", depIds.toArray(new String[depIds.size()]));
            return this.baseMapper.findByTitleLikeAndIdInOrderBySortOrder(title, ids);
        }
        return this.baseMapper.findByTitleLikeOrderBySortOrder(title);
    }
}
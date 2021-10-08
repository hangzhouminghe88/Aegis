package com.mingheinfo.maxdata.manage.service;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.maxdata.manage.entity.Dict;
import com.mingheinfo.maxdata.manage.entity.DictData;
import com.mingheinfo.maxdata.manage.mapper.DictDataMapper;
import com.mingheinfo.maxdata.manage.mapper.DictMapper;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 字典数据接口实现
 * 
 */
@Slf4j
@Service
@Transactional
public class DictDataService extends ServiceImpl<DictDataMapper, DictData> {

    public Page<DictData> findByCondition(DictData dictData, Pageable pageable) {
    	
    	return null;
    	/*

        return this.baseMapper.findAll(new Specification<DictData>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<DictData> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> titleField = root.get("title");
                Path<Integer> statusField = root.get("status");
                Path<String> dictIdField = root.get("dictId");

                List<Predicate> list = new ArrayList<Predicate>();

                //模糊搜素
                if(StrUtil.isNotBlank(dictData.getTitle())){
                    list.add(cb.like(titleField,'%'+dictData.getTitle()+'%'));
                }

                //状态
                if(dictData.getStatus()!=null){
                    list.add(cb.equal(statusField, dictData.getStatus()));
                }

                //所属字典
                if(StrUtil.isNotBlank(dictData.getDictId())){
                    list.add(cb.equal(dictIdField, dictData.getDictId()));
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);*/
    }

    public List<DictData> findByDictId(Integer dictId) {

        return this.baseMapper.findByDictIdAndStatusOrderBySortOrder(dictId, CommonConstant.STATUS_NORMAL);
    }

    public void deleteByDictId(String dictId) {

    	this.baseMapper.deleteByDictId(dictId);
    }
}
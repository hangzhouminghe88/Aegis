package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.DictIndexes;
import com.mingheinfo.maxdata.core.mapper.DictIndexesMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class DictIndexesService extends ServiceImpl<DictIndexesMapper, DictIndexes> {
    public void deleteService() {
        this.baseMapper.deleteService();
    }

    public void truncate() {
        this.baseMapper.truncate();
    }
}

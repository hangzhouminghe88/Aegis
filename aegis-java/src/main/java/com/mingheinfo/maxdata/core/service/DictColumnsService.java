package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.DictColumns;
import com.mingheinfo.maxdata.core.mapper.DictColumnsMapper;
import org.springframework.stereotype.Service;

@Service
public class DictColumnsService extends ServiceImpl<DictColumnsMapper, DictColumns> {
    public void deleteService() {
        this.baseMapper.deleteService();
    }
    public void truncate() {
        this.baseMapper.truncate();
    }
}

package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.DictIndCols;
import com.mingheinfo.maxdata.core.mapper.DictIndColsMapper;
import org.springframework.stereotype.Service;

@Service
public class DictIndColsService extends ServiceImpl<DictIndColsMapper, DictIndCols> {
    public void deleteService() {
        this.baseMapper.deleteService();
    }
    public void truncate() {
        this.baseMapper.truncate();
    }
    public void callSqlIndCols(String service)
    {
        this.baseMapper.callSqlIndCols(service);
    }
}

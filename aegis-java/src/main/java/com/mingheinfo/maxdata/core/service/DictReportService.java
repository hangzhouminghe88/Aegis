package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.DictReport;
import com.mingheinfo.maxdata.core.mapper.DictReportMapper;
import org.springframework.stereotype.Service;

@Service
public class DictReportService extends ServiceImpl<DictReportMapper, DictReport> {
    public void deleteService()
    {
        this.baseMapper.deleteService();
    }

    public void truncate() {
        this.baseMapper.truncate();
    }
}
package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.SqlText;
import com.mingheinfo.maxdata.core.mapper.SqlTextMapper;
import org.springframework.stereotype.Service;

@Service
public class SqlTextService extends ServiceImpl<SqlTextMapper, SqlText> {
    public void deleteSqlText(String service)
    {
        this.baseMapper.deleteSqlText(service);
    }
    public void deleteSqlTextByType(String service, String type)
    {
        this.baseMapper.deleteSqlTextByType(service, type);
    }
}

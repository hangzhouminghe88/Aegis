package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.SqlModule;
import com.mingheinfo.maxdata.core.mapper.SqlModuleMapper;
import org.springframework.stereotype.Service;

@Service

public class SqlModuleService extends ServiceImpl<SqlModuleMapper, SqlModule>
{
  public void callSqlModule()
  {
    this.baseMapper.callSqlModule();
  }
}

package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.SqlPlan;
import com.mingheinfo.maxdata.core.mapper.SqlPlanMapper;
import org.springframework.stereotype.Service;

@Service
public class SqlPlanService extends ServiceImpl<SqlPlanMapper, SqlPlan>
{
  public void deleteSqlPlan(String service)
  {
    this.baseMapper.deleteSqlPlan(service);
  }
}

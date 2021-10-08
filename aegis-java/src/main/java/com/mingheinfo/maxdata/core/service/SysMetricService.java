package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.SysMetric;
import com.mingheinfo.maxdata.core.mapper.SysMetricMapper;
import org.springframework.stereotype.Service;

@Service
public class SysMetricService extends ServiceImpl<SysMetricMapper, SysMetric>
{
  public void callSqlStat(String service)
  {
    this.baseMapper.callSqlStat(service);
  }

  public void callSysMetric(String service)
  {
    this.baseMapper.callSysMetric(service);
  }
}

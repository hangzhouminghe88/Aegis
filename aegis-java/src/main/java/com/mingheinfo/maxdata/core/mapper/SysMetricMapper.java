package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SysMetric;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysMetricMapper extends BaseMapper<SysMetric>
{
  @Select("call sql_sqlstat(#{service})")
  void callSqlStat(String service);
  @Select("call sql_sysmetric(#{service})")
  void callSysMetric(String service);
}

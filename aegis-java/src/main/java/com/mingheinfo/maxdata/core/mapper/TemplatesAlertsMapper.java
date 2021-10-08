package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.TemplatesAlerts;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TemplatesAlertsMapper extends BaseMapper<TemplatesAlerts>
{
  List<Map> queryTrendLog(String service, String gmtCreate, Integer pageNumber, Integer size, String name);
  Integer queryTrendLogCount(String service, String gmtCreate, String name);
}

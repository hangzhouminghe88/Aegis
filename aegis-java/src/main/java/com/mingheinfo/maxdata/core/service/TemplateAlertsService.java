package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.TemplatesAlerts;
import com.mingheinfo.maxdata.core.mapper.TemplatesAlertsMapper;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TemplateAlertsService extends ServiceImpl<TemplatesAlertsMapper, TemplatesAlerts>
{
  public List<Map> queryTrendLog(String service, String gmtCreate, Integer pageNumber, Integer pageSize, String name)
  {
    return this.baseMapper.queryTrendLog(service, gmtCreate, pageNumber , pageSize, name);
  }
  public Integer queryTrendLogCount(String service, String gmtCreate, String name)
  {
    return this.baseMapper.queryTrendLogCount(service, gmtCreate, name);
  }
}

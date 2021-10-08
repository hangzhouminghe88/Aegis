package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.common.utils.DateUtils;
import com.mingheinfo.maxdata.core.entity.Metrics;
import com.mingheinfo.maxdata.core.entity.Template;
import com.mingheinfo.maxdata.core.entity.TemplatesHistory;
import com.mingheinfo.maxdata.core.mapper.TemplatesHistoryMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service

public class TemplatesHistoryService extends ServiceImpl<TemplatesHistoryMapper, TemplatesHistory> {
  public List<Metrics> queryCurvesList(String service, String startTime, String endTime, String tags, String name, String stamp)
  {
    return this.baseMapper.queryCurvesList(service, startTime, endTime, tags, name, stamp);
  }

  public Integer queryCurvesListCount(String service, String startTime, String endTime, String tags, String name)
  {
    return this.baseMapper.queryCurvesListCount(service, startTime, endTime, tags, name);
  }

  public List<Template> queryTemplateName(String service, Integer pageNumber, Integer size, String tags, String name, String type)
  {
    return this.baseMapper.queryTemplateName(service, (pageNumber - 1) * size, size, tags, name, type);
  }

  public Long queryTemplateNameCount(String service, String tags, String name, String type)
  {
    return this.baseMapper.queryTemplateNameCount(service, tags, name, type);
  }

  public List queryCurvesDetailList(String service, String startTime, String endTime, String tags, String name, String stamp)
  {
    return this.baseMapper.queryCurvesDetailList(service, startTime, endTime, tags, name, stamp);
  }

  public List<Metrics> queryTrendList(String service,String sname,String name,String type,String tags,String metricType, String hostIds)
  {
    return this.baseMapper.queryTrendList(service, sname, name, type , tags, metricType, hostIds);
  }

  public List<Template> queryTemplateNameToTrend(String service, Integer pageNumber, Integer size, String tags, String name, String hostIds)
  {
    return this.baseMapper.queryTemplateNameToTrend(service, (pageNumber - 1) * size, size, tags, name, hostIds);
  }

  public Long queryTemplateNameToTrendCount(String service, String tags, String name, String hostIds)
  {
    return this.baseMapper.queryTemplateNameToTrendCount(service, tags, name, hostIds);
  }

  public List<Metrics> queryCurvesMetric(String service, String sname, String name, String type, String tags, String metricType)
  {
    return this.baseMapper.queryCurvesMetric(service, sname, name, type, tags, metricType);
  }

  public Template queryTemplate(String name, String sname)
  {
    return this.baseMapper.queryTemplate(name, sname);
  }

  public List<String> queryServiceByName()
  {
    return this.baseMapper.queryServiceByName();
  }

  public List<Template>  queryTemplateAllName(String name, String tags)
  {
    return this.baseMapper.queryTemplateAllName(name, tags);
  }
  
  public List<Metrics> getGt1DayMetrics(String service, String sname, String name, String type, String tags, String metricType) 
  {
	 return this.baseMapper.getGt1DayMetrics(service, sname, name, type, tags, metricType);
  }
}

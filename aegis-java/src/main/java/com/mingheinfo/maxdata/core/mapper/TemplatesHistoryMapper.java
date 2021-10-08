package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.Metrics;
import com.mingheinfo.maxdata.core.entity.Template;
import com.mingheinfo.maxdata.core.entity.TemplatesHistory;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface TemplatesHistoryMapper extends BaseMapper<TemplatesHistory>
{
  List<Metrics> queryCurvesList(String service, String startTime, String endTime, String tags, String name, String stamp);
  Integer queryCurvesListCount(String service, String startTime, String endTime, String tags, String name);
  List<Template> queryTemplateName(String service, Integer pageNumber, Integer size, String tags, String name, String type);
  Long queryTemplateNameCount(String service, String tags, String name,  String type);
  List queryCurvesDetailList(String service, String startTime, String endTime, String tags, String name, String stamp);
  List<Metrics> queryTrendList(String service,String sname,String name,String type,String tags,String metricType, String hostIds);
  List<Template> queryTemplateNameToTrend(String service, Integer pageNumber, Integer size, String tags, String name, String hostIds);
  Long queryTemplateNameToTrendCount(String service, String tags, String name, String hostIds);
  List<Metrics> queryCurvesMetric(String service, String sname, String name, String type, String tags, String metricType);
  Template queryTemplate(String name, String sname);
  List<String> queryServiceByName();
  List<Template>  queryTemplateAllName(String name, String tags);
  List<Metrics> getGt1DayMetrics(String service, String sname, String name, String type, String tags, String metricType);
}

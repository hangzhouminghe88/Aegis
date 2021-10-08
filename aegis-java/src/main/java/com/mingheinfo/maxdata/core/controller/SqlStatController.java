package com.mingheinfo.maxdata.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.common.utils.SpringContextUtils;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.core.entity.SqlStat;
import com.mingheinfo.maxdata.core.entity.SysMetric;
import com.mingheinfo.maxdata.core.service.SqlStatService;
import com.mingheinfo.maxdata.core.service.SysMetricService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;

@Slf4j
@Transactional
@RestController
@RequestMapping("/api/sqlstat")
public class SqlStatController
{
  @Autowired
  private SqlStatService sqlStatService;
  @Autowired
  private SysMetricService sysMetricService;
  //分页获取
  @RequestMapping(value = "/topsql",method = RequestMethod.GET)
  public ApiJson getTopSqlByPage(PageVo pageVo, String sqlId, String service, String snapId)
  {

    IPage<SqlStat> page = new Page(pageVo.getPageNumber(),pageVo.getPageSize());

    QueryWrapper<SqlStat> queryWrapper = new QueryWrapper<SqlStat>();

    if(StringUtils.isNotBlank(service))
    {
      queryWrapper.eq("service", service);
    }

    if(StringUtils.isNotBlank(snapId))
    {
      queryWrapper.eq("snap_id", snapId);
    }
    if(StringUtils.isNotBlank(sqlId)) {
      queryWrapper.eq("sql_id", sqlId);
    }
    if(StringUtils.isNotBlank(pageVo.getSort()) && StringUtils.isNotBlank(pageVo.getOrder()))
    {
      if(pageVo.getOrder().toLowerCase().equals("asc"))
      {
        queryWrapper.orderByAsc(pageVo.getSort());
      }
      else
      {
        queryWrapper.orderByDesc(pageVo.getSort());
      }
    }
    System.out.println(queryWrapper.getSqlSelect());
    page = sqlStatService.page(page, queryWrapper);
    return ResultUtil.data(page);
  }

  //分页获取
  @RequestMapping(value = "/metric",method = RequestMethod.GET)
  public ApiJson getSysMetricByPage(PageVo pageVo, String metricName, String service, String snapId)
  {

    IPage<SysMetric> page = new Page(pageVo.getPageNumber(),pageVo.getPageSize());

    QueryWrapper<SysMetric> queryWrapper = new QueryWrapper<SysMetric>();

    if(StringUtils.isNotBlank(service))
    {
      queryWrapper.eq("service", service);
    }

    if(StringUtils.isNotBlank(snapId))
    {
      queryWrapper.eq("snap_id", snapId);
    }
    if(StringUtils.isNotBlank(metricName)) {
      queryWrapper.apply("lower(metric_name) like {0}", "%"+metricName.toLowerCase()+"%");
    }
    if(StringUtils.isNotBlank(pageVo.getSort()) && StringUtils.isNotBlank(pageVo.getOrder()))
    {
      if(pageVo.getOrder().toLowerCase().equals("asc"))
      {
        queryWrapper.orderByAsc(pageVo.getSort());
      }
      else
      {
        queryWrapper.orderByDesc(pageVo.getSort());
      }
    }
    page = sysMetricService.page(page, queryWrapper);;
    return ResultUtil.data(page);
  }
}

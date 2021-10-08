package com.mingheinfo.maxdata.core.controller;

import com.mingheinfo.common.utils.DateUtils;
import com.mingheinfo.maxdata.core.entity.SqlBind;
import com.mingheinfo.maxdata.core.entity.SqlSummaryReport;
import com.mingheinfo.maxdata.core.service.SqlBindService;
import com.mingheinfo.maxdata.core.service.SqlSummaryReportService;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.common.vo.DbParameter;
import com.mingheinfo.common.vo.DbSpfile;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.core.entity.SqlSummary;
import com.mingheinfo.maxdata.core.service.SqlSummaryService;

import cn.hutool.core.codec.Base64;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 段空间统计
 */
@Slf4j
@RestController
@RequestMapping("/api/sqlSummary")
@Transactional
public class SqlSummaryController {

    @Autowired
    private SqlSummaryService sqlSummaryService;

    @Autowired
    private SqlSummaryReportService sqlSummaryReportService;

    @Autowired
    private SqlBindService sqlBindService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getSqlSummaryByPage(PageVo pageVo,String name,String service, String gmtCreate){

    	IPage<SqlSummary> page = new Page<SqlSummary>(pageVo.getPageNumber(),pageVo.getPageSize());

    	QueryWrapper<SqlSummary> queryWrapper = new QueryWrapper<SqlSummary>();

    	if(StringUtils.isNotBlank(service))
    	{
    		queryWrapper.eq("service", service);
	   	}

    	if(StringUtils.isNotBlank(name))
    	{
    		queryWrapper.apply("lower(concat(sql_text,sql_id,module,app_name)) like {0}", "%"+name+"%");
    	}
    	if(StringUtils.isNotBlank(gmtCreate)) {
    	  queryWrapper.eq("gmt_create", gmtCreate);
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
    	page = sqlSummaryService.page(page, queryWrapper);;
    	return ResultUtil.data(page);
    }

  //分页获取
  @RequestMapping(value = "/detail",method = RequestMethod.GET)
  public ApiJson getSqlSummaryDetailByPage(PageVo pageVo,String sqlId,String service, String gmtCreate){

    IPage<SqlSummary> page = new Page<SqlSummary>(pageVo.getPageNumber(),pageVo.getPageSize());

    QueryWrapper<SqlSummary> queryWrapper = new QueryWrapper<SqlSummary>();



    if(StringUtils.isNotBlank(service))
    {
      queryWrapper.eq("service", service);
    }
    if(StringUtils.isNotBlank(sqlId)) {
      queryWrapper.eq("sql_id", sqlId);
    }
    if(StringUtils.isNotBlank(gmtCreate)){
      queryWrapper.between("gmt_create", gmtCreate, DateUtils.getDate());
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

    page = sqlSummaryService.page(page, queryWrapper);

    return ResultUtil.data(page);
  }

    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(SqlSummary entity){

    	sqlSummaryService.save(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(SqlSummary entity){

    	sqlSummaryService.updateById(entity);

        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	sqlSummaryService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }

    //SQL区段分析报表
  @RequestMapping(value="/summary", method = RequestMethod.GET)
  public ApiJson getSQLActionSummary(PageVo pageVo, String service, String name)
  {
    IPage<SqlSummaryReport> page = new Page<SqlSummaryReport>(pageVo.getPageNumber(),pageVo.getPageSize());

    QueryWrapper<SqlSummaryReport> queryWrapper = new QueryWrapper<SqlSummaryReport>();
    if(StringUtils.isNotBlank(name))
    {
      queryWrapper.eq("gmt_create", name);
    }
    if(StringUtils.isNotBlank(service))
    {
      queryWrapper.eq("service", service.trim());
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
    page = sqlSummaryReportService.page(page, queryWrapper);;
    return ResultUtil.data(page);
  }

  //SQL区段分析报表
  @RequestMapping(value="/sqlbind", method = RequestMethod.GET)
  public ApiJson getSQLActionSqlBind(PageVo pageVo, String service, String name)
  {
    IPage<SqlBind> page = new Page<SqlBind>(pageVo.getPageNumber(),pageVo.getPageSize());

    QueryWrapper<SqlBind> queryWrapper = new QueryWrapper<SqlBind>();
    if(StringUtils.isNotBlank(name))
    {
      queryWrapper.eq("sql_text", name);
    }
    if(StringUtils.isNotBlank(service))
    {
      queryWrapper.eq("service", service);
    }
    queryWrapper.apply("gmt_create >= curdate()");
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
    page = sqlBindService.page(page, queryWrapper);;
    return ResultUtil.data(page);
  }
}

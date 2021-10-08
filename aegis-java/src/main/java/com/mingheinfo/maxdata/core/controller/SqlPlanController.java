package com.mingheinfo.maxdata.core.controller;

import com.mingheinfo.maxdata.core.entity.SqlPlan;
import com.mingheinfo.maxdata.core.service.SqlPlanService;
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
@RequestMapping("/api/sqlplan")
@Transactional
public class SqlPlanController {

  @Autowired
  private SqlPlanService sqlPlanService;

  @Autowired
  private StringRedisTemplate redisTemplate;

  //分页获取
  @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
  public ApiJson getSqlSummaryByPage(PageVo pageVo,String name,String service, String sqlId){

    IPage<SqlPlan> page = new Page<SqlPlan>();

    QueryWrapper<SqlPlan> queryWrapper = new QueryWrapper<SqlPlan>();

    if(StringUtils.isNotBlank(service))
    {
      queryWrapper.eq("service", service);
    }
    if(StringUtils.isNotBlank(name)) {
      queryWrapper.eq("operation", name);
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
    page = sqlPlanService.page(page, queryWrapper);
    return ResultUtil.data(page);
  }

  //保存数据
  @RequestMapping(value = "/save",method = RequestMethod.POST)
  public ApiJson save(SqlPlan entity){

    sqlPlanService.save(entity);
    return ResultUtil.success();
  }

  //更新数据
  @RequestMapping(value = "/edit",method = RequestMethod.POST)
  public ApiJson edit(SqlPlan entity){

    sqlPlanService.updateById(entity);

    return ResultUtil.data(entity);
  }

  //批量通过ids删除
  @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
  public ApiJson delByIds(@PathVariable Integer[] ids){

    for(Integer id:ids){
      sqlPlanService.removeById(id);
    }
    return ResultUtil.success("批量通过id删除数据成功");
  }
}

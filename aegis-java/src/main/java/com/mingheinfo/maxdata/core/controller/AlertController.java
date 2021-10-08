package com.mingheinfo.maxdata.core.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mingheinfo.maxdata.core.entity.AlertsResults;
import com.mingheinfo.maxdata.core.service.AlertsResultsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpRequest;
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
import com.mingheinfo.maxdata.core.entity.Alert;
import com.mingheinfo.maxdata.core.service.AlertService;

import cn.hutool.core.codec.Base64;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 告警信息
 */
@Slf4j
@RestController
@RequestMapping("/api/alert")
@Transactional
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private AlertsResultsService alertsResultsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/alert",method = RequestMethod.GET)
    public ApiJson getAlertByPage(PageVo pageVo, String startTime, String endTime, String hostName){

    	IPage<Alert> page = new Page<Alert>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<Alert> queryWrapper = new QueryWrapper<Alert>();
    	if(StringUtils.isNotBlank(hostName))
    	{
    	    queryWrapper.eq("host_name", hostName);
      }
    	queryWrapper.between("gmt_create", startTime, endTime);
    	page = alertService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }

  //分页获取
  @RequestMapping(value = "/service",method = RequestMethod.GET)
  public ApiJson getAlertByService(PageVo pageVo, String startTime, String endTime, String hostName, String service)
  {

    IPage page = new Page (pageVo.getPageNumber(),pageVo.getPageSize());
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("startTime", startTime);
    map.put("endTime",  endTime);
    map.put("hostName", hostName);
    map.put("service", service);
    map.put("pageNumber", (pageVo.getPageNumber() - 1) * pageVo.getPageSize());
    map.put("size", pageVo.getPageSize());
    List<HashMap> list = alertService.queryAlertByService(map);
    Integer total = alertService.queryAlertByServiceCount(map);
    page.setRecords(list);
    page.setTotal(total);
    return ResultUtil.data(page);
  }

  //分页获取
  @RequestMapping(value = "/type",method = RequestMethod.GET)
  public ApiJson getAlertByType(PageVo pageVo, String startTime, String endTime, String hostName, String service)
  {

    IPage page = new Page (pageVo.getPageNumber(),pageVo.getPageSize());
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("startTime", startTime);
    map.put("endTime",  endTime);
    map.put("hostName", hostName);
    map.put("service", service);
    map.put("pageNumber", (pageVo.getPageNumber() - 1) * pageVo.getPageSize());
    map.put("size", pageVo.getPageSize());
    List<HashMap> list = alertService.queryAlertByType(map);
    Integer total = alertService.queryAlertByTypeCount(map);
    page.setRecords(list);
    page.setTotal(total);
    return ResultUtil.data(page);
  }

    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(Alert entity){

    	alertService.save(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(Alert entity){

    	alertService.updateById(entity);

        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	alertService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }

  //保存数据
  @RequestMapping(value = "/register",method = RequestMethod.POST)
  public ApiJson register(Alert entity, HttpServletRequest request){
    AlertsResults alertsResults = new AlertsResults();
    alertsResults.setAlertId(entity.getId());
    alertsResults.setFixedBy(entity.getFixedBy());
    alertsResults.setGmtCreate(new Date());
    alertsResults.setFixedInfo(request.getParameter("description"));
    alertsResults.setKind(request.getParameter("kind"));
    alertsResults.setSubKind(request.getParameter("subKind"));
    alertsResultsService.save(alertsResults);
    UpdateWrapper<Alert> updateWrapper = new UpdateWrapper<>();
    updateWrapper.set("fixed_by", entity.getFixedBy());
    alertService.update(updateWrapper);
    return ResultUtil.success();
  }

}

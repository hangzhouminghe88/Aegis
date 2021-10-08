package com.mingheinfo.maxdata.core.controller;

import com.mingheinfo.common.utils.DateUtils;
import com.mingheinfo.maxdata.core.entity.SqlObject;
import com.mingheinfo.maxdata.core.entity.SqlSummary;
import com.mingheinfo.maxdata.core.entity.SqlSummaryRelationModel;
import com.mingheinfo.maxdata.core.service.SqlObjectService;
import com.mingheinfo.maxdata.core.service.SqlSummaryService;
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
import com.mingheinfo.maxdata.core.entity.SysAshWait;
import com.mingheinfo.maxdata.core.service.SysAshWaitService;

import cn.hutool.core.codec.Base64;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * 活动会话历史
 */
@Slf4j
@RestController
@RequestMapping("/api/sysAshWait")
@Transactional
public class SysAshWaitController {

    @Autowired
    private SysAshWaitService ashService;

    @Autowired
    private SqlSummaryService sqlSummaryService;

    @Autowired
    private SqlObjectService sqlObjectService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getSysAshWaitByPage(PageVo pageVo,String name,String service){

    	IPage<SysAshWait> page = new Page<SysAshWait>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<SysAshWait> queryWrapper = new QueryWrapper<SysAshWait>();


    	if(StringUtils.isNotBlank(service))
    	{
    		queryWrapper.eq("service", service);
    		queryWrapper.apply("create_time=(SELECT MAX(create_time) FROM sys_ash_wait WHERE service={0})", service);
	   	}

    	if(StringUtils.isNotBlank(name))
    	{
    		queryWrapper.like("event", name);
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
    	
    	page = ashService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }
    
    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(SysAshWait entity){

    	ashService.save(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(SysAshWait entity){

    	ashService.updateById(entity);

        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	ashService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }


  //object
  @RequestMapping(value = "/relation",method = RequestMethod.GET)
  public ApiJson getSysAshWaitRelationByPage(PageVo pageVo,String name,String service, String  objName, String sqlId){
    String maxGmt = sqlSummaryService.getMaxGmt(service);
    String maxObjType = sqlObjectService.getMaxObjType(service, objName);
    String regx = "TEST:" + maxObjType;
    IPage page = new Page(pageVo.getPageNumber(),pageVo.getPageSize());
    int pos = regx.indexOf("TABLE");
    if(pos > 0)
    {
      page = new Page<SqlSummary>(pageVo.getPageNumber(),pageVo.getPageSize());
      QueryWrapper<SqlSummary> queryWrapper = new QueryWrapper<SqlSummary>();
      if(StringUtils.isNotBlank(service))
      {
        queryWrapper.eq("service", service);
      }
      if(StringUtils.isNotBlank(sqlId))
      {
        queryWrapper.eq("sql_id",  sqlId);
      }
      if(StringUtils.isNotBlank(maxGmt))
      {
        queryWrapper.eq("gmt_create", maxGmt);
      }
      queryWrapper.apply("lower(sql_text) like {0}", "%"+objName.toLowerCase()+"%");
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
    }
    else
    {
      List<SqlSummaryRelationModel> list = sqlSummaryService.getRelationList(service, maxGmt, objName.toLowerCase(), pageVo.getPageNumber() - 1,pageVo.getPageSize());
      Integer cnt = sqlSummaryService.getRelationListCnt(service, maxGmt, objName.toLowerCase());
      page.setRecords(list);
      page.setTotal(cnt);
    }
    return  ResultUtil.data(page);
  }

  @RequestMapping(value = "/all", method = RequestMethod.GET)
  public ApiJson getAllAsh(PageVo pageVo, String service, String name)
  {
    QueryWrapper<SysAshWait> queryWrapper = new QueryWrapper<SysAshWait>();
    if(StringUtils.isNotBlank(service))
    {
      queryWrapper.eq("service", service);
    }
    if(StringUtils.isNotBlank(name))
    {
      queryWrapper.eq("event", name);
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
    List<SysAshWait>  list = ashService.list(queryWrapper);
    return ResultUtil.data(list);
  }
}

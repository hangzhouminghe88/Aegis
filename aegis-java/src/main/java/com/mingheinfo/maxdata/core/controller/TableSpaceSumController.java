package com.mingheinfo.maxdata.core.controller;

import com.mingheinfo.common.utils.DateUtils;
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
import com.mingheinfo.maxdata.core.entity.TableSpaceSum;
import com.mingheinfo.maxdata.core.service.TableSpaceSumService;

import cn.hutool.core.codec.Base64;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 表空间统计
 */
@Slf4j
@RestController
@RequestMapping("/api/tableSpaceSum")
@Transactional
public class TableSpaceSumController {

    @Autowired
    private TableSpaceSumService tableSpaceSumService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getTableSpaceSumByPage(PageVo pageVo,String name,String service){

    	IPage<TableSpaceSum> page = new Page<TableSpaceSum>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<TableSpaceSum> queryWrapper = new QueryWrapper<TableSpaceSum>();
    	
    	if(StringUtils.isNotBlank(name))
    	{
    		queryWrapper.like("name", name);
    	}
    	
    	if(StringUtils.isNotBlank(service))
    	{
    		queryWrapper.like("service", service);
    	}
    	else
    	{
	    	queryWrapper.apply("gmt_create=CURDATE()");
	    	queryWrapper.gt("used_rate", 85);
	    	queryWrapper.lt("free_space", 6000);
	    	queryWrapper.lt("used_days", 6);
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
    	
    	page = tableSpaceSumService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }
    
    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(TableSpaceSum entity){

    	tableSpaceSumService.save(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(TableSpaceSum entity){

    	tableSpaceSumService.updateById(entity);

        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	tableSpaceSumService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }

   @RequestMapping(value = "/report", method = RequestMethod.GET)
	 public ApiJson getTableSpaceReport(PageVo pageVo,String name,String service,String tableName){
		 IPage<TableSpaceSum> page = new Page<TableSpaceSum>(pageVo.getPageNumber(),pageVo.getPageSize());

		 QueryWrapper<TableSpaceSum> queryWrapper = new QueryWrapper<TableSpaceSum>();

		 if(StringUtils.isNotBlank(name))
		 {
			 queryWrapper.like("name", name);
		 }

		 if(StringUtils.isNotBlank(service))
		 {
			 queryWrapper.eq("service", service);
		 }
		 queryWrapper.apply("gmt_create=CURDATE()");
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

		 page = tableSpaceSumService.page(page, queryWrapper);

		 return ResultUtil.data(page);
	 }

	 @RequestMapping(value="history", method = RequestMethod.GET)
	public ApiJson getTableSpaceHistory(PageVo pageVo,String name,String service,String gmtCrete){
		IPage<TableSpaceSum> page = new Page<TableSpaceSum>(pageVo.getPageNumber(),pageVo.getPageSize());

		QueryWrapper<TableSpaceSum> queryWrapper = new QueryWrapper<TableSpaceSum>();

		if(StringUtils.isNotBlank(name))
		{
			queryWrapper.like("name", name);
		}

		if(StringUtils.isNotBlank(service))
		{
			queryWrapper.eq("service", service);
		}
		queryWrapper.between("gmt_create", DateUtils.getDate30(), DateUtils.getDate());
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

		page = tableSpaceSumService.page(page, queryWrapper);

		return ResultUtil.data(page);
	}
}

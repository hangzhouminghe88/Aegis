package com.mingheinfo.maxdata.core.controller;

import com.mingheinfo.common.utils.DateUtils;
import com.mingheinfo.maxdata.core.entity.TableMonSum;
import com.mingheinfo.maxdata.core.service.TableMonSumService;
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
import com.mingheinfo.maxdata.core.entity.TableMon;
import com.mingheinfo.maxdata.core.service.TableMonService;

import cn.hutool.core.codec.Base64;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/tableMon")
@Transactional
public class TableMonController {

    @Autowired
    private TableMonService tableMonService;

    @Autowired
    private StringRedisTemplate redisTemplate;

	@Autowired
	private TableMonSumService tableMonSumService;
    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getTableMonByPage(PageVo pageVo,String name,String service,String tableName, String mode, String owner){

    	IPage<TableMonSum> page = new Page<TableMonSum>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<TableMonSum> queryWrapper = new QueryWrapper<TableMonSum>();
			String maxDate = tableMonSumService.getMaxDate(service);
      if(StringUtils.isNotBlank(mode) && mode.equals("sys"))
      {
        queryWrapper.eq("owner", mode.toUpperCase());
			}
			if(StringUtils.isNotBlank(mode) && mode.equals("app"))
			{
				queryWrapper.ne("owner", "SYS");
			}
    	if(StringUtils.isNotBlank(service))
    	{
    		queryWrapper.eq("service", service);
	   	}
      if(StringUtils.isNotBlank(owner))
      {
      	queryWrapper.eq("owner", owner);
			}
    	if(StringUtils.isNotBlank(name))
    	{
    		queryWrapper.like("table_name", name);
    	}
    	
    	if(StringUtils.isNotBlank(tableName))
    	{
    		queryWrapper.like("table_name", tableName);
    	}
    	if(!StringUtils.isNotBlank(mode))
    	{
    		queryWrapper.gt("create_time", maxDate);
    		queryWrapper.ne("owner", "SYS");
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
    	
    	page = tableMonSumService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }
    
    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(TableMon entity){

    	tableMonService.save(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(TableMon entity){

    	tableMonService.updateById(entity);

        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	tableMonService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }


	//分页获取
	@RequestMapping(value = "/history",method = RequestMethod.GET)
	public ApiJson getTableMonHistoryByPage(PageVo pageVo,String name,String service,String tableName, String mode){

		IPage<TableMonSum> page = new Page<TableMonSum>(pageVo.getPageNumber(),pageVo.getPageSize());

		QueryWrapper<TableMonSum> queryWrapper = new QueryWrapper<TableMonSum>();
		if(StringUtils.isNotBlank(service))
		{
			queryWrapper.eq("service", service);
		}

		if(StringUtils.isNotBlank(name))
		{
			queryWrapper.like("table_name", name);
		}

		if(StringUtils.isNotBlank(tableName))
		{
			queryWrapper.like("table_name", tableName);
		}
		queryWrapper.between("create_time", DateUtils.getDate30(), DateUtils.getDate());
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

		page = tableMonSumService.page(page, queryWrapper);

		return ResultUtil.data(page);
	}


}

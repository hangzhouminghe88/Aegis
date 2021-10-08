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
import com.mingheinfo.maxdata.core.entity.SqlSummaryReport;
import com.mingheinfo.maxdata.core.service.SqlSummaryReportService;

import cn.hutool.core.codec.Base64;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 段空间统计
 */
@Slf4j
@RestController
@RequestMapping("/api/sqlSummaryReport")
@Transactional
public class SqlSummaryReportController {

    @Autowired
    private SqlSummaryReportService sqlSummaryReportService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getSqlSummaryReportByPage(PageVo pageVo,String name,String service){

    	IPage<SqlSummaryReport> page = new Page<SqlSummaryReport>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<SqlSummaryReport> queryWrapper = new QueryWrapper<SqlSummaryReport>();



    	if(StringUtils.isNotBlank(service))
    	{
    		queryWrapper.eq("service", service);
	   	}
    	else
    	{
        	if(StringUtils.isNotBlank(name))
        	{
        		queryWrapper.like("service", name);
        	}   
        	queryWrapper.apply("gmt_create=CURDATE()");
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
    	
    	page = sqlSummaryReportService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }

    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(SqlSummaryReport entity){

    	sqlSummaryReportService.save(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(SqlSummaryReport entity){

    	sqlSummaryReportService.updateById(entity);

        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	sqlSummaryReportService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }


	//历史
	@RequestMapping(value = "/history",method = RequestMethod.GET)
	public ApiJson getSqlSummaryReportHistoryByPage(PageVo pageVo,String name,String service){

		IPage<SqlSummaryReport> page = new Page<SqlSummaryReport>(pageVo.getPageNumber(),pageVo.getPageSize());

		QueryWrapper<SqlSummaryReport> queryWrapper = new QueryWrapper<SqlSummaryReport>();



		if(StringUtils.isNotBlank(service))
		{
			queryWrapper.eq("service", service);
		}
		else
		{
			if(StringUtils.isNotBlank(name))
			{
				queryWrapper.like("service", name);
			}
			queryWrapper.between("gmt_create", DateUtils.getDate30(), DateUtils.getDate());
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

		page = sqlSummaryReportService.page(page, queryWrapper);

		return ResultUtil.data(page);
	}

}

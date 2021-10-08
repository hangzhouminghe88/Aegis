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
import com.mingheinfo.maxdata.core.entity.SegmentSpaceSum;
import com.mingheinfo.maxdata.core.service.SegmentSpaceSumService;

import cn.hutool.core.codec.Base64;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 段空间统计
 */
@Slf4j
@RestController
@RequestMapping("/api/segmentSpaceSum")
@Transactional
public class SegmentSpaceSumController {

    @Autowired
    private SegmentSpaceSumService segmentSpaceSumService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getSegmentSpaceSumByPage(PageVo pageVo,String segmentName,String service){

    	IPage<SegmentSpaceSum> page = new Page<SegmentSpaceSum>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<SegmentSpaceSum> queryWrapper = new QueryWrapper<SegmentSpaceSum>();
    	
    	if(StringUtils.isNotBlank(segmentName))
    	{
    		queryWrapper.like("segment_name", segmentName);
    	}
    	
    	if(StringUtils.isNotBlank(service))
    	{
    		queryWrapper.eq("service", service);
    	}
    	else
    	{
	    	queryWrapper.apply("create_time=CURDATE()");
	    	queryWrapper.ge("used_space", 10000);
	    	queryWrapper.ge("diff_space", 100);
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
    	
    	page = segmentSpaceSumService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }
    
    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(SegmentSpaceSum entity){

    	segmentSpaceSumService.save(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(SegmentSpaceSum entity){

    	segmentSpaceSumService.updateById(entity);

        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	segmentSpaceSumService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }


	//分页获取
	@RequestMapping(value = "/report",method = RequestMethod.GET)
	public ApiJson getSegmentSpaceSumByReportPage(PageVo pageVo,String segmentName,String service){

		IPage<SegmentSpaceSum> page = new Page<SegmentSpaceSum>(pageVo.getPageNumber(),pageVo.getPageSize());

		QueryWrapper<SegmentSpaceSum> queryWrapper = new QueryWrapper<SegmentSpaceSum>();

		if(StringUtils.isNotBlank(segmentName))
		{
			queryWrapper.eq("segment_name", segmentName);
		}

		if(StringUtils.isNotBlank(service))
		{
			queryWrapper.eq("service", service);
		}
		queryWrapper.apply("create_time=CURDATE()");
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

		page = segmentSpaceSumService.page(page, queryWrapper);

		return ResultUtil.data(page);
	}

	//历史
	@RequestMapping(value = "/history",method = RequestMethod.GET)
	public ApiJson getSegmentSpaceSumByHistoryPage(PageVo pageVo,String segmentName,String service){

		IPage<SegmentSpaceSum> page = new Page<SegmentSpaceSum>(pageVo.getPageNumber(),pageVo.getPageSize());

		QueryWrapper<SegmentSpaceSum> queryWrapper = new QueryWrapper<SegmentSpaceSum>();

		if(StringUtils.isNotBlank(segmentName))
		{
			queryWrapper.eq("segment_name", segmentName);
		}

		if(StringUtils.isNotBlank(service))
		{
			queryWrapper.eq("service", service);
		}
		else
		{
			queryWrapper.between("create_time", DateUtils.getDate30(), DateUtils.getDate());
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

		page = segmentSpaceSumService.page(page, queryWrapper);

		return ResultUtil.data(page);
	}


	//历史
	@RequestMapping(value = "/include",method = RequestMethod.GET)
	public ApiJson getSegmentSpaceSumByIncludePage(PageVo pageVo,String tableName,String service, String partitionName){

		IPage<SegmentSpaceSum> page = new Page<SegmentSpaceSum>(pageVo.getPageNumber(),pageVo.getPageSize());

		QueryWrapper<SegmentSpaceSum> queryWrapper = new QueryWrapper<SegmentSpaceSum>();

		if(StringUtils.isNotBlank(tableName))
		{
			queryWrapper.eq("table_space_name", tableName);
		}
		if(StringUtils.isNotBlank(partitionName))
		{
			queryWrapper.eq("partition_name", partitionName);
		}
		if(StringUtils.isNotBlank(service))
		{
			queryWrapper.eq("service", service);
		}
		queryWrapper.apply("create_time=CURDATE()");
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

		page = segmentSpaceSumService.page(page, queryWrapper);

		return ResultUtil.data(page);
	}
	
	
	//分页获取
    @RequestMapping(value = "/alert",method = RequestMethod.GET)
    public ApiJson getSegmentSpaceSumByAlert(PageVo pageVo,String segmentName,String service){

    	IPage<SegmentSpaceSum> page = new Page<SegmentSpaceSum>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<SegmentSpaceSum> queryWrapper = new QueryWrapper<SegmentSpaceSum>();
    	
    	if(StringUtils.isNotBlank(segmentName))
    	{
    		queryWrapper.like("segment_name", segmentName);
    	}
    	else
    	{
	    	queryWrapper.apply("create_time=CURDATE()");
	    	queryWrapper.ge("used_space", 10000);
	    	queryWrapper.ge("diff_space", 100);
    	}
    	queryWrapper.notLike("service", "datac%");
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
    	
    	page = segmentSpaceSumService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }
}

package com.mingheinfo.maxdata.core.controller;

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
import com.mingheinfo.maxdata.core.entity.SequenceReport;
import com.mingheinfo.maxdata.core.service.SequenceReportService;

import cn.hutool.core.codec.Base64;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/sequenceReport")
@Transactional
public class SequenceReportController {

    @Autowired
    private SequenceReportService sequenceReportService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getSequenceReportByPage(PageVo pageVo,String name,String service,String sequence){

    	IPage<SequenceReport> page = new Page<SequenceReport>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<SequenceReport> queryWrapper = new QueryWrapper<SequenceReport>();


    	if(StringUtils.isNotBlank(service))
    	{
    		queryWrapper.eq("service", service);
		
	   	}

    	if(StringUtils.isNotBlank(name))
    	{
    		queryWrapper.apply("concat(owner,sequence) like {0}", "%"+name+"%");
    	}   
    	
    	if(StringUtils.isNotBlank(sequence))
    	{
    		queryWrapper.eq("sequence", sequence);
    	}
    	else
    		queryWrapper.apply("create_time=(SELECT MAX(create_time) FROM sequence_report WHERE service={0})", service);
    	
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
    	
    	page = sequenceReportService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }
    
    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(SequenceReport entity){

    	sequenceReportService.save(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(SequenceReport entity){

    	sequenceReportService.updateById(entity);

        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	sequenceReportService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
    


}

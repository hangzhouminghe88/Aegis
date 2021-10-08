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
import com.mingheinfo.maxdata.core.entity.Snapshot;
import com.mingheinfo.maxdata.core.service.SnapshotService;

import cn.hutool.core.codec.Base64;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/snapshot")
@Transactional
public class SnapshotController {

    @Autowired
    private SnapshotService snapshotService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getSnapshotByPage(PageVo pageVo,String sqlId,String service, String startTime, String endTime){

     IPage<Snapshot> page = new Page<Snapshot>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
      QueryWrapper<Snapshot> queryWrapper = new QueryWrapper<Snapshot>();
      if(StringUtils.isNotBlank(sqlId))
      {
        queryWrapper.eq("id", sqlId);
      }
    	if(StringUtils.isNotBlank(service))
    	{
    		queryWrapper.eq("service", service);
	   	}
    	if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime))
    	{
    	  queryWrapper.between("create_time", startTime, endTime);
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
    	page = snapshotService.page(page, queryWrapper);
    	return ResultUtil.data(page);
    }
    
    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(Snapshot entity){

    	snapshotService.save(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(Snapshot entity){

    	snapshotService.updateById(entity);

        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	snapshotService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
    


}

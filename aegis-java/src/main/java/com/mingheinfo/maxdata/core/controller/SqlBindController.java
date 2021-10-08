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
import com.mingheinfo.maxdata.core.entity.SqlBind;
import com.mingheinfo.maxdata.core.service.SqlBindService;

import cn.hutool.core.codec.Base64;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * sql绑定变量
 */
@Slf4j
@RestController
@RequestMapping("/api/sqlBind")
@Transactional
public class SqlBindController {

    @Autowired
    private SqlBindService sqlBindService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getSqlBindByPage(PageVo pageVo,String name,String service){

    	IPage<SqlBind> page = new Page<SqlBind>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<SqlBind> queryWrapper = new QueryWrapper<SqlBind>();


    	if(StringUtils.isNotBlank(service))
    	{
    		queryWrapper.eq("service", service);
	   	}

    	if(StringUtils.isNotBlank(name))
    	{
    		queryWrapper.apply("sql_text like {0}", "%"+name+"%");
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
    	
    	page = sqlBindService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }
    
    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(SqlBind entity){

    	sqlBindService.save(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(SqlBind entity){

    	sqlBindService.updateById(entity);

        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	sqlBindService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
    


}

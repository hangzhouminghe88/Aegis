package com.mingheinfo.maxdata.manage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.manage.entity.UserOperLog;
import com.mingheinfo.maxdata.manage.service.UserOperLogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 用户操作日志
 */
@Slf4j
@RestController
@RequestMapping("/api/userOperLog")
@Transactional
public class UserOperLogController {

    @Autowired
    private UserOperLogService userOperLogService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getUserOperLogByPage(PageVo pageVo,Integer domainId){

    	IPage<UserOperLog> page = new Page<UserOperLog>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<UserOperLog> queryWrapper = new QueryWrapper<UserOperLog>();

    	queryWrapper.orderByDesc("id");
    	
    	page = userOperLogService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }

    //获取所有
    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    public ApiJson getAllUserOperLogInfo(Integer dbLinkId){
    	QueryWrapper<UserOperLog> queryWrapper = new QueryWrapper<UserOperLog>();
    	queryWrapper.eq("db_link_id", dbLinkId);
    	
    	List<UserOperLog> list = userOperLogService.list(queryWrapper);
        return ResultUtil.data(list);
    }
    
    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(UserOperLog entity){

    	userOperLogService.save(entity);
        return ResultUtil.success();
    }


    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	userOperLogService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }


}

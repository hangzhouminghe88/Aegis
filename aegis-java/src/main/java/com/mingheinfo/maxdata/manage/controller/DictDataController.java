package com.mingheinfo.maxdata.manage.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.manage.entity.Dict;
import com.mingheinfo.maxdata.manage.entity.DictData;
import com.mingheinfo.maxdata.manage.entity.Role;
import com.mingheinfo.maxdata.manage.service.DictDataService;
import com.mingheinfo.maxdata.manage.service.DictService;

import cn.hutool.core.util.StrUtil;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/dictData")
@CacheConfig(cacheNames = "dictData")
@CrossOrigin
@Transactional
public class DictDataController{

    @Autowired
    private DictService dictService;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/getByCondition",method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    public ApiJson getByCondition(DictData dictData,PageVo pageVo){
    	ApiJson ajaxJson = new ApiJson();
    	
    	IPage<DictData> page = new Page<DictData>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<DictData> queryWrapper = new QueryWrapper<DictData>();
    	
    	if(dictData.getDictId()!=null)
    	{
    		queryWrapper.eq("dict_id", dictData.getDictId());
    	}
    	
    	if(dictData.getStatus()!=null)
    	{
    		queryWrapper.eq("status", dictData.getStatus());
    	}    
    	
    	if(StrUtil.isNotBlank(dictData.getTitle()))
    	{
    		queryWrapper.like("title", dictData.getTitle());
    	}     	
    	
    	page = dictDataService.page(page, queryWrapper);
    	
    	
        //Page<DictData> page = dictDataService.findByCondition(dictData, PageUtil.initPage(pageVo));
        
        ajaxJson.setResult(page);
        return ajaxJson;
    }

    @RequestMapping(value = "/getByType/{type}",method = RequestMethod.GET)
    @ApiOperation(value = "通过类型获取")
    @Cacheable(key = "#type")
    public ApiJson getByType(@PathVariable String type){
    	ApiJson ajaxJson = new ApiJson();
    	
        Dict dict = dictService.findByType(type);
        if (dict == null) {
        	ajaxJson.setError("-1","字典类型Type不存在");
            return ajaxJson;
        }
        List<DictData> list = dictDataService.findByDictId(dict.getId());
        
        ajaxJson.setResult(list);
        return ajaxJson;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ApiOperation(value = "添加")
    public ApiJson add(DictData dictData){
    	ApiJson ajaxJson = new ApiJson();
    	
        Dict dict = dictService.getById(dictData.getDictId());
        if (dict == null) {
        	ajaxJson.setError("-1","字典类型id不存在");
            return ajaxJson;
        }
        dictDataService.save(dictData);
        // 删除缓存
        redisTemplate.delete("dictData::"+dict.getType());
        
        return ajaxJson;
    }

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ApiOperation(value = "编辑")
    public ApiJson edit(DictData dictData){
    	ApiJson ajaxJson = new ApiJson();
    	
        dictDataService.updateById(dictData);
        // 删除缓存
        Dict dict = dictService.getById(dictData.getDictId());
        redisTemplate.delete("dictData::"+dict.getType());
        return ajaxJson;
    }

    @RequestMapping(value = "/delByIds/{ids}",method = RequestMethod.DELETE)
    @ApiOperation(value = "批量通过id删除")
    public ApiJson delByIds(@PathVariable String[] ids){
    	ApiJson ajaxJson = new ApiJson();
    	
        for(String id : ids){
            DictData dictData = dictDataService.getById(id);
            Dict dict = dictService.getById(dictData.getDictId());
            dictDataService.removeById(id);
            // 删除缓存
            redisTemplate.delete("dictData::"+dict.getType());
        }
        
        return ajaxJson;
    }
}

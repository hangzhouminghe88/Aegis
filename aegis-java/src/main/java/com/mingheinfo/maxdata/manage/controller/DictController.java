package com.mingheinfo.maxdata.manage.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.maxdata.manage.entity.Dict;
import com.mingheinfo.maxdata.manage.service.DictDataService;
import com.mingheinfo.maxdata.manage.service.DictService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/dict")
@CrossOrigin
@Transactional
public class DictController{

    @Autowired
    private DictService dictService;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //获取全部数据
    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    public ApiJson getAll(){
    	ApiJson ajaxJson = new ApiJson();
    	
        List<Dict> list = dictService.findAllOrderBySortOrder();
        
        ajaxJson.setResult(list);
        return ajaxJson;
    }

    //添加
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public ApiJson add(Dict dict){
    	ApiJson ajaxJson = new ApiJson();
    	
        if(dictService.findByType(dict.getType())!=null){
        	ajaxJson.setError("-1", "字典类型Type已存在");
            return ajaxJson;
        }
        dictService.save(dict);
        
        return ajaxJson;
    }

    //编辑
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(Dict dict){
    	ApiJson ajaxJson = new ApiJson();
    	
        Dict old = dictService.getById(dict.getId());
        // 若type修改判断唯一
        if(!old.getType().equals(dict.getType())&&dictService.findByType(dict.getType())!=null){
        	ajaxJson.setError("-1", "字典类型Type已存在");
            return ajaxJson;
        }
        dictService.updateById(dict);
        
        return ajaxJson;
    }

    //通过id删除
    @RequestMapping(value = "/delByIds/{id}",method = RequestMethod.DELETE)
    public ApiJson delAllByIds(@PathVariable String id){
    	ApiJson ajaxJson = new ApiJson();

        Dict dict = dictService.getById(id);
        dictService.removeById(id);
        dictDataService.deleteByDictId(id);
        // 删除缓存
        redisTemplate.delete("dictData::"+dict.getType());
        return ajaxJson;
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ApiOperation(value = "搜索字典")
    public ApiJson searchPermissionList(@RequestParam String key){
    	ApiJson ajaxJson = new ApiJson();
    	
        List<Dict> list = dictService.findByTitleOrTypeLike(key);
        
        ajaxJson.setResult(list);
        
        return ajaxJson;
    }
}

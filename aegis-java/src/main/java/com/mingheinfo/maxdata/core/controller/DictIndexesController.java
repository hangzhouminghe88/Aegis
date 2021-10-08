package com.mingheinfo.maxdata.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.core.entity.DictIndexes;
import com.mingheinfo.maxdata.core.service.DictIndexesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据字典索引信息接口
 * **/
@Slf4j
@RestController
@RequestMapping("/api/dictIndex")
@Transactional
public class DictIndexesController {
    @Autowired
    private DictIndexesService dictIndexesService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getDictReportByPage(PageVo pageVo, String name, String service, String owner){

        IPage<DictIndexes> page = new Page<DictIndexes>(pageVo.getPageNumber(),pageVo.getPageSize());

        QueryWrapper<DictIndexes> queryWrapper = new QueryWrapper<DictIndexes>();


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
            queryWrapper.apply("table_name like {0}","%"+name+"%");
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

        page = dictIndexesService.page(page, queryWrapper);

        return ResultUtil.data(page);
    }

    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(DictIndexes entity){

        dictIndexesService.save(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(DictIndexes entity){

        dictIndexesService.updateById(entity);

        return ResultUtil.data(entity);
    }

    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){

        for(Integer id:ids){
            dictIndexesService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }

    @RequestMapping(value="/indexes", method = RequestMethod.GET)
    public ApiJson getDictIndexesByTableName(PageVo pageVo, String tableName, String service, String search)
    {
        IPage<DictIndexes> page = new Page<DictIndexes>(pageVo.getPageNumber(),pageVo.getPageSize());

        QueryWrapper<DictIndexes> queryWrapper = new QueryWrapper<DictIndexes>();


        if(StringUtils.isNotBlank(service))
        {
            queryWrapper.eq("service", service);

        }
        if(StringUtils.isNotBlank(tableName))
        {
            queryWrapper.eq("table_name", tableName);
        }
        if(StringUtils.isNotBlank(search))
        {
            queryWrapper.eq("index_name", search);
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

        page = dictIndexesService.page(page, queryWrapper);

        return ResultUtil.data(page);
    }
}

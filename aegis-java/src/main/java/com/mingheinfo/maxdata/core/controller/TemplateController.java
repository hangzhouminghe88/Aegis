package com.mingheinfo.maxdata.core.controller;

import cn.hutool.core.date.DateUtil;
import com.mingheinfo.maxdata.core.entity.Host;
import com.mingheinfo.maxdata.core.entity.TemplatesHosts;
import com.mingheinfo.maxdata.core.service.HostService;
import com.mingheinfo.maxdata.core.service.TemplatesHostsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.mingheinfo.maxdata.core.entity.Template;
import com.mingheinfo.maxdata.core.service.TemplateService;
import com.mingheinfo.common.utils.DateUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.List;

/**
 * 模板信息接口
 */
@Slf4j
@RestController
@RequestMapping("/api/template")
@Transactional
public class TemplateController {

    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);
    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplatesHostsService templatesHostsService;

    @Autowired
    private HostService hostService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage", method = RequestMethod.GET)
    public ApiJson getTemplateByPage(PageVo pageVo, String name) {

        IPage<Template> page = new Page<Template>(pageVo.getPageNumber(), pageVo.getPageSize());

        QueryWrapper<Template> queryWrapper = new QueryWrapper<Template>();
        if(StringUtils.isNotBlank(name))
        {
            queryWrapper.like("name", name);
        }

        page = templateService.page(page, queryWrapper);

        return ResultUtil.data(page);
    }

    //保存数据
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ApiJson save(Template entity) {
        templateService.save(entity);
        this.slave(entity);
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ApiJson edit(Template entity) {
        templateService.updateById(entity);
        slave(entity);
        return ResultUtil.data(entity);
    }

    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}", method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids) {

        for (Integer id : ids) {
            templateService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }

    public void slave(Template entity) {
        logger.info("添加templatesHost===========");
        templatesHostsService.removeById(entity.id);
        QueryWrapper<Host> queryHostWrapper = new QueryWrapper<Host>();
        logger.info("kind====" + entity.getKind());
        List<Host> hosts = null;
        if(entity.getKind().equals("oracle")) {
            logger.info("查询hosts===========");
            queryHostWrapper.eq("db_type", 0);
            queryHostWrapper.eq("db_role", 0);
            queryHostWrapper.notLike("service", "tc%");
            queryHostWrapper.notLike("service", "ic%");
            queryHostWrapper.notLike("service", "dwdb%");
            hosts = getHosts(queryHostWrapper);
        }else if(entity.getKind().equals("mysql")) {
            logger.info("查询hosts mysql===========");
            queryHostWrapper.eq("db_type", 1);
            hosts = getHosts(queryHostWrapper);
        }else if(entity.getKind().equals("host")) {
            logger.info("查询hosts host===========");
            queryHostWrapper.in("db_type", 0,1);
            queryHostWrapper.notLike("service", "tc%");
            queryHostWrapper.notLike("service", "ic%");
            queryHostWrapper.notLike("service", "dwdb%");
            hosts = getHosts(queryHostWrapper);
        }
        saveHostTemplates(hosts, entity);
    }

    public List<Host> getHosts(QueryWrapper<Host> queryWrapper) {
        logger.info("获得Host列表");
        return hostService.list(queryWrapper);
    }

    public void  saveHostTemplates(List<Host> hosts, Template template) {
        if(hosts == null) return;
        TemplatesHosts templatesHosts = new TemplatesHosts();
        for(Host host : hosts) {
            //gmt_create,service,db_name,temp_id,name,sname,';
            //                  $sql .= 'host_name,host_id,superby,score,times,sortby,type,tags,level
            java.sql.Date s = new java.sql.Date(new java.util.Date().getTime());
            templatesHosts.setGmtCreate(s);
            templatesHosts.setService(host.getService());
            templatesHosts.setDbName(host.getDbName());
            templatesHosts.setTempId(template.id);
            templatesHosts.setName(template.getName());
            templatesHosts.setSname(template.getSname());
            templatesHosts.setHostName(host.getName());
            templatesHosts.setHostId(host.getId());
            templatesHosts.setSuperBy(0);
            templatesHosts.setScore(new BigDecimal("0"));
            templatesHosts.setTimes(0);
            templatesHosts.setSortBy(template.getSortBy());
            templatesHosts.setType(template.getType());
            templatesHosts.setTags(template.getTags());
            templatesHosts.setLevel(template.getLevel());
            templatesHostsService.save(templatesHosts);
        }
    }
}
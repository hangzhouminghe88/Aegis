package com.mingheinfo.maxdata.core.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mingheinfo.common.utils.DateUtils;
import com.mingheinfo.common.utils.LicenseUtils;
import com.mingheinfo.maxdata.core.entity.HostGroup;
import com.mingheinfo.maxdata.core.entity.LicenseInfo;
import com.mingheinfo.maxdata.core.entity.Template;
import com.mingheinfo.maxdata.core.entity.TemplatesHosts;
import com.mingheinfo.maxdata.core.service.HostGroupService;
import com.mingheinfo.maxdata.core.service.TemplateService;
import com.mingheinfo.maxdata.core.service.TemplatesHostsService;
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
import com.mingheinfo.maxdata.core.entity.Host;
import com.mingheinfo.maxdata.core.service.HostService;

import cn.hutool.core.codec.Base64;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 主机信息接口
 */
@Slf4j
@RestController
@RequestMapping("/api/host")
@Transactional
public class HostController {

    @Autowired
    private HostService hostService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplatesHostsService templatesHostsService;

    @Autowired
    private HostGroupService hostGroupService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getHostByPage(PageVo pageVo){

    	IPage<Host> page = new Page<Host>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<Host> queryWrapper = new QueryWrapper<Host>();
    	
    	page = hostService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }

    //获取所有
    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    public ApiJson getAllHost(){
    	QueryWrapper<Host> queryWrapper = new QueryWrapper<Host>();
    	
    	 List<Host> list = hostService.list(queryWrapper);

        return ResultUtil.data(list);
    }
    
    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(Host entity){
    	LicenseUtils lic = new LicenseUtils();
    	LicenseInfo info = LicenseUtils.getLicenseInfo();
    	int instanceCount = hostService.hostCnt();
        if(instanceCount < info.getInstanceCount()) {
        	hostService.save(entity);
            slave(entity);
            return ResultUtil.success();
        }
        return ResultUtil.error("已到达最大限制数，如需添加请联系管理员!");
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(Host entity){
        UpdateWrapper<Host> updateWrapper = new UpdateWrapper<Host>();
        updateWrapper.set("name", entity.getName());
        updateWrapper.set("os_type", entity.getOsType());
        updateWrapper.set("os_version", entity.getOsVersion());
        updateWrapper.set("db_type", entity.getDbType());
        updateWrapper.set("db_name", entity.getDbName());
        updateWrapper.set("db_role", entity.getDbRole());
        updateWrapper.set("up_days", entity.getUpDays());
        updateWrapper.set("up_load", entity.getUpLoad());
        updateWrapper.set("ip_addr", entity.getIpAddr());
        updateWrapper.set("mon_level", entity.getMonLevel());
        updateWrapper.set("threshold", entity.getThreshold());
        updateWrapper.set("group_id", entity.getGroupId());
        updateWrapper.set("service", entity.getService());
        updateWrapper.set("user_name", entity.getUserName());
        updateWrapper.set("password", entity.getPassword());
        updateWrapper.set("port", entity.getPort());
        updateWrapper.set("identity", entity.getIdentity());
        updateWrapper.eq("id", entity.id);
    	  hostService.update(updateWrapper);
        slave(entity);
        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	hostService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }

    //设置template_host数据
  public void  slave(Host entity)
  {
     templatesHostsService.removeByHostId(entity.id);
     List<Template> templates = new ArrayList<Template>();
     TemplatesHosts templatesHosts = new TemplatesHosts();
     if(entity.getDbType() == 0)
     {
       QueryWrapper<Template> queryWrapper = new QueryWrapper<Template>();
       queryWrapper.apply("type <> 'SYSMSQL'");
       templates = templateService.list(queryWrapper);
     }
     else
     {
       QueryWrapper<Template> queryWrapper = new QueryWrapper<Template>();
       queryWrapper.apply("type = 'SYSMSQL' or type = 'SYSHOST'");
       templates = templateService.list(queryWrapper);
     }
     templates.forEach(template -> {
       templatesHosts.setGmtCreate(DateUtils.parseDate(DateUtils.formatDateTime(new Date())));
       templatesHosts.setService(entity.getName());
       templatesHosts.setDbName(entity.getDbName());
       templatesHosts.setTempId(template.id);
       templatesHosts.setName(template.getName());
       templatesHosts.setSname(template.getSname());
       templatesHosts.setHostName(entity.getName());
       templatesHosts.setHostId(entity.id);
       templatesHosts.setGroupId(entity.getGroupId());
       ApiJson group = getHostGroupById(entity.getGroupId().toString());
       JSONObject obj = JSONObject.parseObject(JSONObject.toJSONString(group.getResult()));
       templatesHosts.setGroupName(obj.get("name").toString());
       templatesHosts.setSuperBy(0);
       templatesHosts.setSortBy(template.getSortBy());
       templatesHosts.setType(template.getType());
       templatesHosts.setTags(template.getTags());
       templatesHosts.setScore(BigDecimal.ZERO);
       templatesHosts.setTimes(0);
       templatesHosts.setCurrval(BigDecimal.ZERO);
       templatesHosts.setLast1d(BigDecimal.ZERO);
       templatesHosts.setLast7d(BigDecimal.ZERO);
       templatesHosts.setTimeWaited(BigInteger.ZERO);
       templatesHosts.setValue(BigInteger.ZERO);
       templatesHosts.setFlag(0);
       templatesHosts.setMaxLimit(BigDecimal.ZERO);
       templatesHosts.setMaxLimit(BigDecimal.ZERO);
       templatesHosts.setAvgRatio(BigDecimal.ZERO);
       templatesHosts.setLastval(BigDecimal.ZERO);
       templatesHosts.setMaxValue(BigDecimal.ZERO);
       templatesHosts.setDbName(entity.getDbName());
       templatesHosts.setKind(template.getKind());
       templatesHostsService.save(templatesHosts);
     });
  }

  //查询模板
  //分页获取
  @RequestMapping(value = "/template",method = RequestMethod.GET)
  public ApiJson getHostTemplate(PageVo pageVo, @RequestParam String hostName, @RequestParam String name){

    IPage<TemplatesHosts> page = new Page<TemplatesHosts>(pageVo.getPageNumber(),pageVo.getPageSize());
    QueryWrapper<TemplatesHosts> queryWrapper = new QueryWrapper<TemplatesHosts>();
    if(StringUtils.isNotBlank(hostName))
    {
      queryWrapper.eq("host_name", hostName);
    }
    if(StringUtils.isNotBlank(name))
    {
      queryWrapper.like("name", name);
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
    page = templatesHostsService.page(page, queryWrapper);
    return ResultUtil.data(page);
  }

  //分页获取
  @RequestMapping(value = "/group",method = RequestMethod.GET)
  public ApiJson getHostGroupById(String id){

    QueryWrapper<HostGroup> queryWrapper = new QueryWrapper<HostGroup>();
    if(StringUtils.isNotBlank(id))
    {
      queryWrapper.eq("id", id);
    }
    HostGroup hostGroup = hostGroupService.getById(id);
    return ResultUtil.data(hostGroup);
  }
}

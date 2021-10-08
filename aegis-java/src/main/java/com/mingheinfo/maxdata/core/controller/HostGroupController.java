package com.mingheinfo.maxdata.core.controller;

import com.mingheinfo.common.utils.DateUtils;
import com.mingheinfo.maxdata.core.entity.*;
import com.mingheinfo.maxdata.core.service.*;
import com.mingheinfo.maxdata.monitor.jobs.SqlSnapShotJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.LogFactory;
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

import cn.hutool.core.codec.Base64;
import io.micrometer.core.instrument.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 主机组接口
 */
@Slf4j
@RestController
@RequestMapping("/api/hostGroup")
@Transactional
public class HostGroupController {

    @Autowired
    private HostGroupService hostGroupService;
    @Autowired
    private HostService hostService;
    @Autowired
    private HostGroupsDetailService hostGroupDetailService;
    @Autowired
    private ThresholdsGroupsService thresholdsGroupsService;
    @Autowired
    private ThresholdsService thresholdsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

     private static final Logger logger = LoggerFactory.getLogger(HostGroupController.class);
    //分页获取
    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getHostGroupByPage(PageVo pageVo){

    	IPage<HostGroup> page = new Page<HostGroup>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<HostGroup> queryWrapper = new QueryWrapper<HostGroup>();
    	
    	page = hostGroupService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }

    //获取所有
    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    public ApiJson getAllHostGroup(){
    	QueryWrapper<HostGroup> queryWrapper = new QueryWrapper<HostGroup>();
    	
    	List<HostGroup> list = hostGroupService.list(queryWrapper);

        return ResultUtil.data(list);
    }
    
    //保存数据
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ApiJson save(HostGroup entity){

    	  hostGroupService.save(entity);
        slave(entity.id, entity.getName(), entity.getHostNames());
        return ResultUtil.success();
    }

    //更新数据
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson edit(HostGroup entity){
        logger.info("hostGroup ====" + entity);
    	  hostGroupService.updateById(entity);
        slave(entity.id, entity.getName(), entity.getHostNames());
        return ResultUtil.data(entity);
    }
    
    //批量通过ids删除
    @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson delByIds(@PathVariable Integer[] ids){
        
        for(Integer id:ids){
        	hostGroupService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
    /***
     * 同步设置主机详情，阈值等
     * @param groupId组ID
     * @param groupName组名称
     * @param hostName主机名称
     * **/
    private void slave(Integer groupId, String groupName, String hostName)
    {
      hostGroupService.deleteHostGroupDetailByGID(groupId);
      hostGroupService.deleteThresholdsGroupsByGID(groupId);

      List<HostGroupDetail> list = new ArrayList<HostGroupDetail>();
      String[] hostname = hostName.split(",");
      for(String it : hostname)
      {
          QueryWrapper<Host> queryWrapper = new QueryWrapper<Host>();
          queryWrapper.eq("name", it);
          Host host = hostService.getOne(queryWrapper);
          HostGroupDetail hostGroupDetail = new HostGroupDetail();
          hostGroupDetail.setGroupId(groupId);
          hostGroupDetail.setGmtCreate(new Date());
          hostGroupDetail.setGroupName(groupName);
          if(host!=null && StringUtils.isNotBlank(host.id.toString()))
          hostGroupDetail.setHostId(host.id);
          if(host!=null && StringUtils.isNotBlank(host.getName().toString()))
          hostGroupDetail.setHostName(host.getName());
          list.add(hostGroupDetail);
      }
      hostGroupDetailService.saveBatch(list);
      ThresholdsGroups thresholdsGroups = new ThresholdsGroups();
      QueryWrapper<Thresholds> queryWrapperThresholds = new QueryWrapper<Thresholds>();
      Thresholds thresholds = thresholdsService.getOne(queryWrapperThresholds);
      if(thresholds !=null ) {
        thresholdsGroups.setThresholdName(thresholds.getName());
        thresholdsGroups.setBaseval(thresholds.getBaseval());
        thresholdsGroups.setGroupId(groupId);
        thresholdsGroups.setGroupName(groupName);
        thresholdsGroupsService.save(thresholdsGroups);
      }
    }
}

package com.mingheinfo.maxdata.core.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.core.entity.Alert;
import com.mingheinfo.maxdata.core.entity.AlertsResults;
import com.mingheinfo.maxdata.core.service.AlertService;
import com.mingheinfo.maxdata.core.service.AlertsResultsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/result")
@Transactional
public class AlertsResultsController
{
  @Autowired
  private AlertsResultsService alertsResultsService;

  @Autowired
  private AlertService alertService;

  //分页获取
  @RequestMapping(value = "/list",method = RequestMethod.GET)
  public ApiJson getAlertByPage(PageVo pageVo, String kind, String alertId){

    IPage<AlertsResults> page = new Page<AlertsResults>(pageVo.getPageNumber(),pageVo.getPageSize());

    QueryWrapper<AlertsResults> queryWrapper = new QueryWrapper<AlertsResults>();
    if(StringUtils.isNotBlank(alertId))
    {
      queryWrapper.eq("alert_id", alertId);
    }
    if(StringUtils.isNotBlank(kind))
    {
      queryWrapper.like("concat(kind, sub_kind)", kind);
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
    page = alertsResultsService.page(page, queryWrapper);

    return ResultUtil.data(page);
  }

  //保存数据
  @RequestMapping(value = "/edit",method = RequestMethod.POST)
  public ApiJson edit(AlertsResults entity){
    entity.setGmtCreate(new Date());
    alertsResultsService.updateById(entity);
    return ResultUtil.success();
  }

  //保存数据
  @RequestMapping(value = "/save",method = RequestMethod.POST)
  public ApiJson save(AlertsResults entity){
    entity.setGmtCreate(new Date());
    alertsResultsService.save(entity);
    return ResultUtil.success();
  }

  //批量通过ids删除
  @RequestMapping(value = "/delAllByIds/{ids}",method = RequestMethod.DELETE)
  public ApiJson delByIds(@PathVariable Integer[] ids){
    for(int i = 0; i<= ids.length - 1; i++){
      while (i == ids.length - 1) {
        UpdateWrapper<Alert> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("fixed_by", null);
        AlertsResults alertsResults = alertsResultsService.getById(ids[i]);
        updateWrapper.eq("id", alertsResults.getAlertId());
        alertService.update(updateWrapper);
        break;
      }
      alertsResultsService.removeById(ids[i]);
    }
    return ResultUtil.success("批量通过id删除数据成功");
  }
}

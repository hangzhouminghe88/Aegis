package com.mingheinfo.maxdata.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.DateUtils;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.core.entity.TableMonHistory;
import com.mingheinfo.maxdata.core.entity.TableMonSum;
import com.mingheinfo.maxdata.core.service.TableMonHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/tableMonHistory")
@Transactional
public class TableMonHistoryController
{

  @Autowired
  private TableMonHistoryService tableMonHistoryService;

  @RequestMapping(value = "/history",method = RequestMethod.GET)
  public ApiJson getTableMonHistoryByPage(PageVo pageVo, String indexName){

    IPage<TableMonHistory> page = new Page<TableMonHistory>(pageVo.getPageNumber(),pageVo.getPageSize());

    QueryWrapper<TableMonHistory> queryWrapper = new QueryWrapper<TableMonHistory>();
    if(StringUtils.isNotBlank(indexName))
    {
      queryWrapper.like("index_name", indexName);
    }
    queryWrapper.between("gmt_modified", DateUtils.getDate30(), DateUtils.getDate());
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

    page = tableMonHistoryService.page(page, queryWrapper);

    return ResultUtil.data(page);
  }

}

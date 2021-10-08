package com.mingheinfo.maxdata.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.maxdata.core.entity.SqlText;
import com.mingheinfo.maxdata.core.service.SqlTextService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/sqlText")
@Transactional
public class SqlTextController
{
  @Autowired
  private SqlTextService sqlTextService;

  private static final Logger logger = LoggerFactory.getLogger(SqlTextController.class);

  @Autowired
  private StringRedisTemplate redisTemplate;
  @RequestMapping(value = "/getAll/{sqlId}", method = RequestMethod.GET)
  public ApiJson getAllPage(@PathVariable String sqlId, String service)
  {
    logger.info("sqlId=====" + sqlId + " service =====" + service);
    QueryWrapper<SqlText> queryWrapper = new QueryWrapper<SqlText>();
    if(StringUtils.isNotBlank(service))
    {
      queryWrapper.eq("service", service);
    }

    if(StringUtils.isNotBlank(sqlId))
    {
      queryWrapper.eq("sql_id", sqlId);
    }
    queryWrapper.eq("sql_type", "SQLSUM");
    logger.info("getExpression ===" + queryWrapper.getSqlSelect());
    return ResultUtil.data(sqlTextService.list(queryWrapper));
  }
}

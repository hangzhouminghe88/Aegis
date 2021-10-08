package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.core.entity.SqlSummaryReport;
import com.mingheinfo.maxdata.core.mapper.SqlSummaryReportMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SqlSummaryReportService extends ServiceImpl<SqlSummaryReportMapper, SqlSummaryReport> {
  public void deleteSqlSummaryReport(String service)
  {
      this.baseMapper.deleteSqlSummaryReport(service);
  }

  public void insertSqlSumaryReport(String service)
  {
      this.baseMapper.insertSqlSumaryReport(service);
  }

  public void callSqlSummaryReport(String service)
  {
    this.baseMapper.callSqlSummaryReport(service);
  }
}
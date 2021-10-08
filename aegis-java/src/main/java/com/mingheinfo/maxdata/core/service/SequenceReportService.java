package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.core.entity.SequenceReport;
import com.mingheinfo.maxdata.core.mapper.SequenceReportMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SequenceReportService extends ServiceImpl<SequenceReportMapper, SequenceReport> {
  public void deleteTody(String service) {
      this.baseMapper.deleteTody(service);
  }

  public void insetSequenceReport(String service) {
      this.baseMapper.insetSequenceReport(service);
  }
}
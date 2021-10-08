package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.TemplatesSummary;
import com.mingheinfo.maxdata.core.mapper.TemplatesSummaryMapper;
import org.springframework.stereotype.Service;

@Service
public class TemplatesSummaryService extends ServiceImpl<TemplatesSummaryMapper, TemplatesSummary>
{
  public void truncateTemplatesSummary()
  {
    this.baseMapper.truncateTemplatesSummary();
  }

  public void insetTemplatesSummary()
  {
    this.baseMapper.insetTemplatesSummary();
  }
}

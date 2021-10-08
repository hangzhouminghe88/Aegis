package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.TemplatesHosts;
import com.mingheinfo.maxdata.core.mapper.TemplatesHostsMapper;
import org.springframework.stereotype.Service;

@Service
public class TemplatesHostsService extends ServiceImpl<TemplatesHostsMapper, TemplatesHosts> {
  public void removeByHostId(Integer hostId)
  {
    this.baseMapper.removeByHostId(hostId);
  }
}

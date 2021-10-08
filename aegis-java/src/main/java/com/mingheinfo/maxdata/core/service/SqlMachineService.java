package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.SqlMachine;
import com.mingheinfo.maxdata.core.mapper.SqlMachineMapper;
import org.springframework.stereotype.Service;

@Service
public class SqlMachineService extends ServiceImpl<SqlMachineMapper, SqlMachine>
{
  public void deleteSqlMachine(String service)
  {
    this.baseMapper.deleteSqlMachine(service);
  }
}

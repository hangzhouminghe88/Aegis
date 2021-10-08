package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.core.entity.SqlObject;
import com.mingheinfo.maxdata.core.mapper.SqlObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class SqlObjectService extends ServiceImpl<SqlObjectMapper, SqlObject>
{
  public void deleteSqlObject()
  {
    this.baseMapper.deleteSqlObject();
  }

  public void insertSqlObject()
  {
    this.baseMapper.insertSqlObject();
  }
  public String getMaxObjType(String service, String objName)
  {
    return this.baseMapper.getMaxObjType(service, objName);
  }
}

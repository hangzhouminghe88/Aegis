package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SqlModule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SqlModuleMapper extends BaseMapper<SqlModule>
{
  @Select("call sql_sqlmodule(now())")
  void callSqlModule();
}

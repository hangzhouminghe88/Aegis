package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SqlMachine;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SqlMachineMapper extends BaseMapper<SqlMachine>
{
  @Delete("delete from sql_machine where service = #{sevice} and gmt_create = DATE_FORMAT(NOW(), '%Y-%m-%d ')")
  void deleteSqlMachine(String service);
}

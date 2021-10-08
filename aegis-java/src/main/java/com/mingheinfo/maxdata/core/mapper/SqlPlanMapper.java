package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SqlPlan;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SqlPlanMapper extends BaseMapper<SqlPlan>
{
  @Delete("delete from sql_plan where service = #{service}")
  void deleteSqlPlan(String service);
}

package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SqlObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SqlObjectMapper extends BaseMapper<SqlObject>
{
  @Delete("delete from sql_object where plan_type = 'SQLSUM';")
  void deleteSqlObject();
  @Insert("insert into sql_object(service, object_name, object_type, plan_type)\n" +
          "    select service, object_name, object_type, plan_type\n" +
          "      from sql_plan \n" +
          "     where plan_type = 'SQLSUM'\n" +
          "      group by service, object_name, object_type, plan_type")
  void insertSqlObject();

  @Select("select max(object_type) from ( select object_type from sql_object where service= #{service} and object_name = #{objName} group by object_type) as t")
  String getMaxObjType(String service, String objName);
}

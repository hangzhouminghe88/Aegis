package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SqlSummary;
import com.mingheinfo.maxdata.core.entity.SqlSummaryRelationModel;

@Mapper
public interface SqlSummaryMapper extends BaseMapper<SqlSummary> {
    @Delete("delete from sql_summary where service = #{service} and gmt_create = DATE_FORMAT(NOW(), '%Y-%m-%d ')")
    void deleteSqlSummary(String service);
    @Select("call sql_sqlsummary(#{service}, now())")
    void callSqlSummary(String service);
    @Select("select * from sql_summary where service= #{service} and sql_id=#{sqlId} and gmt_create= DATE_FORMAT(NOW() - INTERVAL 1 DAY, '%Y-%m-%d')")
    SqlSummary getPrevDayDataBySqlId(String service, String sqlId);
    @Select("select max(gmt_create) from (select gmt_create from sql_summary where service = #{service} group by gmt_create) as t")
    String getMaxGmt(String service);
    @Select("SELECT a.*, b.plan_line, b.object_name FROM (SELECT * FROM sql_summary WHERE service='${service}')AS a INNER JOIN(SELECT * FROM sql_plan WHERE service='${service}') AS b ON a.sql_id = b.sql_id WHERE a.gmt_create=#{gmtCreate} AND (LOWER(b.object_name) LIKE '%${objectName}%') ORDER BY a.exec_delta desc limit #{pageNumber}, #{pageSize}")
    List<SqlSummaryRelationModel>getRelationList(String service, String gmtCreate, String objectName, Integer pageNumber, Integer pageSize);
    @Select("SELECT COUNT(*) AS cnt FROM (SELECT a.*, b.plan_line, b.object_name FROM (SELECT * FROM sql_summary WHERE service=#{service})AS a INNER JOIN(SELECT * FROM sql_plan WHERE service=#{service}) AS b ON a.sql_id = b.sql_id WHERE a.gmt_create=#{gmtCreate} AND (LOWER(b.object_name) LIKE '%${objectName}%') ORDER BY a.exec_delta desc ) AS f")
    Integer getRelationListCnt(String service, String gmtCreate, String objectName);
}
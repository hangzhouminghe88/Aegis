package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SqlSummaryReport;

@Mapper
public interface SqlSummaryReportMapper extends BaseMapper<SqlSummaryReport> {
  @Delete("delete from sql_summary_report where service = #{service} and gmt_create = DATE_FORMAT(NOW(), '%Y-%m-%d ')")
    void deleteSqlSummaryReport(String service);
  @Insert("insert into sql_summary_report(service, gmt_create) values(#{service}, curdate());")
    void insertSqlSumaryReport(String service);
  @Select("call sql_sqlsum_rpt(#{service}, now())")
  void callSqlSummaryReport(String service);

}
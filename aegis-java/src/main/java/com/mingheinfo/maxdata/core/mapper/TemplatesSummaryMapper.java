package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.TemplatesSummary;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TemplatesSummaryMapper extends BaseMapper<TemplatesSummary>
{
  @Update("truncate table templates_summary")
  void truncateTemplatesSummary();
  @Insert(" insert into templates_summary (gmt_create,service,name,type,currval,currmax,baseval,basemax,avgrate,dayname) select date_format(gmt_modified,'%Y-%m-%d'),service,name,type,\n" +
          "           round(avg(currval),2) as currval,\n" +
          "           max(currval) as max_curr,\n" +
          "           round(avg(lastval),2) as baseval,\n" +
          "           max(lastval) as max_base,\n" +
          "           round(avg(ratio),2) as ratio,\n" +
          "           dayname(max(gmt_modified)) as dayname\n" +
          "      from templates_history\n" +
          "     where ratio > 0.7 and ratio < 1.4\n" +
          "      and currval > 0 and lastval > 0\n" +
          "      and gmt_modified < curdate()\n" +
          "     group by date_format(gmt_modified,'%Y-%m-%d'),service,name,type")
  void insetTemplatesSummary();
}

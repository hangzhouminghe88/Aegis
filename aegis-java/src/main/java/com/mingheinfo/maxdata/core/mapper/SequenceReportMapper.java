package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SequenceReport;

@Mapper
public interface SequenceReportMapper extends BaseMapper<SequenceReport> {
    @Delete("delete from sequence_report where service = #{service} and create_time = DATE_FORMAT(NOW(), '%Y-%m-%d ')")
    void deleteTody(String service);
    @Insert("insert into sequence_report(service, owner, sequence, create_time,\n" +
            "                          min_value, incr_by, last_number, cache_size, delta)\n" +
            "        select a.service, a.owner, a.sequence, date_format(now(), '%Y-%m-%d') as create_time,\n" +
            "               a.min_value, a.incr_by, a.last_number, a.cache_size, a.last_number - b.last_number as delta\n" +
            "          from sequence a left join sequence b on a.sequence = b.sequence\n" +
            "           and a.service = b.service and a.owner = b.owner\n" +
            "           and date_format(b.create_time,'%Y-%m-%d') = date_format(now() - interval 1 day, '%Y-%m-%d')\n" +
            "         where a.service=#{service}\n" +
            "          and date_format(a.create_time,'%Y-%m-%d') = date_format(now(), '%Y-%m-%d')")
    void insetSequenceReport(String service);
}
package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.TableMonSum;

@Mapper
public interface TableMonSumMapper extends BaseMapper<TableMonSum> {
    @Delete("delete from table_mon_sum where service = #{service} and create_time = DATE_FORMAT(NOW(), '%Y-%m-%d ')")
    void deleteTody(String service);
    @Insert("insert into table_mon_sum(owner, service, table_name, create_time, \n" +
            "          ins_diff, upd_diff, del_diff, dml_diff, truncated, num_rows)\n" +
            "\tselect a.owner, a.service, a.table_name, date_format(now(), '%Y-%m-%d') as create_time, \n" +
            "\t       if(a.inss-b.inss>=0,a.inss-b.inss,a.inss) as ins_diff, \n" +
            "  \t       if(a.upds-b.upds>=0,a.upds-b.upds,a.upds) as upd_diff, \n" +
            "\t       if(a.dels-b.dels>=0,a.dels-b.dels,a.dels) as del_diff, \n" +
            "\t       if(a.total_dmls-b.total_dmls>=0,a.total_dmls-b.total_dmls,a.total_dmls) as dml_diff, \n" +
            "               a.truncated, a.num_rows\n" +
            "\t  from table_mon a left join table_mon b on a.table_name = b.table_name \n" +
            "           and a.service = b.service and a.owner = b.owner\n" +
            "           and date_format(b.gmt_create,'%Y-%m-%d') = date_format(now() - interval 1 day, '%Y-%m-%d')\n" +
            "\t where a.service=#{service}\n" +
            "\t  and date_format(a.gmt_create,'%Y-%m-%d') = date_format(now(), '%Y-%m-%d');\n")
    void insetTableMonSum(String service);

    @Select("select max(create_time) from table_mon_sum where service=#{service}")
    String getMaxDate(String service);
}
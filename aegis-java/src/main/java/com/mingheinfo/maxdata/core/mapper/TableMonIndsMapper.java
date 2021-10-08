package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.TableMonInds;

@Mapper
public interface TableMonIndsMapper extends BaseMapper<TableMonInds> {
    @Delete("delete from table_mon_inds where create_time = curdate()")
    void deleteTableMonInds();
    @Insert("insert into table_mon_inds(service, owner, table_name, sql_total, total_size, ind_total,\n" +
            "                          ind_used, ind_unused, redu_total, col_max, col_avg, create_time)\n" +
            "    select service, owner, table_name, sum(sql_total), sum(seg_size), count(*), \n" +
            "           ifnull(sum(if(used_stats='YES',1,null)),0) as used, \n" +
            "           ifnull(sum(if(used_stats='NO',1,null)),0) as unused,\n" +
            "           ifnull(sum(if(redu_stats='YES',1,null)),0) as redu,\n" +
            "           max(col_total), round(avg(col_total),2), curdate()\n" +
            "      from dict_indexes\n" +
            "     group by service, owner, table_name")
    void insertData();
}
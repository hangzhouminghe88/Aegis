package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.TableMon;

@Mapper
public interface TableMonMapper extends BaseMapper<TableMon> {
    @Delete("delete from table_mon where service = #{service} and gmt_create = DATE_FORMAT(NOW(), '%Y-%m-%d ')")
    void deleteTody(String service);
}
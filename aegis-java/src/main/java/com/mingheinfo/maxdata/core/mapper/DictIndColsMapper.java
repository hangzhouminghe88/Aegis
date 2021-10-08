package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.DictIndCols;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DictIndColsMapper extends BaseMapper<DictIndCols> {
    @Delete("delete from dict_indcols where service not in (select distinct service from templates_hosts)")
    void deleteService();
    @Update("truncate table dict_indcols")
    void truncate();
    @Select("call sql_indcols(#{service})")
    void callSqlIndCols(String service);
}

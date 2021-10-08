package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.DictColumns;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DictColumnsMapper extends BaseMapper<DictColumns> {
    @Delete("delete from dict_columns where service not in (select distinct service from templates_hosts)")
    void deleteService();
    @Update("truncate table dict_columns")
    void truncate();
}

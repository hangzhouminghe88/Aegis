package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.DictIndexes;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DictIndexesMapper extends BaseMapper<DictIndexes> {
    @Delete("delete from dict_indexes where service not in (select distinct service from templates_hosts)")
    void deleteService();
    @Select("select * from dict_indexes")
    List<DictIndexes> getAll();
    @Update("truncate table dict_indexes")
    void truncate();
}

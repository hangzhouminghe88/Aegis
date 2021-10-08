package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SqlText;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SqlTextMapper extends BaseMapper<SqlText> {
    @Delete("delete from sql_text where service = #{service} and sql_type = 'SQLSUM'")
    void deleteSqlText(String service);
    @Delete("delete from sql_text where service = #{service} and sql_type = #{type}")
    void deleteSqlTextByType(String service, String type);
}

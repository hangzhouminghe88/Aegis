package com.mingheinfo.maxdata.core.mapper;
import com.mingheinfo.maxdata.core.entity.DictReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DictReportMapper  extends  BaseMapper<DictReport> {
    @Delete("delete from dict_tables where service not in (select distinct service from templates_hosts)")
    void deleteService();
    @Update("truncate table dict_tables")
    void truncate();
}

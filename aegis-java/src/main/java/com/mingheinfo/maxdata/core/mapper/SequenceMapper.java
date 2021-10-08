package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.Sequence;

@Mapper
public interface SequenceMapper extends BaseMapper<Sequence> {
    @Delete("delete from sequence where service = #{service} and create_time = DATE_FORMAT(NOW(), '%Y-%m-%d ')")
   void deleteTody(String service);
}
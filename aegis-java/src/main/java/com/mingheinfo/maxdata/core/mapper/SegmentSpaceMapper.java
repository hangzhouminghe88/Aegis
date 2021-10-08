package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SegmentSpace;

@Mapper
public interface SegmentSpaceMapper extends BaseMapper<SegmentSpace> {
	
	@Delete("delete from segment_space where service = #{service} and create_time >= date_format(now(), '%Y-%m-%d')")
	void deleteToday(String service);
}
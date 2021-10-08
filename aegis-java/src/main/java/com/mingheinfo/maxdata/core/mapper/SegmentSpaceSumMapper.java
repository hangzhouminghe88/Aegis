package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SegmentSpaceSum;

@Mapper
public interface SegmentSpaceSumMapper extends BaseMapper<SegmentSpaceSum> {
	@Delete("delete from segment_space_sum where service = #{service} and create_time = DATE_FORMAT(NOW(), '%Y-%m-%d ')")
	void deleteToday(String service);
	
	void statSegmentSpace(String service);
}
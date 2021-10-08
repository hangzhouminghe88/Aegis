package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SysEvent;

@Mapper
public interface SysEventMapper extends BaseMapper<SysEvent> {
	@Update("truncate table sys_event")
	void truncate();
}
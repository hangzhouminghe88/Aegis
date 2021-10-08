package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SysCustom;

@Mapper
public interface SysCustomMapper extends BaseMapper<SysCustom> {
	@Update("truncate table sys_custom")
	void truncate();
}
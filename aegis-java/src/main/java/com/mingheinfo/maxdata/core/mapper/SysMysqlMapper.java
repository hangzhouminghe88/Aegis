package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SysMysql;

@Mapper
public interface SysMysqlMapper extends BaseMapper<SysMysql> {
	@Update("truncate table sys_mysql")
	void truncate();
}
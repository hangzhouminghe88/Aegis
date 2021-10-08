package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.TableSpace;

@Mapper
public interface TableSpaceMapper extends BaseMapper<TableSpace> {
	@Delete("delete from table_space where service = #{service} and gmt_create = DATE_FORMAT(NOW(), '%Y-%m-%d')")
	void deleteToday(String service);
}
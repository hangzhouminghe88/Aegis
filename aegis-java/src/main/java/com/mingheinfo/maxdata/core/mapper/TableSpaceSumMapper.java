package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.TableSpaceSum;

@Mapper
public interface TableSpaceSumMapper extends BaseMapper<TableSpaceSum> {
	@Select("call sql_tabspace(#{service},now())")
	void callSqlTabSpace(String service);
}
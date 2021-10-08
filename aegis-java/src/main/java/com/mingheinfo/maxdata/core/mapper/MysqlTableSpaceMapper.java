package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.MysqlTableSpace;

@Mapper
public interface MysqlTableSpaceMapper extends BaseMapper<MysqlTableSpace> {
	@Select("call mysql_tabspace(curdate())")
	void callMysqlTabSpace();
}
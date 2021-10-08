package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.SysStat;

@Mapper
public interface SysStatMapper extends BaseMapper<SysStat> {

	@Update("truncate table sys_stats")
	void truncate();
	@Select("call all_curves()")
	void callAllCurves();
	@Select("call sys_curves(#{service})")
	void callSysCurves(String service);
}
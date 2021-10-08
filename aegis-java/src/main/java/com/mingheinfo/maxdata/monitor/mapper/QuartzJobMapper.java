package com.mingheinfo.maxdata.monitor.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.monitor.entity.QuartzJob;

@Mapper
public interface QuartzJobMapper extends BaseMapper<QuartzJob> {
    /**
     * 通过类名获取
     * @param jobClassName
     * @return
     */
	@Select("select * from t_quartz_job where job_class_name=#{jobClassName}")
    List<QuartzJob> findByJobClassName(String jobClassName);
}
package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.Template;

@Mapper
public interface TemplateMapper extends BaseMapper<Template> {
  @Select("SELECT count(*) from template where name=#{name}")
  int queryByName(String name);
}
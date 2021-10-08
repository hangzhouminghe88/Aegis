package com.mingheinfo.maxdata.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.TemplatesHosts;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TemplatesHostsMapper extends BaseMapper<TemplatesHosts> {
  @Delete("delete from templates_hosts where host_id = #{hostId}")
  void removeByHostId(Integer hostId);
}

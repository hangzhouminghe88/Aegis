package com.mingheinfo.maxdata.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.HostGroup;

@Mapper
public interface HostGroupMapper extends BaseMapper<HostGroup>
{
   @Delete("delete from hosts_groups_detail where group_id = #{GID}")
   void deleteHostGroupDetailByGID(Integer GID);
   @Delete("delete from thresholds_groups where group_id = #{GID}")
   void deleteThresholdsGroupsByGID(Integer GID);
}
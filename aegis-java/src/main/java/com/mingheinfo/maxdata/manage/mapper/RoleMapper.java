package com.mingheinfo.maxdata.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.manage.entity.Role;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
	@Select("select * from t_role where default_role=#{defaultRole}") 
	List<Role> findByDefaultRole(Boolean defaultRole);
	
	@Select("select * from t_role")
	List<Role> getAll();
}
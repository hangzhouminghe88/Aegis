package com.mingheinfo.maxdata.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.manage.entity.RolePermission;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    /**
     * 通过permissionId获取
     * @param permissionId
     * @return
     */
	@Select("select * from t_role_permission where permission_id=#{permissionId}") 
    List<RolePermission> findByPermissionId(Integer permissionId); 

    /**
     * 通过roleId获取
     * @param roleId
     */
	@Select("select * from t_role_permission where role_id=#{roleId}")
    List<RolePermission> findByRoleId(Integer roleId);

    /**
     * 通过roleId删除
     * @param roleId
     */
	@Delete("delete from t_role_permission where role_id=#{roleId}")
    void deleteByRoleId(Integer roleId);
}
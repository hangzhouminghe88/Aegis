package com.mingheinfo.maxdata.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.manage.entity.Role;
import com.mingheinfo.maxdata.manage.entity.UserRole;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    /**
     * 通过roleId查找
     * @param roleId
     * @return
     */
    @Select("select * from t_user_role where role_id=#{roleId}") 	
    List<UserRole> findByRoleId(Integer roleId);

    /**
     * 删除用户角色
     * @param userId
     */
    @Delete("delete from t_user_role where user_id=#{userId}") 
    void deleteByUserId(Integer userId);
    
    /**
     * 通过用户id获取
     * @param userId
     * @return
     */
    @Select("select * from t_user_role where user_id=#{userId}") 
    List<Role> findByUserId(@Param("userId") Integer userId);

    /**
     * 通过用户id获取用户角色关联的部门数据
     * @param userId
     * @return
     */
    List<String> findDepIdsByUserId(@Param("userId") Integer userId);
    
}
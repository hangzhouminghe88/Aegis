package com.mingheinfo.maxdata.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.manage.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 通过用户名获取用户
     * @param username
     * @return
     */
	@Select("select * from t_user where username=#{userName}")
    User findByUserName(String userName);

    /**
     * 通过手机获取用户
     * @param mobile
     * @return
     */
	@Select("select * from t_user where mobile=#{mobile}")
    User findByMobile(String mobile);

    /**
     * 通过邮件获取用户
     * @param email
     * @return
     */
	@Select("select * from t_user where email=#{email}")
    User findByEmail(String email);
	
    /**
     * 通过部门id获取
     * @param departmentId
     * @return
     */
	@Select("select * from t_user where department_id=#{departmentId}")
    List<User> findByDepartmentId(Integer departmentId);	

}
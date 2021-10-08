package com.mingheinfo.maxdata.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.manage.entity.Department;
import com.mingheinfo.maxdata.manage.entity.DepartmentHeader;

/**
 * 部门负责人数据处理层
 * 
 */
public interface DepartmentHeaderMapper extends BaseMapper<DepartmentHeader> {

    /**
     * 通过部门和负责人类型获取
     * @param departmentId
     * @param type
     * @return
     */
	@Select("select * from t_department_header where type=#{type} and department_id=#{departmentId}")  
    List<DepartmentHeader> findByDepartmentIdAndType(@Param("departmentId") Integer departmentId, @Param("type") Integer type);

    /**
     * 通过部门id删除
     * @param departmentId
     */
	@Delete("delete from t_department_header where department_id=#{departmentId}")
    void deleteByDepartmentId(Integer departmentId);

    /**
     * 通过userId删除
     * @param userId
     */
	@Delete("delete from t_department_header where user_id=#{userId}")
    void deleteByUserId(Integer userId);
}
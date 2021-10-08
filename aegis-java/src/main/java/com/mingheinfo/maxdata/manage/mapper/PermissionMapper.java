package com.mingheinfo.maxdata.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.manage.entity.Permission;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    /**
     * 通过用户id获取
     * @param userIdp
     * @return
     */
    List<Permission> findByUserId(@Param("userId") Integer userId);
    
    /**
     * 通过层级查找
     * 默认升序
     * @param level
     * @return
     */
	@Select("select * from t_permission where level=#{level} order by sort_order")    
    List<Permission> findByLevelOrderBySortOrder(Integer level);

    /**
     * 通过parendId查找
     * @param parentId
     * @return
     */
	@Select("select * from t_permission where parent_id=#{parentId} order by sort_order")    
    List<Permission> findByParentIdOrderBySortOrder(Integer parentId);

    /**
     * 通过类型和状态获取
     * @param type
     * @param status
     * @return
     */
	@Select("select * from t_permission where type=#{type} and status=#{status} order by sort_order")    
    List<Permission> findByTypeAndStatusOrderBySortOrder(@Param("type") Integer type, @Param("status") Integer status);

    /**
     * 通过名称获取
     * @param title
     * @return
     */
	
	@Select("select * from t_permission where title=#{title}") 
    List<Permission> findByTitle(String title);

    /**
     * 模糊搜索
     * @param title
     * @return
     */
	@Select("select * from t_permission where title LIKE CONCAT('%',#{title},'%') order by sort_order") 
    List<Permission> findByTitleLikeOrderBySortOrder(String title);    
}
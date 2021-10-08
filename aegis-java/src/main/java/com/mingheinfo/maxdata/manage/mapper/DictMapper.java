package com.mingheinfo.maxdata.manage.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.manage.entity.Dict;
import com.mingheinfo.maxdata.manage.entity.Permission;

import java.util.List;

/**
 * 字典数据处理层
 * 
 */
public interface DictMapper extends BaseMapper<Dict> {

    /**
     * 排序获取全部
     * @return
     */
    @Select("select * from t_dict order by sort_order")
    List<Dict> findAllOrderBySortOrder();

    /**
     * 通过type获取
     * @param type
     * @return
     */
    @Select("select * from t_dict where type=#{type}")
    Dict findByType(String type);

    /**
     * 模糊搜索
     * @param key
     * @return
     */
    @Select("select * from t_dict d where d.title LIKE CONCAT('%',#{key},'%') or d.type like CONCAT('%',#{key},'%') order by d.sort_order")
    List<Dict> findByTitleOrTypeLike(@Param("key") String key);
}
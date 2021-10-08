package com.mingheinfo.maxdata.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.manage.entity.DictData;

/**
 * 字典数据数据处理层
 * 
 */
public interface DictDataMapper extends BaseMapper<DictData> {
    /**
     * 通过dictId和状态获取
     * @param dictId
     * @param status
     * @return
     */
	@Select("select * from t_dict_data where dict_id=#{dictId} and status=#{status} order by sort_order")  
    List<DictData> findByDictIdAndStatusOrderBySortOrder(@Param("dictId") Integer dictId, @Param("status") Integer status);

    /**
     * 通过dictId删除
     * @param dictId
     */
	@Delete("delete from t_dict_data where dict_id=#{dictId}")
    void deleteByDictId(String dictId);
}
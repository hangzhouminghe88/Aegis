package com.mingheinfo.maxdata.core.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mingheinfo.maxdata.core.entity.Alert;

@Mapper
public interface AlertMapper extends BaseMapper<Alert> {
  List<HashMap> queryAlertByService(HashMap<String, Object> map);
  Integer queryAlertByServiceCount(HashMap<String, Object> map);
  List<HashMap> queryAlertByType(HashMap<String, Object> map);
  Integer queryAlertByTypeCount(HashMap<String, Object> map);
}
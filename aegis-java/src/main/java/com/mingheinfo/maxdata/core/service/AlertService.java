package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.core.entity.Alert;
import com.mingheinfo.maxdata.core.mapper.AlertMapper;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertService extends ServiceImpl<AlertMapper, Alert> {
   public List<HashMap> queryAlertByService(HashMap<String, Object> map)
   {
     return this.baseMapper.queryAlertByService(map);
   }

   public Integer queryAlertByServiceCount(HashMap<String, Object> map)
   {
     return this.baseMapper.queryAlertByServiceCount(map);
   }

   public List<HashMap> queryAlertByType(HashMap<String, Object> map)
   {
     return this.baseMapper.queryAlertByType(map);
   }

   public Integer queryAlertByTypeCount(HashMap<String, Object> map)
   {
     return this.baseMapper.queryAlertByTypeCount(map);
   }
}
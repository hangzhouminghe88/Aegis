package com.mingheinfo.maxdata.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.core.entity.SqlPlan;
import com.mingheinfo.maxdata.core.entity.SqlSummary;
import com.mingheinfo.maxdata.core.entity.SqlSummaryRelationModel;
import com.mingheinfo.maxdata.core.mapper.SqlSummaryMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SqlSummaryService extends ServiceImpl<SqlSummaryMapper, SqlSummary>
{
    public void deleteSqlSummary(String service)
    {
        this.baseMapper.deleteSqlSummary(service);
    }

    public void callSqlSummary(String service)
    {
        this.baseMapper.callSqlSummary(service);
    }

    public SqlSummary getPrevDayDataBySqlId(String service, String sqlId) {
        return this.baseMapper.getPrevDayDataBySqlId(service, sqlId);
    }

    public String getMaxGmt(String service)
    {
        return this.baseMapper.getMaxGmt(service);
    }

    public List<SqlSummaryRelationModel> getRelationList(String service, String gmtCreate, String objectName, Integer pageNumber, Integer pageSize)
    {
        return this.baseMapper.getRelationList(service, gmtCreate, objectName, pageNumber, pageSize);
    }

    public Integer getRelationListCnt(String service, String gmtCreate, String objectName)
    {
        return this.baseMapper.getRelationListCnt(service, gmtCreate, objectName);
    }
}
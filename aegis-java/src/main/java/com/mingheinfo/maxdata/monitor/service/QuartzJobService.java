package com.mingheinfo.maxdata.monitor.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mingheinfo.maxdata.monitor.entity.QuartzJob;
import com.mingheinfo.maxdata.monitor.mapper.QuartzJobMapper;


/**
 * 定时任务接口
 * 
 */
@Service
public class QuartzJobService extends ServiceImpl<QuartzJobMapper, QuartzJob>{

    /**
     * 通过类名获取
     * @param jobClassName
     * @return
     */
    public List<QuartzJob> findByJobClassName(String jobClassName) {

        return this.baseMapper.findByJobClassName(jobClassName);
    }
}
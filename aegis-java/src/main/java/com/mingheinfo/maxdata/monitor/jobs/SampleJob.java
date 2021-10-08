package com.mingheinfo.maxdata.monitor.jobs;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 示例带参定时任务
 * @author Exrickx
 */
@Slf4j
public class SampleJob implements Job {
    
	private static final Logger logger = LoggerFactory.getLogger(SampleJob.class);
	
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    	logger.info(String.format("欢迎使用XBoot前后端分离开发平台!作者:Exrick 时间:"+ DateUtil.now()));
    }
}

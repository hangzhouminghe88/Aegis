package com.mingheinfo.maxdata.monitor.controller;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.common.exception.MaxDataException;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.monitor.entity.QuartzJob;
import com.mingheinfo.maxdata.monitor.jobs.OracleStatUpdateJob;
import com.mingheinfo.maxdata.monitor.service.QuartzJobService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/quartzJob")
@Transactional
public class QuartzJobController {
	
	private static final Logger logger = LoggerFactory.getLogger(QuartzJobController.class);
	
    @Autowired
    private QuartzJobService quartzJobService;

    @Autowired
    private Scheduler scheduler;

    @RequestMapping(value = "/getAllByPage",method = RequestMethod.GET)
    public ApiJson getAll(PageVo pageVo){
    	
    	IPage<QuartzJob> page = new Page<QuartzJob>(pageVo.getPageNumber(),pageVo.getPageSize());
    	
    	QueryWrapper<QuartzJob> queryWrapper = new QueryWrapper<QuartzJob>();
    	
    	page = quartzJobService.page(page, queryWrapper);
     
        return ResultUtil.data(page);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public ApiJson addJob(QuartzJob job){
    	System.out.println("job==============" + job);
        List<QuartzJob> list = quartzJobService.findByJobClassName(job.getJobClassName());
        if(list!=null&&list.size()>0){
            return ResultUtil.error("该定时任务类名已存在");
        }
        add(job.getJobClassName(),job.getCronExpression(),job.getParameter());
        quartzJobService.save(job);
        return ResultUtil.success("创建定时任务成功");
    }

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public ApiJson editJob(QuartzJob job){

        delete(job.getJobClassName());
        add(job.getJobClassName(),job.getCronExpression(),job.getParameter());
        job.setStatus(CommonConstant.STATUS_NORMAL);
        quartzJobService.updateById(job);
        return ResultUtil.success("更新定时任务成功");
    }

    //暂停定时任务
    @RequestMapping(value = "/pause",method = RequestMethod.POST)
    public ApiJson pauseJob(QuartzJob job){

        try {
            scheduler.pauseJob(JobKey.jobKey(job.getJobClassName()));
        } catch (SchedulerException e) {
            throw new MaxDataException("暂停定时任务失败");
        }
        job.setStatus(CommonConstant.STATUS_DISABLE);
        quartzJobService.updateById(job);
        return ResultUtil.success("暂停定时任务成功");
    }

    //恢复定时任务
    @RequestMapping(value = "/resume",method = RequestMethod.POST)
    public ApiJson resumeJob(QuartzJob job){

        try {
            scheduler.resumeJob(JobKey.jobKey(job.getJobClassName()));
        } catch (SchedulerException e) {
            throw new MaxDataException("恢复定时任务失败");
        }
        job.setStatus(CommonConstant.STATUS_NORMAL);
        quartzJobService.updateById(job);
        return ResultUtil.success("恢复定时任务成功");
    }

    @RequestMapping(value = "/delByIds/{ids}",method = RequestMethod.DELETE)
    public ApiJson deleteJob(@PathVariable String[] ids){

        for(String id:ids){
            QuartzJob job = quartzJobService.getById(id);
            delete(job.getJobClassName());
            quartzJobService.removeById(job);
        }
        return ResultUtil.success("删除定时任务成功");
    }

    /**
     * 添加定时任务
     * @param jobClassName
     * @param cronExpression
     * @param parameter
     */
    public void add(String jobClassName, String cronExpression, String parameter){

        try {
            //构建job信息
            JobDetail jobDetail = JobBuilder.newJob(getClass(jobClassName).getClass())
                    .withIdentity(jobClassName)
                    .usingJobData("parameter",parameter)
                    .build();

            //表达式调度构建器(即任务执行的时间) 使用withMisfireHandlingInstructionDoNothing() 忽略掉调度暂停过程中没有执行的调度
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();

            //按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName)
                    .withSchedule(scheduleBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
        	logger.error(e.toString());
            throw new MaxDataException("创建定时任务失败");
        } catch (Exception e){
            throw new MaxDataException("后台找不到该类名任务");
        }
    }

    /**
     * 删除定时任务
     * @param jobClassName
     */
    public void delete(String jobClassName){

        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName));
            scheduler.deleteJob(JobKey.jobKey(jobClassName));
        } catch (Exception e) {
            throw new MaxDataException("删除定时任务失败");
        }
    }

    public static Job getClass(String classname) throws Exception {
        Class<?> class1 = Class.forName(classname);
        return (Job)class1.newInstance();
    }

}

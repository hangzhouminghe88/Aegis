package com.mingheinfo.maxdata.monitor.jobs;

import cn.hutool.core.date.DateUtil;
import com.mingheinfo.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mingheinfo.common.database.JdbcConnection;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.common.utils.SpringContextUtils;
import com.mingheinfo.maxdata.core.entity.Host;
import com.mingheinfo.maxdata.core.entity.HostInstance;
import com.mingheinfo.maxdata.core.entity.MysqlTableSpace;
import com.mingheinfo.maxdata.core.entity.SegmentSpace;
import com.mingheinfo.maxdata.core.entity.TableSpace;
import com.mingheinfo.maxdata.core.service.HostInstanceService;
import com.mingheinfo.maxdata.core.service.HostService;
import com.mingheinfo.maxdata.core.service.MysqlTableSpaceService;
import com.mingheinfo.maxdata.core.service.SegmentSpaceService;
import com.mingheinfo.maxdata.core.service.SegmentSpaceSumService;
import com.mingheinfo.maxdata.core.service.TableSpaceService;
import com.mingheinfo.maxdata.core.service.TableSpaceSumService;
/**
 * 采集段空间、表空间相关数据
 * */
@Slf4j
public class OracleDailyMonitorJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger(OracleDailyMonitorJob.class);
	
	private static HostService hostService = SpringContextUtils.getBean(HostService.class);	
	private static SegmentSpaceService segmentSpaceService = SpringContextUtils.getBean(SegmentSpaceService.class);
	private static SegmentSpaceSumService segmentSpaceSumService = SpringContextUtils.getBean(SegmentSpaceSumService.class);
	private static TableSpaceService tableSpaceService = SpringContextUtils.getBean(TableSpaceService.class);
	private static TableSpaceSumService tableSpaceSumService = SpringContextUtils.getBean(TableSpaceSumService.class);
	private static HostInstanceService hostInstanceService = SpringContextUtils.getBean(HostInstanceService.class);
	private static MysqlTableSpaceService mysqlTableSpaceService = SpringContextUtils.getBean(MysqlTableSpaceService.class);
	
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    	
   	
    	QueryWrapper<Host> queryWrapper = new QueryWrapper<Host>();
    	
    	List<Host> list = hostService.list(queryWrapper);
    	for(Host host:list)
    	{
    		if(host.getDbType()!=0) {
    			continue;
    		}
    		logger.info("实例" + host.getService());
    		JdbcConnection conn = JdbcConnection.getInstance(host.getService(), host.getUserName(), host.getPassword(),host.getIpAddr(), host.getPort(), host.getIdentity(), host.getName());
        	if(conn==null)
        	{
        		logger.error("实例："+host.getName()+" 数据库连接失败");
        		continue;
        	}
        	
    		//清除当日数据
        	segmentSpaceService.deleteToday(host.getName());
        	getSegmentSpaceInfo(conn);
        	
        	segmentSpaceSumService.deleteToday(host.getName());
        	segmentSpaceSumService.statSegmentSpace(host.getName());
        	
        	tableSpaceService.deleteToday(host.getName());
        	getTableSpaceInfo(conn);
        	//调用存储过程得到tableSpaceSum表中的数据
        	tableSpaceSumService.callSqlTabSpace(host.getName());
        	
        	//getHostInstanceInfo(conn);
    	}
			mysqlTableSpaceService.callMysqlTabSpace();
    }
    
    private void getSegmentSpaceInfo(JdbcConnection conn)
    {
    	List<SegmentSpace> list = conn.getSegmentSpaceList();
    	
    	segmentSpaceService.saveBatch(list);
    }

    private void getTableSpaceInfo(JdbcConnection conn)
    {
    	List<TableSpace> list = conn.getTableSpaceList();
    	
    	tableSpaceService.saveBatch(list);
    }    
    
    private void getHostInstanceInfo(JdbcConnection conn)
    {
    	List<HostInstance> list = conn.getHostInstanceList();
    	
    	hostInstanceService.saveBatch(list);
    }    
}

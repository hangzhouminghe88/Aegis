package com.mingheinfo.maxdata.monitor.jobs;

import com.mingheinfo.maxdata.core.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mingheinfo.common.database.JdbcConnection;
import com.mingheinfo.common.utils.SpringContextUtils;
import com.mingheinfo.maxdata.core.entity.Host;
import com.mingheinfo.maxdata.core.entity.SysAshWait;
import com.mingheinfo.maxdata.core.entity.SysCustom;
import com.mingheinfo.maxdata.core.entity.SysEvent;
import com.mingheinfo.maxdata.core.entity.SysStat;
import com.mingheinfo.maxdata.core.service.HostService;
import com.mingheinfo.maxdata.core.service.SysAshWaitService;
import com.mingheinfo.maxdata.core.service.SysCustomService;
import com.mingheinfo.maxdata.core.service.SysEventService;
import com.mingheinfo.maxdata.core.service.SysMysqlService;
import com.mingheinfo.maxdata.core.service.SysStatService;
/**
 * 采集oracle指标数据
 * 
 */
@Slf4j
public class OracleStatUpdateJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger(OracleStatUpdateJob.class);

	private static SysStatService sysStatService = SpringContextUtils.getBean(SysStatService.class);
	private static SysEventService sysEventService = SpringContextUtils.getBean(SysEventService.class);
	private static SysMysqlService sysMysqlService = SpringContextUtils.getBean(SysMysqlService.class);
	private static SysCustomService sysCustomService = SpringContextUtils.getBean(SysCustomService.class);
	private static SysAshWaitService sysAshWaitService = SpringContextUtils.getBean(SysAshWaitService.class);
	private static HostService hostService = SpringContextUtils.getBean(HostService.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		// 清空数据
		sysStatService.truncate();
		sysEventService.truncate();
		sysMysqlService.truncate();
		sysCustomService.truncate();
		sysAshWaitService.truncate();

		QueryWrapper<Host> queryWrapper = new QueryWrapper<Host>();

		List<Host> list = hostService.list(queryWrapper);
		for (Host host : list) {
			if (host.getDbType() != 0)
				continue;

			JdbcConnection conn = JdbcConnection.getInstance(host.getService(), host.getUserName(), host.getPassword(),host.getIpAddr(), host.getPort(), host.getIdentity(), host.getName());
			if (conn == null) {
				logger.error("实例：" + host.getName() + " 数据库连接失败");
				try {
					/**
					 * 当连接失败时给予提示
					 * */
					WebSocketService service = new WebSocketService();
					service.broadcast("实例:" + host.getName() + " 数据库连接失败");
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
			}

			getSysStatInfo(conn);
			getSysEventInfo(conn);
			getSysCustomInfo(conn);
			getSysAshWaitInfo(conn);
		}
		sysStatService.callAllCurves();
	}

	private void getSysStatInfo(JdbcConnection conn) {
		List<SysStat> list = conn.getSysStatList();

		sysStatService.saveBatch(list);
	}

	private void getSysEventInfo(JdbcConnection conn) {
		List<SysEvent> list = conn.getSysEventList();

		sysEventService.saveBatch(list);
	}

	private void getSysCustomInfo(JdbcConnection conn) {
		List<SysCustom> list = conn.getSysCustomList();

		sysCustomService.saveBatch(list);
	}

	private void getSysAshWaitInfo(JdbcConnection conn) {
		List<SysAshWait> list = conn.getSysAshWaitList();

		sysAshWaitService.saveBatch(list);
	}

}

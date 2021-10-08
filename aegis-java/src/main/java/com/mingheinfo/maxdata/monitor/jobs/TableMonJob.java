package com.mingheinfo.maxdata.monitor.jobs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mingheinfo.common.database.JdbcConnection;
import com.mingheinfo.common.utils.DateUtils;
import com.mingheinfo.common.utils.SpringContextUtils;
import com.mingheinfo.maxdata.core.entity.Host;
import com.mingheinfo.maxdata.core.entity.Sequence;
import com.mingheinfo.maxdata.core.entity.TableMon;
import com.mingheinfo.maxdata.core.entity.TableMonSum;
import com.mingheinfo.maxdata.core.service.*;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 采集记录变更相关数据
 * */
@Slf4j
public class TableMonJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(TableMonJob.class);
    private static TableMonService tableMonService = SpringContextUtils.getBean(TableMonService.class);
    private static TableMonSumService tableMonSumService = SpringContextUtils.getBean(TableMonSumService.class);
    private static SequenceService sequenceService = SpringContextUtils.getBean(SequenceService.class);
    private static SequenceReportService sequenceReportService = SpringContextUtils.getBean(SequenceReportService.class);
    private static HostService hostService = SpringContextUtils.getBean(HostService.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        QueryWrapper<Host> queryWrapper = new QueryWrapper<Host>();
        List<Host> list = hostService.list(queryWrapper);
        InputStream instream = TableMonJob.class.getClassLoader().getResourceAsStream("config.properties");
 		BufferedReader reader = new BufferedReader(new InputStreamReader(instream));   
 		Properties p = new Properties();
 		 try {
 			p.load(reader);
 		} catch (IOException e1) {
 			// TODO Auto-generated catch block
 			e1.printStackTrace();
 		}
        for(Host host:list)
        {
            if(host.getDbType()!=0)
                continue;

            JdbcConnection conn = JdbcConnection.getInstance(host.getService(), host.getUserName(), host.getPassword(),host.getIpAddr(), host.getPort(), host.getIdentity(), host.getName());
            if(conn==null)
            {
                logger.error("实例："+host.getName()+" 数据库连接失败");
                continue;
            }
            //清除当日
            tableMonService.deleteTody(host.getName());
            getTableMon(conn);
            tableMonSumService.deleteTody(host.getName());
            tableMonSumService.insetTableMonSum(host.getName());
            sequenceService.deleteTody(host.getName());
            getSequence(conn);
            sequenceReportService.deleteTody(host.getName());
            sequenceReportService.insetSequenceReport(host.getName());

        }
    }

    private void getTableMon(JdbcConnection conn) {
        List<TableMon> list = conn.getTableMon();
        tableMonService.saveBatch(list);
    }

    private void getSequence(JdbcConnection conn) {
        List<Sequence> list = conn.getSequence();
        sequenceService.saveBatch(list);
    }
}

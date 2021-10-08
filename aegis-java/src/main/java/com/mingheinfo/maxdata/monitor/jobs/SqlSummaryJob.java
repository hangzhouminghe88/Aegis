package com.mingheinfo.maxdata.monitor.jobs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mingheinfo.common.database.JdbcConnection;
import com.mingheinfo.common.utils.DateUtils;
import com.mingheinfo.common.utils.SpringContextUtils;
import com.mingheinfo.maxdata.core.entity.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 采集sql全文索引数据
 * */
@Slf4j
public class SqlSummaryJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(SqlSummaryJob.class);
    private static SqlSummaryService sqlSummaryService = SpringContextUtils.getBean(SqlSummaryService.class);
    private static SqlSummaryReportService sqlSummaryReportService = SpringContextUtils.getBean(SqlSummaryReportService.class);
    private static SqlTextService sqlTextService = SpringContextUtils.getBean(SqlTextService.class);
    private static SqlPlanService sqlPlanService = SpringContextUtils.getBean(SqlPlanService.class);
    private static SqlMachineService sqlMachineService = SpringContextUtils.getBean(SqlMachineService.class);
    private static SqlObjectService sqlObjectService = SpringContextUtils.getBean(SqlObjectService.class);
    private static SqlModuleService sqlModuleService = SpringContextUtils.getBean(SqlModuleService.class);
    private static TemplatesSummaryService templatesSummaryService = SpringContextUtils.getBean(TemplatesSummaryService.class);
    private static HostService hostService = SpringContextUtils.getBean(HostService.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        QueryWrapper<Host> queryWrapper = new QueryWrapper<Host>();
        List<Host> list = hostService.list(queryWrapper);
        for(Host host : list)
        {
            if(host.getDbType()!=0)
                continue;

            JdbcConnection conn = JdbcConnection.getInstance(host.getService(), host.getUserName(), host.getPassword(),host.getIpAddr(), host.getPort(), host.getIdentity(), host.getName());
            if(conn==null)
            {
                logger.error("实例："+host.getName()+" 数据库连接失败");
                continue;
            }
            //清除sqlsummary
            deleteSqlSummary(host.getName());
            getSqlSummary(conn);
            //清除summaryReport
            deleteSqlSummaryReport(host.getName());
            //插入数据
            insertSqlSumaryReport(host.getName());
            //删除sqlText
            deleteSqlText(host.getName());
            //采集sqlText数据
            getSqlText(conn);
            //删除sqlPlan数据
            deleteSqlPlan(host.getName());
            //采集sqlPlan数据
            getSqlPlan(conn);
            //删除SqlMachine
            deleteSqlMachine(host.getName());
            //采集sqlMachine数据
            getSqlMachine(conn);
             //call sql_sqlsummary('$SERVER', now());
            //  call sql_sqlsum_rpt('$SERVER', now());
            call(host.getService());
            //清除sqlObject
            deleteSqlObject();
            //采集sqlObject数据
            sqlObjectService.insertSqlObject();
            //清空templates_summary
            templatesSummaryService.truncateTemplatesSummary();
            templatesSummaryService.insetTemplatesSummary();
        }
        callSqlModule();
    }
    //清除sqlsummary
    private void deleteSqlSummary (String service)
    {
        if (!service.isEmpty())
        {
            sqlSummaryService.deleteSqlSummary(service);
        }
    }
    //采集sqlSummary数据
    public void  getSqlSummary (JdbcConnection conn)
    {
      if (conn != null)
      {
          List<SqlSummary> list = conn.getSqlSummary();
          sqlSummaryService.saveBatch(list);
      }
    }
    //清除sqlSummaryReport数据
    private void deleteSqlSummaryReport(String service)
    {
        if(!service.isEmpty()) {
            sqlSummaryReportService.deleteSqlSummaryReport(service);
        }
    }
    //
    private void insertSqlSumaryReport(String service)
    {
        sqlSummaryReportService.insertSqlSumaryReport(service);
    }
    //删除sqlText
    private void deleteSqlText(String service)
    {
        if(!service.isEmpty())
        {
            sqlTextService.deleteSqlText(service);
        }
    }
    //采集sqlText数据
    private void getSqlText(JdbcConnection conn)
    {
      if(conn != null)
      {
          List<SqlText> list =  conn.getSqlText();
          sqlTextService.saveBatch(list);
      }
    }
    //删除sqlPlan
    private void deleteSqlPlan(String service)
    {
        if(!service.isEmpty())
        {
            sqlPlanService.deleteSqlPlan(service);
        }
    }
    //采集sqlPlan数据
    private void getSqlPlan(JdbcConnection conn)
    {
        if(conn != null)
        {
            List<SqlPlan> list = conn.getSqlPlan();
            sqlPlanService.saveBatch(list);
        }
    }
    //清除deleteSqlMachine
    private void deleteSqlMachine(String service)
    {
        if(!service.isEmpty())
        {
            sqlMachineService.deleteSqlMachine(service);
        }
    }
    //采集sqlMachine数据
    private void getSqlMachine(JdbcConnection conn)
    {
        if(conn != null)
        {
            List<SqlMachine> list = conn.getSqlMachine();
            sqlMachineService.saveBatch(list);
        }
    }
    //调用存储过程
    private  void call(String service)
    {
        sqlSummaryService.callSqlSummary(service);
        sqlSummaryReportService.callSqlSummaryReport(service);
    }
    //清除deleteSqlObject
    private void deleteSqlObject()
    {
        sqlObjectService.deleteSqlObject();
    }
    //调用存储过程
    private void callSqlModule()
    {
        sqlModuleService.callSqlModule();
    }
}

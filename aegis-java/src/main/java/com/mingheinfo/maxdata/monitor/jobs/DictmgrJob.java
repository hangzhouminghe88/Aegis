package com.mingheinfo.maxdata.monitor.jobs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.QueryChainWrapper;
import com.mingheinfo.common.database.JdbcConnection;
import com.mingheinfo.common.utils.MimeUtils;
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
import java.util.List;
import java.util.Properties;
/**
 * 采集数据字段相关的数据
 * */
@Slf4j
public class DictmgrJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(DictmgrJob.class);
    private static HostService hostService = SpringContextUtils.getBean(HostService.class);
    private static DictReportService dictReportService = SpringContextUtils.getBean(DictReportService.class);
    private static DictColumnsService dictColumnsService = SpringContextUtils.getBean(DictColumnsService.class);
    private static DictIndexesService dictIndexesService = SpringContextUtils.getBean(DictIndexesService.class);
    private static DictIndColsService dictIndColsService = SpringContextUtils.getBean(DictIndColsService.class);
    private static TableMonIndsService tableMonIndsService = SpringContextUtils.getBean(TableMonIndsService.class);
    private static TableMonHistoryService tableMonHistoryService = SpringContextUtils.getBean(TableMonHistoryService.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException  {

        dictReportService.truncate();
        dictColumnsService.truncate();
        dictIndexesService.truncate();
        dictIndColsService.truncate();

        QueryWrapper<Host> queryWrapper = new QueryWrapper<Host>();

        List<Host> list = hostService.list(queryWrapper);
        InputStream instream = DictmgrJob.class.getClassLoader().getResourceAsStream("config.properties");
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
            logger.info("实例" + host.getService());
            JdbcConnection conn = JdbcConnection.getInstance(host.getService(), host.getUserName(), host.getPassword(),host.getIpAddr(), host.getPort(), host.getIdentity(), host.getName());
            if(conn==null)
            {
                logger.error("实例："+host.getName()+" 数据库连接失败");
                continue;
            }
            //获得数据字典数据
            getDictTables(conn);
            //获得数据字典列信息
            getDicttabcols(conn);
            //
            getDictIndexes(conn);
            //
            getDictIndCols(conn);
            //调用存储过程
            callSqlIndCols(host.getName());
            //删除
            deleteDictTablesService();
            //删除
            deleteDictTableColsService();
            //删除
            deleteDictIndexesService();
            //删除
            deleteDictIndColsService();
            //调用存储过程

            //删除
            deleteTableMonInds();
            //
            updateTableMonInds();
            //
            updateTableHistory();
        }
    }
    //获得dictables数据
    private void  getDictTables(JdbcConnection conn) {
        List<DictReport> list = conn.getDictList();
        dictReportService.saveBatch(list);
    }
    //或得dictcolumn数据
    private void  getDicttabcols(JdbcConnection conn) {
        List<DictColumns> list = conn.getDictColumnList();
        dictColumnsService.saveBatch(list);
    }
    //
    private void  getDictIndexes(JdbcConnection conn) {
        List<DictIndexes> list = conn.getDictIndexes();
        dictIndexesService.saveBatch(list);
    }
    //获得dictIndexCols数据
    private void getDictIndCols(JdbcConnection conn) {
        List<DictIndCols> list = conn.getDictIndCols();
        dictIndColsService.saveBatch(list);
    }
    private void deleteDictTablesService() {
        dictReportService.deleteService();
    }
    private void deleteDictTableColsService() {
        dictColumnsService.deleteService();
    }

    private void deleteDictIndexesService() {
        dictIndexesService.deleteService();
    }

    private void deleteDictIndColsService() {
        dictIndColsService.deleteService();
    }
    /**
     * 更新tableIndex表数据
     * */
    private void updateTableMonInds() {
        tableMonIndsService.insertData();
    }

    private void deleteTableMonInds() {
        tableMonIndsService.deleteTableMonInds();
    }
    /**
     * 更新历史数据表
     * */
    private void updateTableHistory() {
        QueryWrapper<DictIndexes> queryWrapper = new QueryWrapper<DictIndexes>();
        logger.info("queryWrapper  =========== " + queryWrapper);
        List<DictIndexes> list = dictIndexesService.list(queryWrapper);
        List<TableMonHistory> tableMonHistoryList = new ArrayList<TableMonHistory>();
        for(DictIndexes entity : list) {
            TableMonHistory tableMonHistory = new TableMonHistory();
            tableMonHistory.setGmtCreate(entity.getGmtCreate());
            if(entity.getColTotal() != null)
            tableMonHistory.setColTotal(entity.getColTotal());
            tableMonHistory.setGmtModified(entity.getGmtModified());
            tableMonHistory.setIncludeColumn(entity.getIncludeColumn());
            tableMonHistory.setIndexName(entity.getIndexName());
            tableMonHistory.setIndexType(entity.getIndexType());
            tableMonHistory.setOwner(entity.getOwner());
            tableMonHistory.setReduStats(entity.getReduStats());
            tableMonHistory.setSegSize(entity.getSegSize());
            tableMonHistory.setService(entity.getService());
            if(entity.getSqlTotal() != null)
            tableMonHistory.setSqlTotal(entity.getSqlTotal());
            tableMonHistory.setUniqueness(entity.getUniqueness());
            tableMonHistory.setTableName(entity.getTableName());
            tableMonHistory.setUsedStats(entity.getUsedStats());
            tableMonHistoryList.add(tableMonHistory);
        }
        tableMonHistoryService.saveBatch(tableMonHistoryList);
    }
    //调用存储过程
    public void callSqlIndCols(String service)
    {
        if(service != null)
        {
            dictIndColsService.callSqlIndCols(service);
        }
    }
}

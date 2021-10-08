package com.mingheinfo.maxdata.monitor.jobs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mingheinfo.common.database.JdbcConnection;
import com.mingheinfo.common.utils.SpringContextUtils;
import com.mingheinfo.maxdata.core.entity.*;
import com.mingheinfo.maxdata.core.service.*;

import cn.hutool.core.lang.Console;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 采集快照相关数据
 **/
@Slf4j
public class SqlSnapShotJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger(SqlSnapShotJob.class);
	private static final HostService hostService = SpringContextUtils.getBean(HostService.class);
	private static final SqlStatService sqlStatService = SpringContextUtils.getBean(SqlStatService.class);
	private static final SqlTextService sqlTextService = SpringContextUtils.getBean(SqlTextService.class);
	private static final SqlPlanService sqlPlanService = SpringContextUtils.getBean(SqlPlanService.class);
	private static final SqlObjectService sqlObjectService = SpringContextUtils.getBean(SqlObjectService.class);
	private static final SqlSessionService sqlSessionService = SpringContextUtils.getBean(SqlSessionService.class);
	private static final SysAshSumService sysAshSumService = SpringContextUtils.getBean(SysAshSumService.class);
	private static final SysMetricService sysMetricService = SpringContextUtils.getBean(SysMetricService.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		QueryWrapper<Host> queryWrapper = new QueryWrapper<Host>();
		List<Host> list = hostService.list(queryWrapper);
		for (Host host : list) {
			if (host.getDbType() != 0)
				continue;

			JdbcConnection conn = JdbcConnection.getInstance(host.getService(), host.getUserName(), host.getPassword(),host.getIpAddr(), host.getPort(), host.getIdentity(), host.getName());
			if (conn == null) {
				logger.error("实例：" + host.getService() + " 数据库连接失败");
				continue;
			}
			executeShell(host.getName(), host.getUserName(), host.getPassword(), host.getIpAddr(), host.getIdentity(), host.getService(), host.getPort());
//			getSqlStat(conn);
//			deleteSqlTextByType(host.getService(), "SQLSNAP");
//			getSqlTextByStat(conn);
//			getSqlPlanByStat(conn);
//			getSqlObjectByStat(conn);
//			getSqlSessionByStat(conn);
//			getSysAwaitSum(conn);
//			getSysMetric(conn);
//			// 调用存储过程
//			sysMetricService.callSqlStat(host.getService());
//			sysMetricService.callSysMetric(host.getService());
		}
	}

	private void executeShell(String service, String username, String password, String host, String identity, String serviceName, String port) {
		String cmdStr = "#!/bin/ksh\n" + "# function:get statpack report\n" + "# usage: crontab on aix&linux\n"
				+ "# 2005-12-27 add comment and script standardization\n" + "export ORACLE_HOME=/home/oracle\n"
				+ "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$ORACLE_HOME/lib\n"
				+ "export NLS_LANG=AMERICAN_AMERICA.ZHS16GBK\n" + "export PATH=$PATH:$HOME/bin:$ORACLE_HOME/bin\n"
				+ "export LANG=zh_CN.uft8\n" + "SQLPLUS=$ORACLE_HOME/bin/sqlplus\n" +

		"#MYSQL_CONN=-uroot -pjaguar -hlocalhost aegis\n" +

		"DATE=`date +%Y-%m-%d`\n" + "TIME=`date +%H-%M-%S`\n" +

		"#start\n" + "SERVER=" + service + "\n" +

		"AWRDIR='/tmp/aegis/awr/'\n" + "ADMDIR='/tmp/aegis/adm/'\n" + "OUTFILE=$SERVER'_adm_'$DATE'_'$TIME.txt\n"
				+ "OUTPUT=$ADMDIR$OUTFILE\n" +		
		"SNAPFILE='/tmp/aegis/sqlmon/snapshot_'$SERVER'.lst'\n" + "$SQLPLUS -s "+ username + "/" + password + "@" + host +":"+port+"/"+ serviceName + "<<EOF\n" + "set echo off\n"
				+ "set feedback off\n" + "set heading off\n" + "set pagesize 0\n" + "set linesize 121\n"
				+ "set trimspool on\n" +

		"var bst varchar2(32);\n" + "var est varchar2(32);\n" + "var bid number;\n" + "var eid number;\n"
				+ "var awr varchar2(99);\n" +

		"begin\n" + "select min(snap_id), max(snap_id),\n"
				+ "to_char(max(begin_interval_time), 'yyyy-mm-dd hh24:mi:ss'),\n"
				+ "to_char(max(end_interval_time), 'yyyy-mm-dd hh24:mi:ss')\n" + "into :bid, :eid, :bst, :est\n"
				+ "from (select snap_id, begin_interval_time, end_interval_time\n"
				+ "from v\\$instance i, dba_hist_snapshot s\n" + "where begin_interval_time >= sysdate - 60/360\n"
				+ "and i.instance_number = s.instance_number\n" + "order by snap_id desc)\n" + "where rownum < 3;\n"
				+ "select 'awr_'||:bid||'_'||:eid||'.txt' into :awr from dual;\n" + "end;\n" + "/\n"
				+ "spool $SNAPFILE\n" + "col val format a120 newline\n"
				+ "select :bst||'@'||:est||'@'||'$SERVER'||'_'||:awr||'@'||'$SERVER'||'@'||:bid||'@'||:eid||'@'||'$SERVER' val\n"
				+ "from dual;\n" + "spool off\n" +

		"@/tmp/aegis/snapshot.sql\n" +

		"spool $OUTPUT\n" + "set serveroutput on size 1000000\n" + "set long 1000000\n" + "set pagesize 0\n"
				+ "select dbms_advisor.get_task_report(t.task_name, 'TEXT', 'ALL', 'ALL', 'SYS') as ADDM_report\n"
				+ "from dba_advisor_tasks t\n" + "where exists (select 1\n" + "from dba_advisor_recommendations r\n"
				+ "where t.task_id = r.task_id)\n" + "and t.task_id = (select max(t.task_id)\n"
				+ "from dba_advisor_tasks t\n" + "where t.advisor_name = 'ADDM')\n" + "and t.status = 'COMPLETED';\n"
				+ "spool off\n" +

		"EOF\n" +

		"AWRFNAME=`sed 's/'$SERVER'_//g' $SNAPFILE |grep @| cut -f3 -d @`\n"
				+ "echo /bin/mv $AWRDIR$AWRFNAME $AWRDIR$SERVER'_'$AWRFNAME\n"
				+ "/bin/mv $AWRDIR$AWRFNAME $AWRDIR$SERVER'_'$AWRFNAME\n" +

		"mysql -uroot -pjaguar -hlocalhost aegis <<EOF\n"
				+ "load data infile '$SNAPFILE' into table snapshot fields terminated by '@' enclosed by ''\n"
				+ "(create_time, end_time, report_name, db_name, begin_snap, end_snap, service);\n" + "EOF\n" +

		"cnt=`cat $OUTPUT|wc -l`\n" + "if [ $cnt -gt 9 ]; then\n" + "mysql -uroot -pjaguar -hlocalhost aegis <<EOF\n"
				+ "update snapshot\n" + "set addm_rpt_name = '$OUTFILE'\n"
				+ "where db_name = '$SERVER' and end_time > now() - interval 30 minute;\n" + "EOF\n" + "else\n"
				+ "/bin/rm $OUTPUT\n" + "fi\n"
						+ "sh " + System.getProperty("user.dir") + "/sqlstat-" + service + ".sh >" +"/tmp/aegis/sqlmon/c1c_sqlstat.log";
		String cmdSql = "Rem awrrpt_mayj.sql\n" +

		"set echo off;\n" + "set veri off;\n" + "set feedback off;\n" + "set termout on;\n" + "set heading off;\n" +

		"variable rpt_options number;\n" +

		"define NO_OPTIONS = 0;\n" + "define ENABLE_ADDM = 8;\n" +

		"rem according to your needs, the value can be 'text' or 'html'\n" + "variable dbid number;\n"
				+ "variable inst_num number;\n" + "variable bid number;\n" + "variable eid number;\n"
				+ "define report_type='text';\n" + "begin\n" + ":rpt_options := &NO_OPTIONS;\n"
				+ "select dbid into :dbid from v$database;\n"
				+ "select instance_number into :inst_num from v$instance;\n"
				+ "select min(snap_id), max(snap_id) into :bid, :eid\n"
				+ "from (select snap_id from dba_hist_snapshot where instance_number = :inst_num order by snap_id desc)\n"
				+ "where rownum < 3;\n" + "end;\n" + "/\n" +

		"column ext new_value ext noprint\n" + "column fn_name new_value fn_name noprint;\n"
				+ "column lnsz new_value lnsz noprint;\n" +

		"select 'txt' ext from dual where lower('&report_type') = 'text';\n"
				+ "select 'html' ext from dual where lower('&report_type') = 'html';\n"
				+ "select 'awr_report_text' fn_name from dual where lower('&report_type') = 'text';\n"
				+ "select 'awr_report_html' fn_name from dual where lower('&report_type') = 'html';\n"
				+ "select '80' lnsz from dual where lower('&report_type') = 'text';\n" +

		"set linesize 80;\n" + "column report_name new_value report_name noprint;\n" +

		"select '/tmp/aegis/awr/awr_'||:bid||'_'||:eid||'.txt' report_name from dual;\n" + "set termout off\n"
				+ "spool &report_name;\n"
				+ "select output from table(dbms_workload_repository.&fn_name(:dbid, :inst_num, :bid, :eid, :rpt_options ));\n"
				+ "spool off;\n" + "set termout on;\n" + "clear columns sql;\n" + "ttitle off;\n" + "btitle off;\n"
				+ "repfooter off;\n" + "undefine report_name\n" + "undefine report_type\n" + "undefine fn_name\n"
				+ "undefine lnsz\n" + "undefine NO_OPTIONS";
		String cmdStat = "#!/bin/sh\n"
				+ "export ORACLE_HOME=/home/oracle\n"
				+ "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$ORACLE_HOME/lib\n"
				+ "export NLS_LANG=AMERICAN_AMERICA.ZHS16GBK\n"
				+ "export PATH=$PATH:$HOME/bin:$ORACLE_HOME/bin\n"
				+ "export LANG=en_US\n"
				+ "SQLPLUS=$ORACLE_HOME/bin/sqlplus\n"
				+ "#start\n"
				+ "SERVER=" + service + "\n" 
				+ "OUTFILE1=/tmp/aegis/sqlmon/sqlstat_$SERVER.lst\n"
				+ "OUTFILE2=/tmp/aegis/sqlmon/sqltext_$SERVER.lst\n"
				+ "OUTFILE3=/tmp/aegis/sqlmon/sqlplan_$SERVER.lst\n"
				+ "OUTFILE4=/tmp/aegis/sqlmon/objtype_$SERVER.lst\n"
				+ "OUTFILE5=/tmp/aegis/sqlmon/machine_$SERVER.lst\n"
				+ "OUTFILE6=/tmp/aegis/sqlmon/sysash2_$SERVER.lst\n"
				+ "OUTFILE7=/tmp/aegis/sqlmon/smetric_$SERVER.lst\n"
				+ "$SQLPLUS -s "+ username + "/" + password + "@" + host +":"+port+"/"+ serviceName + " << EOF\n"
				+ "set echo off\n"
				+ "set feedback off\n"
				+ "set heading off\n"
				+ "set pagesize 0 \n"
				+ "set linesize 130\n"
				+ "set trimspool on\n"
				+ "spool $OUTFILE1\n"
				+ "select '$SERVER'||';'||s.snap_id||';'||to_char(s.end_interval_time, 'yyyy-mm-dd hh24:mi')||';'||\n"
				+ "t.sql_id||';'||t.version_count||';'||t.executions_delta||';'||\n"
				+ "round(sorts_delta/(decode(executions_delta,0,1,executions_delta)), 4)||';'||\n"
				+ "round(disk_reads_delta/(decode(executions_delta,0,1,executions_delta)), 4)||';'||\n"
				+ "round(buffer_gets_delta/(decode(executions_delta,0,1,executions_delta)), 4)||';'||\n"
				+ "round(cpu_time_delta/(decode(executions_delta,0,1,executions_delta))/1000000, 4)||';'||\n"
				+ "round(elapsed_time_delta/(decode(executions_delta,0,1,executions_delta))/1000000, 4)||';'||\n"
				+ "round(parse_calls_delta/(decode(executions_delta,0,1,executions_delta)), 4)||';'||\n"
				+ "round(rows_processed_delta/(decode(executions_delta,0,1,executions_delta)), 2)||';'||\n"
				+ "parsing_schema_name||';'||trim(substr(substr(module,1,instr(module,'(')-1),instr(module,'@')+1))\n"
				+ "from dba_hist_snapshot s, dba_hist_sqlstat t\n"
				+ "where s.snap_id = t.snap_id and s.end_interval_time >= sysdate - INTERVAL '30' MINUTE \n"
				+ "and (t.module not like 'PL/SQL%' and t.module <> 'plsqldev.exe')\n"
				+ "and t.parsing_schema_name not in ('MONITOR') and t.executions_delta >= 0;\n"
				+ "spool off;\n"
				+ "spool $OUTFILE2\n"
				+ "select '$SERVER'||';'||f.sql_id||';'||replace(f.sql_text,';',',')||';'||f.piece||';SQLSNAP'\n"
				+ "from dba_hist_snapshot s, dba_hist_sqlstat t, v\\$sqltext f\n"
				+ "where s.snap_id = t.snap_id and s.end_interval_time >= sysdate - INTERVAL '30' MINUTE\n"
				+ "and (t.module not like 'PL/SQL%' and t.module <> 'plsqldev.exe')\n"
				+ "and t.sql_id = f.sql_id and t.executions_delta >= 0\n"
				+ "and t.parsing_schema_name not in ('MONITOR');\n"
				+ "spool off;\n"
				+ "spool $OUTFILE3\n"
				+ "select '$SERVER'||';'||t.sql_id||';'||t.plan_hash_value||';'||p.id||';'||\n"
				+ "lpad(' ',(depth-1))||p.operation||';'||p.options||';'||p.object_name||';'||\n"
				+ "decode(cardinality,null,'  ',\n"
				+ "decode(sign(cardinality-1000), -1, cardinality||' ',\n"
				+ "decode(sign(cardinality-1000000), -1, trunc(cardinality/1000)||'K',\n"
				+ "decode(sign(cardinality-1000000000), -1, trunc(cardinality/1000000)||'M',\n"
				+ "trunc(cardinality/1000000000)||'G'))))||';'||\n"
				+ "decode(bytes,null,' ',\n"
				+ "decode(sign(bytes-1024), -1, bytes||' ',\n"
				+ "decode(sign(bytes-1048576), -1, trunc(bytes/1024)||'K',\n"
				+ "decode(sign(bytes-1073741824), -1, trunc(bytes/1048576)||'M',\n"
				+ "trunc(bytes/1073741824)||'G'))))||';'||\n"
				+ " decode(cost,null,' ',\n"
				+ "decode(sign(cost-10000000), -1, cost||' ',\n"
				+ "decode(sign(cost-1000000000), -1, trunc(cost/1000000)||'M',\n"
				+ "trunc(cost/1000000000)||'G')))||';'||\n"
				+ "decode(temp_space,null,' ',\n"
				+ "decode(sign(temp_space-10000000), -1, trunc(temp_space/1000,2)||'K',\n"
				+ "decode(sign(temp_space-1000000000), -1, trunc(temp_space/1000000)||'M',\n"
				+ "trunc(temp_space/1000000000)||'G')))||';SQLSNAP' as sql_plan\n"
				+ "from dba_hist_snapshot s, dba_hist_sqlstat t, dba_hist_sql_plan p\n"
				+ "where s.snap_id = t.snap_id and s.end_interval_time >= sysdate - INTERVAL '30' MINUTE\n"
				+ "and (t.module not like 'PL/SQL%' and t.module <> 'plsqldev.exe')\n"
				+ "and t.sql_id = p.sql_id and t.plan_hash_value = p.plan_hash_value\n"
				+ "and t.parsing_schema_name not in ('MONITOR') and t.executions_delta >= 0\n"
				+ "and p.timestamp = (select max(timestamp)\n"
				+ "from dba_hist_sql_plan sp "
				+ "where sp.sql_id = t.sql_id and p.plan_hash_value = sp.plan_hash_value);\n"
				+ "spool off;\n"
				+ "spool $OUTFILE4\n"
				+ "select '$SERVER'||';'||object_name||';'||object_type||';'||'SQLSNAP'\n"
				+ "from dba_objects\n"
				+ "where last_ddl_time >= trunc(sysdate);\n"
				+ "spool off\n"
				+ "spool $OUTFILE5\n"
				+ "select '$SERVER'||';'||machine||';'||count(*)||';'||to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')\n"
				+ "from v\\$session\n"
				+ "group by machine;\n"
				+ "spool off;\n"
				+ "spool $OUTFILE6\n"
				+ "select '$SERVER'||';'||t.wait_class||';'||substr(t.event,0,32)||';'||a.owner||';'||a.object_name||';'||a.object_type||';'||\n"
				+ "t.cnt||';'||s.snap_id||';'||to_char(trunc(s.end_interval_time,'mi'), 'yyyy-mm-dd hh24:mi:ss')\n"
				+ "from ( select wait_class, event, current_obj#, count(*) as cnt\n"
				+ "from v\\$active_session_history\n"
				+ "where wait_class is not null\n"
				+ "group by wait_class, event, current_obj#\n"
				+ "having count(*) > 1) t, dba_objects a, dba_hist_snapshot s\n"
				+ "where t.current_obj# = a.object_id(+)\n"
				+ "and s.end_interval_time > sysdate - INTERVAL '30' MINUTE;\n"
				+ "spool off\n"
				+ "spool $OUTFILE7\n"
				+ "select '$SERVER'||';'||s.snap_id||';'||to_char(s.end_interval_time, 'yyyy-mm-dd hh24:mi')||';'||t.metric_id||';'||\n"
				+ "t.metric_name||';'||trunc(t.average,2)||';'||trunc(t.maxval,2)||';'||trunc(t.standard_deviation,2)\n"
				+ "from dba_hist_snapshot s, dba_hist_sysmetric_summary t\n"
				+ " where s.snap_id = t.snap_id and s.end_interval_time >= sysdate - INTERVAL '30' MINUTE\n"
				+ "and length(t.metric_name) < 40;\n"
				+ "spool off;\n"
				+ "exit;\n"
				+ "EOF\n"
				//連接mysql數據庫
				+ "mysql -uroot -pjaguar -hlocalhost aegis <<EOF\n"
				+ "load data infile '$OUTFILE1' replace into table sql_stat fields terminated by ';' enclosed by ''\n"
						+ "(service, snap_id, snap_time, sql_id, vc, exec_delta, sort_delta, disk_delta,\n"
						+ "buff_delta, cpu_time, elap_time, parse_delta, rows_delta, schema_name, module);\n"
						+ "load data infile '$OUTFILE2' replace into table sql_text fields terminated by ';' enclosed by ''\n"
								+ "(service, sql_id, sql_text, piece, sql_type);\n"
								+ "load data infile '$OUTFILE3' replace into table sql_plan fields terminated by ';' enclosed by ''\n"
										+ "(service, sql_id, plan_hash_value, plan_id, operation, options, object_name, cardinality,\n"
										+ "bytes, cost, temp_space, plan_type);\n"
										+ "load data infile '$OUTFILE4' replace into table sql_object fields terminated by ';' enclosed by ''\n"
												+ "(service, object_name, object_type, plan_type);\n"
												+ "load data infile '$OUTFILE5' replace into table sql_session fields terminated by ';' enclosed by ''\n"
														+ "(service, machine, cnt, gmt_create);"
														+ "load data infile '$OUTFILE6' replace into table sys_ash_sum fields terminated by ';' enclosed by ''\n"
																+ "(service, wait_class, event, owner, object_name, object_type, total_waits, snap_id, snap_time);\n"
																+ "load data infile '$OUTFILE7' replace into table sys_metric fields terminated by ';' enclosed by ''\n"
																		+ "(service, snap_id, gmt_create, metric_id, metric_name, avg, max, sdval);\n"
																		+ "call sql_sqlstat('$SERVER');\n"
																		+ "EOF\n"
																		//連接mysql數據庫
																		+ "mysql -uroot -pjaguar -hlocalhost aegis <<EOF\n"
																		+ "call sql_sysmetric('$SERVER');\n"
																		+ "EOF\n";
		try {
			// 创建临时存储目录来存储快照文件
			File file1 = new File("/tmp/aegis");
			if (!file1.exists()) {
				file1.mkdirs();
				File file2 = new File("/tmp/aegis/awr");
				File file3 = new File("/tmp/aegis/adm");
				File file4 = new File("/tmp/aegis/sqlmon");
				if (!file2.exists())
					file2.mkdirs();
				if (!file3.exists())
					file3.mkdirs();
				if (!file4.exists())
					file4.mkdirs();
			}else {
				//生成sql腳本
				String snapshotSQL = "/tmp/aegis/snapshot.sql";
				File SQLFile = new File(snapshotSQL);
				if (!SQLFile.exists()) {
					SQLFile.createNewFile();
					FileOutputStream out = new FileOutputStream(snapshotSQL);
					// 将脚本写入文件
					out.write(cmdSql.getBytes());
					// 关闭文件输出流
					out.close();
				}
			}
			// 生成快照shell脚本导出快照文件
			String path = System.getProperty("user.dir") + "/snapshot-" + service + ".sh";
			String statPath = System.getProperty("user.dir") + "/sqlstat-" + service + ".sh";
			File file = new File(path);
			File fileStat = new File(statPath);
			// 如果脚本文件不存在则生成脚本文件
						if (!fileStat.exists()) {
							fileStat.createNewFile();
							FileOutputStream out = new FileOutputStream(statPath);
							// 将脚本写入文件
							out.write(cmdStat.getBytes());
							// 关闭文件输出流
							out.close();
							// 执行脚本
							// 如果脚本文件不存在则生成脚本文件
							if (!file.exists()) {
								file.createNewFile();
								FileOutputStream out1 = new FileOutputStream(path);
								// 将脚本写入文件
								out1.write(cmdStr.getBytes());
								// 关闭文件输出流
								out1.close();
								// 执行脚本
								this.runShell("sh " + path);
							}else {
								this.runShell("sh " + path);
							}
						}else {
							// 如果脚本文件不存在则生成脚本文件
							if (!file.exists()) {
								file.createNewFile();
								FileOutputStream out = new FileOutputStream(path);
								// 将脚本写入文件
								out.write(cmdStr.getBytes());
								// 关闭文件输出流
								out.close();
								// 执行脚本
								this.runShell("sh " + path);
							}else {
								this.runShell("sh " + path);
							}
						}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 获得sqlstat
	private void getSqlStat(JdbcConnection conn) {
		if (conn != null) {
			List<SqlStat> list = conn.getSqlStat();
			sqlStatService.saveBatch(list);
		}
	}

	// 获得sqlText
	private void getSqlTextByStat(JdbcConnection conn) {
		if (conn != null) {
			List<SqlText> list = conn.getSqlTextByStat();
			logger.info("sqlText list" + list.size());
			sqlTextService.saveBatch(list);
		}
	}

	// 获得SqlPlan
	private void getSqlPlanByStat(JdbcConnection conn) {
		if (conn != null) {
			List<SqlPlan> list = conn.getSqlPlanByStat();
			sqlPlanService.saveBatch(list);
		}
	}

	// 获得SqlObject
	private void getSqlObjectByStat(JdbcConnection conn) {
		if (conn != null) {
			List<SqlObject> list = conn.getSqlObjectByStat();
			sqlObjectService.saveBatch(list);
		}
	}

	// 获得sqlSession
	private void getSqlSessionByStat(JdbcConnection conn) {
		if (conn != null) {
			List<SqlSession> list = conn.getSqlSessionByStat();
			sqlSessionService.saveBatch(list);
		}
	}

	// 或得sys_awiat_sum
	private void getSysAwaitSum(JdbcConnection conn) {
		if (conn != null) {
			List<SysAshSum> list = conn.getSysAwaitSum();
			sysAshSumService.saveBatch(list);
		}
	}

	// 获得systemMetric
	private void getSysMetric(JdbcConnection conn) {
		if (conn != null) {
			List<SysMetric> list = conn.getSysMetric();
			sysMetricService.saveBatch(list);
		}
	}

	// 删除sqlText
	private void deleteSqlTextByType(String service, String type) {
		if (!service.isEmpty() && !type.isEmpty()) {
			sqlTextService.deleteSqlTextByType(service, type);
		}
	}

	public static List<String> runShell(String shStr) {
		List<String> strList = new ArrayList<String>();
		logger.info("==============" + "进入shell脚本执行类" + shStr);
		try {
			Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", shStr }, null, null);
			InputStreamReader ir = new InputStreamReader(process.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line;
			process.waitFor();
			while ((line = input.readLine()) != null) {
				strList.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("读取的shell脚本" + strList.toString());
		return strList;
	}

	// public static void main(String[] args) {
	// executeShell("maxdata1");
	// }
}

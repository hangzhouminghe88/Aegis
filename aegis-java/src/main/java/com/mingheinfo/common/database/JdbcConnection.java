package com.mingheinfo.common.database;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.mingheinfo.common.utils.DateUtils;
import com.mingheinfo.maxdata.core.entity.*;
import com.mingheinfo.maxdata.core.service.WebSocketService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcConnection {
	private static final Logger logger = LoggerFactory.getLogger(JdbcConnection.class);

	private Connection conn = null;
	private boolean disconnect = false;
	private String service = "";
	
	public JdbcConnection(Connection conn,String service)
	{
		this.conn = conn;
		this.service = service;
	}
	
	public static JdbcConnection getInstance(String service,String userName,String password, String host, String port, String identity, String name) 
	{
		Connection conn = null;
//		String os = System.getProperty("os.name");
//		if(os.toLowerCase().contains("windows"))
//		{
//			//动态获取环境变量的值；Windows  D:\oracle\instantclient_12_2\network\admin
//			System.setProperty("oracle.net.tns_admin",
//							"D:\\oracle\\instantclient_12_2\\network\\admin");
//		}
//		else
//		{
//			System.setProperty("oracle.net.tns_admin",
//							"//home//oracle//network//admin");
//		}
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Properties conProps = new Properties();
	        conProps.put("user", userName);
	        conProps.put("password", password);
	        conProps.put("defaultRowPrefetch", "15");
	        conProps.put("internal_logon", identity);
	        System.out.print("jdbc:oracle:thin:@//"+host+":"+port+"/" + service);
			conn = DriverManager.getConnection("jdbc:oracle:thin:@//"+host+":"+port+"/" + service, conProps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(conn==null)
			return null;
	
		return new JdbcConnection(conn,name);
	}
	
	public boolean isClosed() throws SQLException
	{
		return conn.isClosed();
	}
	
	// 执行查询SQL语句
	public ResultSet queryBySql(String sql) {
		Statement sm = null;
		ResultSet rs = null;
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sql);

			info();
			logger.info("查询sql:{}", sql);

		} catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {
			}
			if (e.getMessage().contains("SCN_TO_TIMESTAMP")) {
				executeBySql("alter function sys.scn_to_timestamp compile");
			}
		}
		return rs;
	}

	// 执行 SQL语句
	public void executeBySql(String sql) {
		Statement sm = null;
		try {
			sm = this.conn.createStatement();
			sm.execute(sql);
			sm.close();

			info();
			logger.info("执行sql:{}", sql);

		} catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
			} catch (SQLException localSQLException1) {
			}
		}
	}


	// 
	public List<SysStat> getSysStatList() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;

		String sql = "select TRUNC(SYSDATE,'MI'),STATISTIC#,NAME,CLASS,VALUE,STAT_ID  from v$sysstat";

		List<SysStat> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sql);
			while (rs.next()) {
				SysStat entity = new SysStat();
				
				entity.setService(service);
				System.out.println("stat time:["+rs.getString(1)+"]");
				
				entity.setGmtCreate(new Date(rs.getDate(1).getTime()));
				entity.setStatistics(rs.getLong(2));
				entity.setName(rs.getString(3));
				entity.setClassNum(rs.getLong(4));
				entity.setValue(rs.getLong(5));
				entity.setStatId(rs.getLong(6));
				
				list.add(entity);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取v$sysstat信息");
		} catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取v$sysstat信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {
			}
		}
		return list;
	}

	public List<SysEvent> getSysEventList() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;

		String sql = "select TRUNC(SYSDATE,'MI'),EVENT,TOTAL_WAITS,TIME_WAITED,AVERAGE_WAIT from v$system_event";

		List<SysEvent> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sql);
			while (rs.next()) {
				SysEvent entity = new SysEvent();
				
				entity.setService(service);

				entity.setCreateTime(new Date(rs.getDate(1).getTime()));
				entity.setEvent(rs.getString(2));
				entity.setTotalWaits(rs.getLong(3));
				entity.setTimeWaited(rs.getLong(4));
				entity.setAvgWait(rs.getFloat(5));
				
				list.add(entity);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取v$system_event信息");
		} catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取v$system_event信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {
			}
		}
		return list;
	}
	
	public List<SysCustom> getSysCustomList() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;

		StringBuilder sb = new StringBuilder();
		sb.append("select TRUNC(SYSDATE,'MI'),name,value from (");
		sb.append("select 'active session' as name, count(*) as value from v$session where status = 'ACTIVE' and type = 'USER'");
		sb.append(" union all");
		sb.append(" select 'total session', count(*) from v$session");
		sb.append(" union all");
		sb.append(" select 'log file switch', count(*) as cnt from v$archived_log where completion_time >= sysdate - 5 / 1440 and dest_id = 1");
		sb.append(" union all");
		sb.append(" select 'total used space', trunc(sum(bytes)/1024/1024/1024, 2) from dba_segments");
		sb.append(" union all");
		sb.append(" select 'shared pool usage', round((i.bytes - s.bytes)/i.bytes, 2) * 100 from v$sgastat s, v$sgainfo i where s.pool = 'shared pool' and s.name = 'free memory' and i.name = 'Shared Pool Size'");
		sb.append(" union all");
		sb.append(" select 'session usage', trunc(count(*)/max(p.value),4) * 100 from v$parameter p, v$session s where p.name = 'sessions' group by p.name)");
		

		List<SysCustom> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next()) {
				SysCustom entity = new SysCustom();
				
				entity.setService(service);
				entity.setCreateTime(new Date(rs.getDate(1).getTime()));
				entity.setName(rs.getString(2));
				entity.setValue(rs.getFloat(3));
			
				list.add(entity);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取v$session信息");
		} catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取v$session信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {
			}
		}
		return list;
	}	
	
	public List<SysAshWait> getSysAshWaitList() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;

		StringBuilder sb = new StringBuilder();
		
		sb.append("select t.wait_class,t.event,a.owner,a.object_name,a.object_type,t.cnt from (");
		sb.append("select wait_class, event, current_obj#, count(*) as cnt from v$active_session_history where wait_class is not null group by wait_class, event, current_obj#");
		sb.append(") t, dba_objects a where t.current_obj# = a.object_id(+)");

		List<SysAshWait> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next()) {
				SysAshWait entity = new SysAshWait();
				
				entity.setService(service);
				entity.setCreateTime(new Date());
				entity.setWaitClass(rs.getString(1));
				entity.setEvent(rs.getString(2));
				entity.setOwner(rs.getString(3));
				entity.setObjectName(rs.getString(4));
				entity.setObjectType(rs.getString(5));
				entity.setTotalWaits(rs.getLong(6));
			
				list.add(entity);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取v$active_session_history信息");
		} catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取v$active_session_history信息");
			logger.error("exception:{}", e.getMessage(), e);
			/**
			 * 当连接失败时给予提示
			 * */
			WebSocketService webSocketService = new WebSocketService();
			webSocketService.broadcast("实例:" + service + e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {
			}
		}
		return list;
	}	
	
	public List<SegmentSpace> getSegmentSpaceList() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;

		StringBuilder sb = new StringBuilder();
		
		sb.append("select s.owner,s.segment_name,s.partition_name,s.segment_type,s.used_space,to_char(s.gmt_create, 'yyyy-mm-dd'),to_char(o.last_ddl_time, 'yyyy-mm-dd hh24:mi:ss'),s.tablespace_name from (");
		sb.append("select owner, segment_name, partition_name, segment_type, tablespace_name,trunc(bytes/1024/1024) as used_space, trunc(sysdate) as gmt_create,row_number() over(partition by segment_name order by partition_name desc)");
		sb.append("from dba_segments where owner not in ('SYS', 'SYSTEM', 'WMSYS', 'DBSNMP', 'OUTLN', 'MONITOR', 'READONLY', 'PERFSTAT') and segment_name not like '%TMP%' and bytes/1024/1024 > 1");
		sb.append(") s, dba_objects o where s.owner = o.owner and s.segment_name = o.object_name and nvl(s.partition_name,'1') = nvl(o.subobject_name,'1') and s.segment_type = o.object_type");

		List<SegmentSpace> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next()) {
				SegmentSpace entity = new SegmentSpace();
				
				entity.setService(service);
				entity.setOwner(rs.getString(1));
				entity.setSegmentName(rs.getString(2));
				entity.setPartitionName(rs.getString(3));
				entity.setSegmentType(rs.getString(4));
				entity.setUsedSpace(rs.getFloat(5));
				entity.setCreateTime(DateUtils.parseDate(rs.getString(6)));
				entity.setLastDdlTime(DateUtils.parseDate(rs.getString(7)));
				entity.setTableSpaceName(rs.getString(8));
			
				list.add(entity);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取SegmentSpace信息");
		} catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取SegmentSpace信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {
			}
		}
		return list;
	}		
	
	public List<TableSpace> getTableSpaceList() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;

		StringBuilder sb = new StringBuilder();
		
		sb.append("select tablespace,total_space,used_space,free_space,to_char(trunc(used_space/total_space, 4) * 100),to_char(sysdate, 'yyyy-mm-dd') ");
		sb.append("from (select t.name tablespace, u.tablespace_size * 8/1024 total_space, ");
		sb.append("trunc(u.tablespace_usedsize * 8/1024, 1) as used_space,trunc((u.tablespace_size - u.tablespace_usedsize) * 8/1024, 1) as free_space ");
		sb.append("from v$tablespace t, dba_hist_tbspc_space_usage u ");
		sb.append("where u.snap_id = (select max(snap_id) from dba_hist_snapshot where end_interval_time >= sysdate - 30/1440) ");
		sb.append("and t.ts# = u.tablespace_id and (instr(t.name,'UNDO')=0 and instr(t.name,'TEMP')=0))");

		List<TableSpace> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next()) {
				TableSpace entity = new TableSpace();
				
				entity.setService(service);
				entity.setName(rs.getString(1));
				entity.setTotalSpace(rs.getFloat(2));
				entity.setUsedSpace(rs.getFloat(3));
				entity.setFreeSpace(rs.getFloat(4));
				entity.setUsedRate(rs.getFloat(5));
				entity.setGmtCreate(DateUtils.parseDate(rs.getString(6)));
			
				list.add(entity);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取TableSpace信息");
		} catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取TableSpace信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {
			}
		}
		return list;
	}	
	
	public List<HostInstance> getHostInstanceList() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;

		String sql = "select instance_number,instance_name,instance_role,status,database_status,host_name,version,to_char(startup_time,'yyyy-mm-dd hh24:mi:ss') from v$instance";

		List<HostInstance> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sql);
			while (rs.next()) {
				HostInstance entity = new HostInstance();
				
				entity.setService(service);
				entity.setInstanceId(rs.getInt(1));
				entity.setInstanceName(rs.getString(2));
				entity.setInstanceRole(rs.getString(3));
				entity.setInstanceStatus(rs.getString(4));
				entity.setDatabaseStatus(rs.getString(5));
				entity.setHostName(rs.getString(6));
				entity.setVersion(rs.getString(7));
				entity.setStartupTime(DateUtils.parseDate(rs.getString(8)));
				
				list.add(entity);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取HostInstance信息");
		} catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取HostInstance信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {
			}
		}
		return list;
	}

	public List<DictReport> getDictList() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;
		String sql = "select t.owner, t.table_name,t.num_rows,t.blocks,avg_row_len,to_char(nvl(t.last_analyzed, sysdate), 'yyyy-mm-dd hh24:mi:ss'),c.comments from dba_tables t, dba_tab_comments c where t.table_name=c.table_name and t.owner=c.owner and t.owner not in ('SYS', 'SYSTEM', 'WMSYS', 'DBSNMP', 'OUTLN', 'MONITOR',  'TSMSYS', 'PERFSTAT', 'XDB') and t.table_name not like  'TMP%'";
		List<DictReport> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sql);
			while (rs.next()) {
				DictReport model = new DictReport();
				model.setService(service);
				model.setOwner(rs.getString(1));
				model.setTableName(rs.getString(2));
				//model.setTableSpaceName(rs.getString(3));
				//model.setStatus(rs.getString(4));
				model.setNumRows(rs.getLong(3));
				model.setBlocks(rs.getLong(4));
				//model.setEmptyBlocks(rs.getLong(7));
				model.setAvgRowLen(rs.getLong(5));
				//model.setDegree(rs.getString(9));
				model.setLastAnalyzed(DateUtils.parseDate(rs.getString(6)));
				//model.setPartitioned(rs.getString(11));
				model.setComment(rs.getString(7));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取数据字典信息");
		}catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取数据字典信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {

			}
		}
		return list;
	}


	public List<DictColumns> getDictColumnList() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select t.owner,t.table_name,t.column_name, ");
		sb.append("case when t.data_type in ('VARCHAR', 'VARCHAR2') then t.data_type || '(' || t.data_length || ')' ");
		sb.append("when t.data_type = 'NUMBER' and t.data_precision is null then t.data_type|| '(' || t.data_length || ')' ");
		sb.append("when t.data_type = 'NUMBER' and t.data_precision is not null then t.data_type||'('||t.data_precision ||','||t.data_scale||')'  ");
		sb.append("else t.data_type end, ");
		sb.append("t.data_length, ");
		sb.append("t.data_precision, ");
		sb.append("t.data_scale, ");
		sb.append("t.num_distinct, ");
		sb.append("t.num_buckets, ");
		sb.append("t.histogram, ");
		sb.append("t.nullable, ");
		sb.append("t.column_id, ");
		sb.append("c.comments ");
		sb.append("from dba_tab_columns t, dba_col_comments c ");
		sb.append("where t.table_name = c.table_name and t.column_name = c.column_name and t.owner = c.owner ");
		sb.append("and t.owner not in ('SYS', 'SYSTEM', 'WMSYS', 'DBSNMP', 'OUTLN', 'MONITOR', 'TSMSYS', 'PERFSTAT', 'XDB') ");
		sb.append("and t.table_name not like 'TMP%' ");
		List<DictColumns> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			logger.info("dic" + sb.toString());
			rs = sm.executeQuery(sb.toString());
			while (rs.next()) {
				DictColumns model = new DictColumns();
				model.setService(service);
				model.setOwner(rs.getString(1));
				model.setTableName(rs.getString(2));
				model.setColumnName(rs.getString(3));
				model.setDataType(rs.getString(4));
				model.setDataLength(rs.getInt(5));
				model.setDataPrecision(rs.getInt(6));
				model.setDataScale(rs.getInt(7));
				model.setNumDistinct(rs.getInt(8));
				model.setNumBuckets(rs.getInt(9));
				model.setHistogram(rs.getString(10));
				model.setNullable(rs.getString(11));
				model.setColumnId(rs.getInt(12));
				model.setComment(rs.getString(13));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取数据字典列信息");
		}catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取数据字典列信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {

			}
		}
		return list;
	}

	public List<DictIndexes> getDictIndexes() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select t.table_owner,t.index_name,t.index_type,t.table_name, ");
		sb.append("t.uniqueness,to_char(created, 'yyyy-mm-dd hh24:mi:ss') ");
		sb.append("from dba_indexes t, dba_objects o ");
		sb.append("where t.owner not in ('SYS', 'SYSTEM', 'WMSYS', 'DBSNMP', 'OUTLN', 'MONITOR', 'TSMSYS', 'PERFSTAT', 'XDB') ");
		sb.append("and t.index_name = o.object_name and o.object_type = 'INDEX' ");
		sb.append("and t.table_name not like 'TMP%' ");
		List<DictIndexes> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next()) {
				DictIndexes model = new DictIndexes();
				model.setService(service);
				model.setOwner(rs.getString(1));
				model.setIndexName(rs.getString(2));
				model.setIndexType(rs.getString(3));
				model.setTableName(rs.getString(4));
				model.setUniqueness(rs.getString(5));
				model.setGmtCreate(DateUtils.parseDate(rs.getString(6)));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取数据字典列信息");
		}catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取数据字典列信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {

			}
		}
		return list;
	}

	public List<DictIndCols> getDictIndCols() {
        Statement sm = null;
        ResultSet rs = null;
        String result = null;
        StringBuilder sb = new StringBuilder();
        sb.append("select t.index_owner,t.index_name,t.table_owner,t.table_name, ");
        sb.append("t.column_name,t.column_position,t.column_length,to_char(trunc(sysdate), 'yyyy-mm-dd hh24:mi:ss') ");
        sb.append("from dba_ind_columns t ");
        sb.append("where t.table_owner not in ('SYS', 'SYSTEM', 'WMSYS', 'DBSNMP', 'OUTLN', 'MONITOR', 'TSMSYS', 'PERFSTAT', 'XDB') ");
        List<DictIndCols> list = new ArrayList();
        try {
            sm = this.conn.createStatement();
            rs = sm.executeQuery(sb.toString());
            while (rs.next()) {
                DictIndCols model = new DictIndCols();
                model.setService(service);
                model.setIndexOwner(rs.getString(1));
                model.setIndexName(rs.getString(2));
                model.setTableOwner(rs.getString(3));
                model.setTableName(rs.getString(4));
                model.setColumnName(rs.getString(5));
                model.setColumnPosition(rs.getInt(6));
                model.setColumnLength(rs.getInt(7));
                model.setGmtCreate(DateUtils.parseDate(rs.getString(8)));
                list.add(model);
            }
            sm.close();
            rs.close();
            info();
            logger.info("获取数据字典列信息");
        }catch (SQLRecoverableException e) {
            this.disconnect = true;
            error();
            logger.error("数据库已断开");
        } catch (SQLException e) {
            error();
            logger.error("获取数据字典列信息");
            logger.error("exception:{}", e.getMessage(), e);

            try {
                if (sm != null) {
                    sm.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException localSQLException1) {

            }
        }
        return list;
    }

    public List<TableMon> getTableMon() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select table_owner,table_name,inss,upds,dels, ");
		sb.append("to_char(inss + upds + dels),to_char(sysdate, 'yyyy-mm-dd'),truncated,num_rows ");
		sb.append("from (select m.table_owner, m.table_name, inserts as inss, updates as upds, deletes as dels, truncated, t.num_rows ");
		sb.append("from sys.dba_tab_modifications m, dba_tables t ");
		sb.append("where m.table_name = t.table_name and t.table_name not like 'TMP%' ");
		sb.append("and t.owner not in ('MONITOR', 'ALIMMQRTZ', 'SYSTEM', 'DBSNMP', 'WMSYS', 'READONLY', 'TSMSYS', 'PERFSTAT') ");
		sb.append("and m.table_owner = t.owner and m.partition_name is null) ");
		List<TableMon> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next()) {
				TableMon model = new TableMon();
				model.setService(service);
				model.setOwner(rs.getString(1));
				model.setTableName(rs.getString(2));
				model.setInss(rs.getLong(3));
				model.setUpds(rs.getLong(4));
				model.setDels(rs.getLong(5));
				model.setTotalDmls(rs.getLong(6));
				model.setGmtCreate(DateUtils.parseDate(rs.getString(7)));
				model.setTruncated(rs.getString(8));
				model.setNumRows(rs.getLong(9));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取数据字典列信息");
		}catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取数据字典列信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {

			}
		}
		return list;
	}

	public List<Sequence> getSequence() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select sequence_owner,sequence_name,min_value, increment_by, ");
		sb.append("cache_size,last_number,to_char(sysdate, 'yyyy-mm-dd') ");
		sb.append("from dba_sequences s ");
		sb.append("where s.sequence_owner not in ('SYS', 'MONITOR', 'XDB', 'ALIMMQRTZ', 'SYSTEM', 'DBSNMP', 'WMSYS', 'READONLY', 'TSMSYS') ");
		List<Sequence> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next()) {
				Sequence model = new Sequence();
				model.setService(service);
				model.setOwner(rs.getString(1));
				model.setSequence(rs.getString(2));
				model.setMinValue(rs.getLong(3));
				//model.setMaxValue(rs.getLong(4));
				model.setIncrBy(rs.getInt(4));
				model.setCacheSize(rs.getInt(5));
				model.setLastNumber(rs.getInt(6));
				model.setCreateTime(DateUtils.parseDate(rs.getString(7)));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取数据字典列信息");
		}catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取数据字典列信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {

			}
		}
		return list;
	}

	public List<SqlSummary> getSqlSummary() {
		Statement sm = null;
		ResultSet rs = null;
		String result = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select s.sql_id,s.executions, ");
		sb.append("trunc(s.sorts/decode(s.executions,0,1,s.executions), 4), ");
		sb.append("trunc(s.disk_reads/decode(s.executions,0,1,s.executions), 4), ");
		sb.append("trunc(s.buffer_gets/decode(s.executions,0,1,s.executions), 4), ");
		sb.append("trunc(s.parse_calls/decode(s.executions,0,1,s.executions), 4), ");
		sb.append("trunc(s.cpu_time/1000000/decode(s.executions,0,1,s.executions), 4), ");
		sb.append("trunc(s.elapsed_time/1000000/decode(s.executions,0,1,s.executions), 4), ");
		sb.append("trunc(s.rows_processed/(decode(s.executions,0,1,s.executions)), 2), ");
		sb.append("s.parsing_schema_name,to_char(sysdate,'yyyy-mm-dd'), ");
		sb.append("trim(substr(substr(module,1,instr(module,'(')-1),instr(module,'@')+1)) as sqlsummary, s.sql_fulltext, ");
		sb.append("s.version_count, s.loaded_versions, s.fetches, s.application_wait_time, s.concurrency_wait_time, s.user_io_wait_time, s.plan_hash_value, s.elapsed_time, to_char(s.last_load_time,'yyyy-mm-dd'),s.module  ");
		sb.append("from v$sqlarea s ");
		sb.append("where s.last_active_time >= trunc(sysdate) - 1 + 4/24 ");
		sb.append("and s.parsing_schema_name not in ( 'SYS', 'MONITOR', 'SYSTEM', 'ORACLE_OCM', 'READONLY' ) ");
		sb.append("and (s.module not like 'PL/SQL%' and s.module <> 'plsqldev.exe' and s.module not like '%imp%') ");
		List<SqlSummary> list = new ArrayList();
		try {
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			System.out.println("sb" +sb.toString());
			while (rs.next()) {
				SqlSummary model = new SqlSummary();
				model.setService(service);
				model.setSqlId(rs.getString(1));
				model.setExecutions(rs.getLong(2));
				model.setSorts(rs.getFloat(3));
				model.setDiskReads(rs.getFloat(4));
				model.setBufferGets(rs.getFloat(5));
				model.setParseCalls(rs.getFloat(6));
				model.setCpuTime(rs.getFloat(7));
				model.setElapPer(rs.getFloat(8));
				model.setRowsProcessed(rs.getFloat(9));
				model.setSchemaName(rs.getString(10));
				model.setGmtCreate(DateUtils.parseDate(rs.getString(11)));
				model.setAppName(rs.getString(12));
				model.setSqlText(rs.getString(13));
				model.setVersionCount(rs.getInt(14));
				model.setLoadedVersions(rs.getInt(15));
				model.setFetches(rs.getLong(16));
				model.setAppWaitTime(rs.getFloat(17));
				model.setConWaitTime(rs.getFloat(18));
				model.setIoWaitTime(rs.getFloat(19));
				model.setPlanHashValue(rs.getLong(20));
				model.setElapsedTime(rs.getFloat(21));
				model.setLastActiveTime(DateUtils.parseDate(rs.getString(22)));
				model.setModule(rs.getString(23));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取数据字典列信息");
		}catch (SQLRecoverableException e) {
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		} catch (SQLException e) {
			error();
			logger.error("获取数据字典列信息");
			logger.error("exception:{}", e.getMessage(), e);

			try {
				if (sm != null) {
					sm.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException localSQLException1) {

			}
		}
		return list;
	}

	public List<SqlText> getSqlText()
	{
		Statement sm = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<SqlText> list = new ArrayList();
		sb.append("select s.sql_id,replace(s.sql_text,';',','),s.piece ");
		sb.append("from v$sqltext s, v$sqlarea a ");
		sb.append("where s.sql_id = a.sql_id and a.last_active_time >= trunc(sysdate) - 1 + 4/24 ");
		sb.append("and a.parsing_schema_name not in ( 'SYS', 'MONITOR', 'SYSTEM', 'ORACLE_OCM', 'READONLY' ) ");
		sb.append("and (a.module not like 'PL/SQL%' and a.module <> 'plsqldev.exe' and a.module not like '%imp%') ");
		sb.append("order by s.piece ");
		try
		{
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next())
			{
				SqlText model = new SqlText();
				model.setService(service);
				model.setSqlId(rs.getString(1));
				model.setSqlText(rs.getString(2));
				model.setPiece(rs.getInt(3));
				model.setSq1Type("SQLSUM");
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取sqlText信息");
		}
		catch (SQLRecoverableException e)
		{
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		}
		catch (SQLException e)
		{
			error();
			logger.error("获取sqlText信息错误");
			logger.error("exception:{}", e.getMessage(), e);

			try
			{
				if (sm != null)
				{
					sm.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException localSQLException1)
			{

			}
		}
		return list;
	}

	public List<SqlPlan> getSqlPlan()
	{
		Statement sm = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select a.sql_id,a.plan_hash_value,p.id, ");
		sb.append("lpad(' ',(depth-1)),p.operation,p.options,p.object_name,p.object_type, ");
		sb.append("decode(cardinality,null,' ', ");
		sb.append("decode(sign(cardinality-1000), -1, cardinality||' ',  ");
		sb.append("decode(sign(cardinality-1000000), -1, trunc(cardinality/1000)||'K', ");
		sb.append("decode(sign(cardinality-1000000000), -1, trunc(cardinality/1000000)||'M', ");
		sb.append("trunc(cardinality/1000000000)||'G')))), ");
		sb.append("decode(bytes,null,' ', ");
		sb.append("decode(sign(bytes-1024), -1, bytes||' ', ");
		sb.append("decode(sign(bytes-1048576), -1, trunc(bytes/1024)||'K', ");
		sb.append("decode(sign(bytes-1073741824), -1, trunc(bytes/1048576)||'M', ");
		sb.append("trunc(bytes/1073741824)||'G')))), ");
		sb.append("decode(cost,null,' ', ");
		sb.append("decode(sign(cost-10000000), -1, cost||' ', ");
		sb.append("decode(sign(cost-1000000000), -1, trunc(cost/1000000)||'M', ");
		sb.append("trunc(cost/1000000000)||'G'))), ");
		sb.append("decode(temp_space,null,' ', ");
		sb.append("decode(sign(temp_space-10000000), -1, trunc(temp_space/1000,2)||'K', ");
		sb.append("decode(sign(temp_space-1000000000), -1, trunc(temp_space/1000000)||'M', ");
		sb.append("trunc(temp_space/1000000000)||'G'))), 'SQLSUM' as sqlplan ");
		sb.append("from v$sqlarea a, (select t.* ");
		sb.append("from (select p.*, rank() over(partition by p.sql_id order by p.child_number desc) as rn ");
		sb.append("from v$sql_plan p) t ");
		sb.append("where rn = 1) p ");
		sb.append("where a.last_active_time >= trunc(sysdate) - 1 + 4/24 ");
		sb.append("and a.parsing_schema_name not in ( 'SYS', 'MONITOR', 'SYSTEM', 'ORACLE_OCM', 'READONLY' ) ");
		sb.append("and (a.module not like 'PL/SQL%' and a.module <> 'plsqldev.exe' and a.module not like '%imp%') ");
		sb.append("and a.sql_id = p.sql_id and a.plan_hash_value = p.plan_hash_value");
		List<SqlPlan> list = new ArrayList<SqlPlan>();
		try
		{
			sm = this.conn.createStatement();
			System.out.println("sql======= "+ sb.toString());
			rs = sm.executeQuery(sb.toString());
			while(rs.next())
			{
				SqlPlan model = new SqlPlan();
				model.setService(service);
				model.setSqlId(rs.getString(1));
				model.setPlanHashValue(rs.getLong(2));
				model.setPlanId(rs.getInt(3));
				//model.setDepth(rs.getInt(4));
				model.setOperation(rs.getString(5));
				model.setOptions(rs.getString(6));
				model.setObjectName(rs.getString(7));
				model.setObjectType(rs.getString(8));
				model.setCardinality(rs.getString(9));
				model.setBytes(rs.getString(10));
				model.setCost(rs.getString(11));
				model.setTemp_space(rs.getString(12));
				model.setPlanType(rs.getString(13));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取sqlText信息");
		}
		catch (SQLRecoverableException e)
		{
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		}
		catch (SQLException e)
		{
			error();
			logger.error("获取sqlPlan信息错误");
			logger.error("exception:{}", e.getMessage(), e);

			try
			{
				if (sm != null)
				{
					sm.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException localSQLException1)
			{

			}
		}
		return list;
	}

	public List<SqlMachine> getSqlMachine()
	{
		Statement sm = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<SqlMachine> list = new ArrayList();
		String sql = "select machine,count(*),to_char(sysdate,'yyyy-mm-dd')\n" +
						"  from v$session\n" +
						" group by machine";
		try
		{
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sql);
			while(rs.next())
			{
				SqlMachine model = new SqlMachine();
				model.setService(service);
				model.setMachine(rs.getString(1));
				model.setCnt(rs.getInt(2));
				model.setGmtCreate(DateUtils.parseDate(rs.getString(3)));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取sqlText信息");
		}
		catch (SQLRecoverableException e)
		{
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		}
		catch (SQLException e)
		{
			error();
			logger.error("获取sqlText信息错误");
			logger.error("exception:{}", e.getMessage(), e);

			try
			{
				if (sm != null)
				{
					sm.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException localSQLException1)
			{

			}
		}
		return list;
	}

	public List<SqlStat> getSqlStat()
	{
		Statement sm = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select s.snap_id,to_char(s.end_interval_time, 'yyyy-mm-dd hh24:mi'), ");
		sb.append("t.sql_id,t.version_count,t.executions_delta, ");
		sb.append("round(sorts_delta/(decode(executions_delta,0,1,executions_delta)), 4), ");
		sb.append("round(disk_reads_delta/(decode(executions_delta,0,1,executions_delta)), 4),  ");
		sb.append("round(buffer_gets_delta/(decode(executions_delta,0,1,executions_delta)), 4), ");
		sb.append("round(cpu_time_delta/(decode(executions_delta,0,1,executions_delta))/1000000, 4), ");
		sb.append("round(elapsed_time_delta/(decode(executions_delta,0,1,executions_delta))/1000000, 4), ");
		sb.append("round(parse_calls_delta/(decode(executions_delta,0,1,executions_delta)), 4), ");
		sb.append("round(rows_processed_delta/(decode(executions_delta,0,1,executions_delta)), 2), ");
		sb.append("parsing_schema_name,trim(substr(substr(module,1,instr(module,'(')-1),instr(module,'@')+1)) ");
		sb.append("from dba_hist_snapshot s, dba_hist_sqlstat t ");
		sb.append("where s.snap_id = t.snap_id and s.end_interval_time >= sysdate - INTERVAL '30' MINUTE ");
		sb.append("and (t.module not like 'PL/SQL%' and t.module <> 'plsqldev.exe') ");
		sb.append("and t.parsing_schema_name not in ('MONITOR') and t.executions_delta >= 0 ");
		List<SqlStat> list = new ArrayList<SqlStat>();
		try
		{
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			System.out.println("sql======= "+ sb.toString());
			while(rs.next())
			{
				SqlStat model = new SqlStat();
				model.setService(service);
        model.setSnapId(rs.getInt(1));
        model.setSnapTime(DateUtils.parseDate(rs.getString(2)));
        model.setSqlId(rs.getString(3));
        model.setVc(rs.getInt(4));
        model.setExecDelta(rs.getInt(5));
        model.setSortDelta(rs.getFloat(6));
        model.setDiskDelta(rs.getFloat(7));
        model.setBuffDelta(rs.getFloat(8));
        model.setCpuTime(rs.getFloat(9));
        model.setElapTime(rs.getFloat(10));
        model.setParseDelta(rs.getFloat(11));
        model.setRowsDelta(rs.getFloat(12));
        model.setSchemaName(rs.getString(13));
        model.setModule(rs.getString(14));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取sqlText信息");
		}
		catch (SQLRecoverableException e)
		{
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		}
		catch (SQLException e)
		{
			error();
			logger.error("获取sqlPlan信息错误");
			logger.error("exception:{}", e.getMessage(), e);

			try
			{
				if (sm != null)
				{
					sm.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException localSQLException1)
			{

			}
		}
		return list;
	}

	public List<SqlText> getSqlTextByStat()
	{
		Statement sm = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<SqlText> list = new ArrayList();
		sb.append("select f.sql_id,replace(f.sql_text,';',','),f.piece ");
		sb.append("from dba_hist_snapshot s, dba_hist_sqlstat t, v$sqltext f ");
		sb.append("where s.snap_id = t.snap_id and s.end_interval_time >= sysdate - INTERVAL '30' MINUTE ");
		sb.append("and (t.module not like 'PL/SQL%' and t.module <> 'plsqldev.exe') ");
		sb.append("and t.sql_id = f.sql_id and t.executions_delta >= 0 ");
		sb.append("and t.parsing_schema_name not in ('MONITOR') ");
		try
		{
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next())
			{
				SqlText model = new SqlText();
				model.setService(service);
				model.setSqlId(rs.getString(1));
				model.setSqlText(rs.getString(2));
				model.setPiece(rs.getInt(3));
				model.setSq1Type("SQLSNAP");
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取sqlText信息");
		}
		catch (SQLRecoverableException e)
		{
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		}
		catch (SQLException e)
		{
			error();
			logger.error("获取sqlText信息错误");
			logger.error("exception:{}", e.getMessage(), e);

			try
			{
				if (sm != null)
				{
					sm.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException localSQLException1)
			{

			}
		}
		return list;
	}

	public List<SqlPlan> getSqlPlanByStat ()
	{
		Statement sm = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select t.sql_id,t.plan_hash_value,p.id, ");
		sb.append("lpad(' ',(depth-1)),p.operation,p.options,p.object_name, ");
		sb.append("decode(cardinality,null,'  ', ");
		sb.append("decode(sign(cardinality-1000), -1, cardinality||' ',  ");
		sb.append("decode(sign(cardinality-1000000), -1, trunc(cardinality/1000)||'K', ");
		sb.append("decode(sign(cardinality-1000000000), -1, trunc(cardinality/1000000)||'M', ");
		sb.append("trunc(cardinality/1000000000)||'G')))), ");
		sb.append("decode(bytes,null,' ', ");
		sb.append("decode(sign(bytes-1024), -1, bytes||' ', ");
		sb.append("decode(sign(bytes-1048576), -1, trunc(bytes/1024)||'K', ");
		sb.append("decode(sign(bytes-1073741824), -1, trunc(bytes/1048576)||'M', ");
		sb.append("trunc(bytes/1073741824)||'G')))), ");
		sb.append("decode(cost,null,' ', ");
		sb.append("decode(sign(cost-10000000), -1, cost||' ', ");
		sb.append("decode(sign(cost-1000000000), -1, trunc(cost/1000000)||'M', ");
		sb.append("trunc(cost/1000000000)||'G'))), ");
		sb.append("decode(temp_space,null,' ', ");
		sb.append("decode(sign(temp_space-10000000), -1, trunc(temp_space/1000,2)||'K', ");
		sb.append("decode(sign(temp_space-1000000000), -1, trunc(temp_space/1000000)||'M', ");
		sb.append("trunc(temp_space/1000000000)||'G'))),';SQLSNAP' as sql_plan ");
		sb.append("from dba_hist_snapshot s, dba_hist_sqlstat t, dba_hist_sql_plan p ");
		sb.append("where s.snap_id = t.snap_id and s.end_interval_time >= sysdate - INTERVAL '30' MINUTE ");
		sb.append("and (t.module not like 'PL/SQL%' and t.module <> 'plsqldev.exe') ");
		sb.append("and t.sql_id = p.sql_id and t.plan_hash_value = p.plan_hash_value ");
		sb.append("and t.parsing_schema_name not in ('MONITOR') and t.executions_delta >= 0 ");
		sb.append("and p.timestamp = (select max(timestamp) ");
		sb.append("from dba_hist_sql_plan sp ");
		sb.append("where sp.sql_id = t.sql_id and p.plan_hash_value = sp.plan_hash_value)");
		List<SqlPlan> list = new ArrayList<SqlPlan>();
		try
		{
			sm = this.conn.createStatement();
			System.out.println("sql======= "+ sb.toString());
			rs = sm.executeQuery(sb.toString());
			while(rs.next())
			{
				SqlPlan model = new SqlPlan();
				model.setService(service);
				model.setSqlId(rs.getString(1));
				model.setPlanHashValue(rs.getLong(2));
				model.setPlanId(rs.getInt(3));
				//model.setDepth(rs.getInt(4));
				model.setOperation(rs.getString(5));
				model.setOptions(rs.getString(6));
				model.setObjectName(rs.getString(7));
				model.setCardinality(rs.getString(8));
				model.setBytes(rs.getString(9));
				model.setCost(rs.getString(10));
				model.setTemp_space(rs.getString(11));
				model.setPlanType(rs.getString(12));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取sqlText信息");
		}
		catch (SQLRecoverableException e)
		{
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		}
		catch (SQLException e)
		{
			error();
			logger.error("获取sqlPlan信息错误");
			logger.error("exception:{}", e.getMessage(), e);

			try
			{
				if (sm != null)
				{
					sm.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException localSQLException1)
			{

			}
		}
		return list;
	}

	public List<SqlObject> getSqlObjectByStat()
	{
		Statement sm = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<SqlObject> list = new ArrayList();
		sb.append("select object_name,object_type,'SQLSNAP' ");
		sb.append("from dba_objects ");
		sb.append("where last_ddl_time >= trunc(sysdate) ");
		try
		{
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next())
			{
				SqlObject model = new SqlObject();
				model.setService(service);
				model.setObjectName(rs.getString(1));
				model.setObjectType(rs.getString(2));
				model.setPlanType(rs.getString(3));;
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取sqlObject信息");
		}
		catch (SQLRecoverableException e)
		{
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		}
		catch (SQLException e)
		{
			error();
			logger.error("获取sqlObject信息错误");
			logger.error("exception:{}", e.getMessage(), e);

			try
			{
				if (sm != null)
				{
					sm.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException localSQLException1)
			{

			}
		}
		return list;
	}

	public List<SqlSession> getSqlSessionByStat()
	{
		Statement sm = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<SqlSession> list = new ArrayList();
		sb.append("select machine,count(*),to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') ");
		sb.append("from v$session ");
		sb.append("group by machine ");
		try
		{
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next())
			{
				SqlSession model = new SqlSession();
				model.setService(service);
				model.setMachine(rs.getString(1));
				model.setCnt(rs.getInt(2));
				model.setGmtCreate(DateUtils.parseDate(rs.getString(3)));;
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取sqlObject信息");
		}
		catch (SQLRecoverableException e)
		{
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		}
		catch (SQLException e)
		{
			error();
			logger.error("获取sqlObject信息错误");
			logger.error("exception:{}", e.getMessage(), e);

			try
			{
				if (sm != null)
				{
					sm.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException localSQLException1)
			{

			}
		}
		return list;
	}

	public List<SysAshSum> getSysAwaitSum()
	{
		Statement sm = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<SysAshSum> list = new ArrayList();
		sb.append("select t.wait_class,substr(t.event,0,32),a.owner,a.object_name,a.object_type, ");
		sb.append("t.cnt,s.snap_id,to_char(trunc(s.end_interval_time,'mi'), 'yyyy-mm-dd hh24:mi:ss') ");
		sb.append("from ( select wait_class, event, current_obj#, count(*) as cnt ");
		sb.append("from v$active_session_history ");
		sb.append("where wait_class is not null ");
		sb.append("group by wait_class, event, current_obj# ");
		sb.append("having count(*) > 1) t, dba_objects a, dba_hist_snapshot s ");
		sb.append("where t.current_obj# = a.object_id(+) ");
		sb.append("and s.end_interval_time > sysdate - INTERVAL '30' MINUTE");
		try
		{
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next())
			{
				SysAshSum model = new SysAshSum();
				model.setService(service);
				model.setWaitClass(rs.getString(1));
				model.setEvent(rs.getString(2));
				model.setOwner(rs.getString(3));
				model.setObjectName(rs.getString(4));
				model.setObjectType(rs.getString(5));
				model.setTotalWaits(rs.getLong(6));
				model.setSnapId(rs.getInt(7));
				model.setSnapTime(DateUtils.parseDate(rs.getString(9)));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取sqlObject信息");
		}
		catch (SQLRecoverableException e)
		{
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		}
		catch (SQLException e)
		{
			error();
			logger.error("获取sqlObject信息错误");
			logger.error("exception:{}", e.getMessage(), e);

			try
			{
				if (sm != null)
				{
					sm.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException localSQLException1)
			{

			}
		}
		return list;
	}

	public List<SysMetric> getSysMetric()
	{
		Statement sm = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		List<SysMetric> list = new ArrayList();
		sb.append("select s.snap_id,to_char(s.end_interval_time, 'yyyy-mm-dd hh24:mi'),t.metric_id, ");
		sb.append("t.metric_name,trunc(t.average,2),trunc(t.maxval,2),trunc(t.standard_deviation,2) ");
		sb.append("from dba_hist_snapshot s, dba_hist_sysmetric_summary t ");
		sb.append("where s.snap_id = t.snap_id and s.end_interval_time >= sysdate - INTERVAL '30' MINUTE ");
		sb.append("and length(t.metric_name) < 40 ");
		try
		{
			sm = this.conn.createStatement();
			rs = sm.executeQuery(sb.toString());
			while (rs.next())
			{
				SysMetric model = new SysMetric();
				model.setService(service);
				model.setSnapId(rs.getInt(1));
				model.setGmtCreate(DateUtils.parseDate(rs.getString(2)));
				model.setMetricId(rs.getInt(3));
				model.setMetricName(rs.getString(4));
				model.setAvg(rs.getDouble(5));
				model.setMax(rs.getDouble(6));
				model.setSdval(rs.getDouble(7));
				list.add(model);
			}
			sm.close();
			rs.close();
			info();
			logger.info("获取sqlObject信息");
		}
		catch (SQLRecoverableException e)
		{
			this.disconnect = true;
			error();
			logger.error("数据库已断开");
		}
		catch (SQLException e)
		{
			error();
			logger.error("获取sqlObject信息错误");
			logger.error("exception:{}", e.getMessage(), e);

			try
			{
				if (sm != null)
				{
					sm.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (SQLException localSQLException1)
			{

			}
		}
		return list;
	}

	public Connection getConn() {
		return this.conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public boolean isDisconnect() {
		return this.disconnect;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	private void info() {
		logger.info("[service=" + getService()+"]");
	}

	private void error() {
		logger.error("[service=" + getService()+"]");
	}
}

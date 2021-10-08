package com.mingheinfo.maxdata.core.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SqlSummaryRelationModel implements Serializable
{
	 private static final long serialVersionUID = 1L;

	    private String service;
	    private String sqlId;
	    private String sqlText;
	    private Float sorts;
	    private Integer versionCount;
	    private Integer loadedVersions;
	    private Long fetches;
	    private Long executions;
	    private Float parseCalls;
	    private Float diskReads;
	    private Float bufferGets;
	    private Float appWaitTime;
	    private Float conWaitTime;
	    private Float ioWaitTime;
	    private Float rowsProcessed;
	    private String schemaName;
	    private Long planHashValue;
	    private Float cpuTime;
	    
	    @JsonFormat(pattern = "yyyy-MM-dd")
	    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")        
	    private Date lastActiveTime;
	    //创建时间
	    @JsonFormat(pattern = "yyyy-MM-dd")
	    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	    @TableField(fill = FieldFill.INSERT)
	    private Date gmtCreate;
	    
	    private Long execDelta;
	    private String module;
	    private String scanType;
	    private Float execPer;
	    private Float diskPer;
	    private Float buffPer;
	    private Float elapPer;
	    private Float cputPer;
	    private Float sortPer;
	    private Float parsePer;
	    private String appName;
	    private String curve;
	    private Float elapsedTime;
	    private String objectName;
	    private String plan_line;

	    public Date getGmtCreate() 
	    {
	        return gmtCreate;
	    }

	    public void setGmtCreate(Date gmtCreate) 
	    {
	        this.gmtCreate = gmtCreate;
	    }

	    public String getService() 
	    {
	        return service;
	    }

	    public void setService(String service) 
	    {
	        this.service = service;
	    }

	    public String getSqlId() 
	    {
	        return sqlId;
	    }

	    public void setSqlId(String sqlId) 
	    {
	        this.sqlId = sqlId;
	    }

	    public String getSqlText() 
	    {
	        return sqlText;
	    }

	    public void setSqlText(String sqlText) 
	    {
	        this.sqlText = sqlText;
	    }

	    public Float getSorts() 
	    {
	        return sorts;
	    }

	    public void setSorts(Float sorts) 
	    {
	        this.sorts = sorts;
	    }

	    public Integer getVersionCount() 
	    {
	        return versionCount;
	    }

	    public void setVersionCount(Integer versionCount) 
	    {
	        this.versionCount = versionCount;
	    }

	    public Integer getLoadedVersions() 
	    {
	        return loadedVersions;
	    }

	    public void setLoadedVersions(Integer loadedVersions) 
	    {
	        this.loadedVersions = loadedVersions;
	    }

	    public Long getFetches() 
	    {
	        return fetches;
	    }

	    public void setFetches(Long fetches) 
	    {
	        this.fetches = fetches;
	    }

	    public Long getExecutions() 
	    {
	        return executions;
	    }

	    public void setExecutions(Long executions) 
	    {
	        this.executions = executions;
	    }

	    public Float getParseCalls() 
	    {
	        return parseCalls;
	    }

	    public void setParseCalls(Float parseCalls) 
	    {
	        this.parseCalls = parseCalls;
	    }

	    public Float getDiskReads() 
	    {
	        return diskReads;
	    }

	    public void setDiskReads(Float diskReads) 
	    {
	        this.diskReads = diskReads;
	    }

	    public Float getBufferGets() 
	    {
	        return bufferGets;
	    }

	    public void setBufferGets(Float bufferGets)
	    {
	        this.bufferGets = bufferGets;
	    }

	    public Float getAppWaitTime() 
	    {
	        return appWaitTime;
	    }

	    public void setAppWaitTime(Float appWaitTime)
	    {
	        this.appWaitTime = appWaitTime;
	    }

	    public Float getConWaitTime() 
	    {
	        return conWaitTime;
	    }

	    public void setConWaitTime(Float conWaitTime) 
	    {
	        this.conWaitTime = conWaitTime;
	    }

	    public Float getIoWaitTime() 
	    {
	        return ioWaitTime;
	    }

	    public void setIoWaitTime(Float ioWaitTime) 
	    {
	        this.ioWaitTime = ioWaitTime;
	    }

	    public Float getRowsProcessed() 
	    {
	        return rowsProcessed;
	    }

	    public void setRowsProcessed(Float rowsProcessed)
	    {
	        this.rowsProcessed = rowsProcessed;
	    }

	    public String getSchemaName() 
	    {
	        return schemaName;
	    }

	    public void setSchemaName(String schemaName) 
	    {
	        this.schemaName = schemaName;
	    }

	    public Long getPlanHashValue() 
	    {
	        return planHashValue;
	    }

	    public void setPlanHashValue(Long planHashValue) 
	    {
	        this.planHashValue = planHashValue;
	    }

	    public Float getCpuTime() 
	    {
	        return cpuTime;
	    }

	    public void setCpuTime(Float cpuTime) 
	    {
	        this.cpuTime = cpuTime;
	    }

	    public Date getLastActiveTime() 
	    {
	        return lastActiveTime;
	    }

	    public void setLastActiveTime(Date lastActiveTime) 
	    {
	        this.lastActiveTime = lastActiveTime;
	    }

	    public Long getExecDelta() 
	    {
	        return execDelta;
	    }

	    public void setExecDelta(Long execDelta)
	    {
	        this.execDelta = execDelta;
	    }

	    public String getModule() 
	    {
	        return module;
	    }

	    public void setModule(String module) 
	    {
	        this.module = module;
	    }

	    public String getScanType() 
	    {
	        return scanType;
	    }

	    public void setScanType(String scanType) 
	    {
	        this.scanType = scanType;
	    }

	    public Float getExecPer() 
	    {
	        return execPer;
	    }

	    public void setExecPer(Float execPer)
	    {
	        this.execPer = execPer;
	    }

	    public Float getDiskPer()
	    {
	        return diskPer;
	    }

	    public void setDiskPer(Float diskPer)
	    {
	        this.diskPer = diskPer;
	    }

	    public Float getBuffPer()
	    {
	        return buffPer;
	    }

	    public void setBuffPer(Float buffPer)
	    {
	        this.buffPer = buffPer;
	    }

	    public Float getElapPer()
	    {
	        return elapPer;
	    }

	    public void setElapPer(Float elapPer)
	    {
	        this.elapPer = elapPer;
	    }

	    public Float getCputPer() 
	    {
	        return cputPer;
	    }

	    public void setCputPer(Float cputPer) 
	    {
	        this.cputPer = cputPer;
	    }

	    public Float getSortPer() 
	    {
	        return sortPer;
	    }

	    public void setSortPer(Float sortPer)
	    {
	        this.sortPer = sortPer;
	    }

	    public Float getParsePer() 
	    {
	        return parsePer;
	    }

	    public void setParsePer(Float parsePer) 
	    {
	        this.parsePer = parsePer;
	    }

	    public String getAppName() 
	    {
	        return appName;
	    }

	    public void setAppName(String appName)
	    {
	        this.appName = appName;
	    }

	    public String getCurve() 
	    {
	        return curve;
	    }

	    public void setCurve(String curve)
	    {
	        this.curve = curve;
	    }
  
}

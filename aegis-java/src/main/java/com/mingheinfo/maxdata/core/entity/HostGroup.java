package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mingheinfo.common.constant.CommonConstant;
import com.mingheinfo.maxdata.base.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//主机组
@Data
@TableName("host_group")
public class HostGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //名称
    private String name;
    
    private Integer type;
    
    private String description;
    //阈值自定义
    private String threshold;
    
    private String state;

    private Integer memberCnt;
    private String cpusAvg;
    private String cpuuAvg;
    private String cpuwAvg;
    private String swapAvg;
    private String memuAvg;
    private String memfAvg;
    private String netiAvg;
    private String netoAvg;
    private String hostNames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getMemberCnt() {
        return memberCnt;
    }

    public void setMemberCnt(Integer memberCnt) {
        this.memberCnt = memberCnt;
    }

    public String getCpusAvg() {
        return cpusAvg;
    }

    public void setCpusAvg(String cpusAvg) {
        this.cpusAvg = cpusAvg;
    }

    public String getCpuuAvg() {
        return cpuuAvg;
    }

    public void setCpuuAvg(String cpuuAvg) {
        this.cpuuAvg = cpuuAvg;
    }

    public String getCpuwAvg() {
        return cpuwAvg;
    }

    public void setCpuwAvg(String cpuwAvg) {
        this.cpuwAvg = cpuwAvg;
    }

    public String getSwapAvg() {
        return swapAvg;
    }

    public void setSwapAvg(String swapAvg) {
        this.swapAvg = swapAvg;
    }

    public String getMemuAvg() {
        return memuAvg;
    }

    public void setMemuAvg(String memuAvg) {
        this.memuAvg = memuAvg;
    }

    public String getMemfAvg() {
        return memfAvg;
    }

    public void setMemfAvg(String memfAvg) {
        this.memfAvg = memfAvg;
    }

    public String getNetiAvg() {
        return netiAvg;
    }

    public void setNetiAvg(String netiAvg) {
        this.netiAvg = netiAvg;
    }

    public String getNetoAvg() {
        return netoAvg;
    }

    public void setNetoAvg(String netoAvg) {
        this.netoAvg = netoAvg;
    }

    public String getHostNames() {
        return hostNames;
    }

    public void setHostNames(String hostName) {
        this.hostNames = hostName;
    }
}
package com.mingheinfo.maxdata.core.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 对应数据字典报表
 *
 * */
@Data
@TableName("dict_tables")
public class DictReport implements Serializable {

    private static final long serialVersionUID = 1L;
    private String owner;
    private String tableName;
    private String tableSpaceName;
    private String status;
    private Long numRows;
    private Long blocks;
    private Long emptyBlocks;
    private Long avgRowLen;
    private String degree;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date lastAnalyzed;
    private String partitioned;
    private String comment;
    private String service;
}

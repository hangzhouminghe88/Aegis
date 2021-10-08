package com.mingheinfo.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class OtherSetting implements Serializable{

    @ApiModelProperty(value = "domain")
    private String domain;
}

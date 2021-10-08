package com.mingheinfo.maxdata.common.controller;

import cn.hutool.http.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mingheinfo.common.json.ApiJson;

import java.util.HashMap;
import java.util.Map;


//Security相关接口
@Slf4j
@RestController
@RequestMapping("/api/common")
@Transactional
public class SecurityController {

    @RequestMapping(value = "/needLogin", method = RequestMethod.GET)
    @ApiOperation(value = "没有登录")
    public ApiJson needLogin(){
    	ApiJson ajaxJson = new ApiJson();
    	
    	ajaxJson.setError("401", "您还未登录");
    	
        return ajaxJson;
    }

}

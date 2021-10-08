package com.mingheinfo.maxdata.common.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.IpInfoUtil;
import com.mingheinfo.common.utils.ResultUtil;

import javax.servlet.http.HttpServletRequest;

//IP接口
@Slf4j
@RestController
@RequestMapping("/api/common/ip")
@Transactional
public class IpInfoController {

    @Autowired
    private IpInfoUtil ipInfoUtil;

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    @ApiOperation(value = "IP及天气相关信息")
    public ApiJson upload(HttpServletRequest request) {

        String result= ipInfoUtil.getIpCity(request);
        return ResultUtil.data(result);
    }
}
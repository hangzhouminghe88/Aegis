package com.mingheinfo.maxdata.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.CreateVerifyCode;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码接口
 */
@RequestMapping("/api/common/captcha")
@RestController
@CrossOrigin
@Transactional
public class CaptchaController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //初始化验证码
    @RequestMapping(value = "/init",method = RequestMethod.GET)
    public ApiJson initCaptcha() {
    	ApiJson ajaxJson = new ApiJson();
    	
        String captchaId = UUID.randomUUID().toString().replace("-","");
        String code = new CreateVerifyCode().randomStr(4);
        // 缓存验证码
        redisTemplate.opsForValue().set(captchaId, code,2L, TimeUnit.MINUTES);
        
        ajaxJson.setResult(captchaId);
        
        return ajaxJson;
    }

    //根据验证码ID获取图片
    @RequestMapping(value = "/draw/{captchaId}", method = RequestMethod.GET)
    public void drawCaptcha(@PathVariable("captchaId") String captchaId, HttpServletResponse response) throws IOException {

        //得到验证码 生成指定验证码
        String code=redisTemplate.opsForValue().get(captchaId);
        CreateVerifyCode vCode = new CreateVerifyCode(116,36,4,10,code);
        response.setContentType("image/png");
        vCode.write(response.getOutputStream());
    }
}

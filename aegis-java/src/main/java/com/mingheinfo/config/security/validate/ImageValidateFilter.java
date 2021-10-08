package com.mingheinfo.config.security.validate;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mingheinfo.common.utils.ResponseUtils;
import com.mingheinfo.config.properties.CaptchaProperties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 图形验证码过滤器
 * 
 */
@Slf4j
@Configuration
public class ImageValidateFilter extends OncePerRequestFilter {

    @Autowired
    private CaptchaProperties captchaProperties;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // 判断URL是否需要验证
        Boolean flag = false;
        String requestUrl = request.getRequestURI();
        PathMatcher pathMatcher = new AntPathMatcher();
        response.setCharacterEncoding("UTF-8");
        for(String url : captchaProperties.getImage()){
            if(pathMatcher.match(url, requestUrl)){
                flag = true;
                break;
            }
        }
        if(flag){
            String captchaId = request.getParameter("captchaId");
            String code = request.getParameter("code");
            if(StrUtil.isBlank(captchaId)||StrUtil.isBlank(code)){
                ResponseUtils.out(response, ResponseUtils.resultMap(false,500,"请传入图形验证码所需参数captchaId或code"));
                return;
            }
            String redisCode = redisTemplate.opsForValue().get(captchaId);
            if(StrUtil.isBlank(redisCode)){
                ResponseUtils.out(response, ResponseUtils.resultMap(false,500,"验证码已过期，请重新获取"));
                return;
            }

            if(!redisCode.toLowerCase().equals(code.toLowerCase())) {
                log.info("验证码错误：code:"+ code +"，redisCode:"+redisCode);
                ResponseUtils.out(response, ResponseUtils.resultMap(false,500,"图形验证码输入错误"));
                return;
            }
            // 已验证清除key
            redisTemplate.delete(captchaId);
            // 验证成功 放行
            chain.doFilter(request, response);
            return;
        }
        // 无需验证 放行
        chain.doFilter(request, response);
    }
}

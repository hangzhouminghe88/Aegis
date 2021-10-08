package com.mingheinfo.config.security.permission;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mingheinfo.common.utils.LicenseUtils;
import com.mingheinfo.common.utils.ResponseUtils;
import com.mingheinfo.config.properties.IgnoredUrlsProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class LicenseFilter  extends OncePerRequestFilter
{
    @Autowired
    private IgnoredUrlsProperties ignoredUrlsProperties;
    
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
		  // 判断URL是否需要验证
        Boolean flag = true;
        String requestUrl = request.getRequestURI();
        PathMatcher pathMatcher = new AntPathMatcher();
        response.setCharacterEncoding("UTF-8");
        // 除配置文件忽略路径其它所有请求都需经过认证和授权
        for(String url:ignoredUrlsProperties.getUrls()){
        	//除忽略鉴权与获得菜单、用户信息之外的接口都需进行license校验
        	if(pathMatcher.match(url, requestUrl) || pathMatcher.match("/api/permission/getMenuList", requestUrl) || pathMatcher.match("/api/user/info", requestUrl)){
                flag = false;
                break;
            }
        }
        if(flag){
        	//校验license是否过期，过期给予提示
        	if(LicenseUtils.isExpired()) 
        	{
        		ResponseUtils.out(response, ResponseUtils.resultMap(false,500,"license已过期，如需继续添加实例请联系管理员!"));
        		return;
        	}
        	//校验license是否达到最大实例数
        	if(LicenseUtils.isLimitMax())
        	{
        		chain.doFilter(request, response);
        		return;
        	}
            // 验证成功 放行
            chain.doFilter(request, response);
            return;
        }
        // 无需验证 放行
        chain.doFilter(request, response);
	}
}

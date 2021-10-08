package com.mingheinfo.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {

		// 默认地址（可以是页面或后台请求接口）
		registry.addViewController("/aegis/**").setViewName("forward:/index.html");
		// 设置过滤优先级最高
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/tmp/aegis/adm/**").addResourceLocations("file:/tmp/aegis/adm/");
		registry.addResourceHandler("/tmp/aegis/awr/**").addResourceLocations("file:/tmp/aegis/awr/");
	}
}

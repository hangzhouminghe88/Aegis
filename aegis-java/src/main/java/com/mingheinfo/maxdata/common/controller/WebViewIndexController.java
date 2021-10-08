package com.mingheinfo.maxdata.common.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/aegis")
public class WebViewIndexController {
	@RequestMapping("/**")
	public void index(HttpServletResponse response) throws IOException {
		response.sendRedirect("classpath:/static/index.html");
	}
}

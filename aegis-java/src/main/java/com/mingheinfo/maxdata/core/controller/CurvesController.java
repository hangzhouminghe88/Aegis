package com.mingheinfo.maxdata.core.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.DateUtils;
import com.mingheinfo.common.utils.ResultUtil;
import com.mingheinfo.common.vo.PageVo;
import com.mingheinfo.maxdata.core.entity.Metrics;
import com.mingheinfo.maxdata.core.entity.Template;
import com.mingheinfo.maxdata.core.entity.TemplatesAlerts;
import com.mingheinfo.maxdata.core.entity.TemplatesHistory;
import com.mingheinfo.maxdata.core.service.TemplateAlertsService;
import com.mingheinfo.maxdata.core.service.TemplatesHistoryService;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/curv")
@Transactional
public class CurvesController {
	public static org.slf4j.Logger logger = LoggerFactory.getLogger(CurvesController.class);
	@Autowired
	private TemplatesHistoryService templatesHistoryService;

	@Autowired
	private TemplateAlertsService templateAlertsService;

	// 分页获取
	@RequestMapping(value = "/dispbyh", method = RequestMethod.GET)
	public ApiJson getDispbyh(PageVo pageVo, String service, int interval, String search, String tags, String type, String name, String sname, String templateType)
			throws Exception {
		IPage page = new Page(pageVo.getPageNumber(), pageVo.getPageSize());
		Map<String, List> map = new HashMap<>();
		switch (interval) {
		case 300:
			map = getMetricResult(service, tags, interval, "minute", name, sname, templateType);
			break;
		case 3600:
			map = getMetricResult(service, tags, interval, "hour", name, sname, templateType);
			break;
		case 24 * 3600:
			map = getMetricResult(service, tags, interval, "day", name, sname, templateType);
			break;
		case 7 * 24 * 3600:
			map = getMetricResult(service, tags, interval, "week", name, sname, templateType);
			break;
		case 30 * 24 * 3600:
			map = getMetricResult(service, tags, interval, "month", name, sname, templateType);
			break;
		case 365 * 24 * 3600:
			map = getMetricResult(service, tags, interval, "year", name, sname, templateType);
			break;
		}
		List<Map<String, List>> newList = new ArrayList<>();
		newList.add(map);
		return ResultUtil.data(newList);
	}

	// 获取deital数据
	// 分页获取
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public ApiJson getDispbyhDetail(PageVo pageVo, String service, String sname, String name) {
		IPage<TemplatesHistory> page = new Page<TemplatesHistory>(pageVo.getPageNumber(), pageVo.getPageSize());
		QueryWrapper<TemplatesHistory> queryWrapper = new QueryWrapper<TemplatesHistory>();
		if (StringUtils.isNotBlank(service)) {
			queryWrapper.eq("service", service);
		}
		if (StringUtils.isNotBlank(sname)) {
			queryWrapper.eq("sname", sname);
		}
		if (StringUtils.isNotBlank(name)) 
		{
			queryWrapper.eq("name", name);
		}
		String gmtModified = DateUtils.getDate7Day();
		queryWrapper.gt("gmt_modified", gmtModified);
		if (StringUtils.isNotBlank(pageVo.getSort()) && StringUtils.isNotBlank(pageVo.getOrder())) {
			if (pageVo.getOrder().toLowerCase().equals("asc")) {
				queryWrapper.orderByAsc(pageVo.getSort());
			} else {
				queryWrapper.orderByDesc(pageVo.getSort());
			}
		}
		page = templatesHistoryService.page(page, queryWrapper);
		return ResultUtil.data(page);
	}

	// 获取deital数据
	// 分页获取
	@RequestMapping(value = "/trend", method = RequestMethod.GET)
	public ApiJson getDispbyhTrendDetail(PageVo pageVo, int interval, String hostIds, String search, String tags)
			throws Exception {
		IPage page = new Page(pageVo.getPageNumber(), pageVo.getPageSize());
		logger.info("=========" + hostIds);
		List<Template> list = templatesHistoryService.queryTemplateNameToTrend("", pageVo.getPageNumber(),
				pageVo.getPageSize(), tags, search, hostIds);
		Long total = templatesHistoryService.queryTemplateNameToTrendCount("", tags, search, hostIds);
		List<String> serviceList = templatesHistoryService.queryServiceByName();
		Map<String, List> map = new HashMap<>();
		switch (interval) {
		case 300:
			map = getTrendResult(tags, list, interval, hostIds, "minute", serviceList);
			break;
		case 3600:
			map = getTrendResult(tags, list, interval, hostIds, "hour", serviceList);
			break;
		case 24 * 3600:
			map = getTrendResult(tags, list, interval, hostIds, "day", serviceList);
			break;
		case 7 * 24 * 3600:
			map = getTrendResult(tags, list, interval, hostIds, "week", serviceList);
			break;
		case 30 * 24 * 3600:
			map = getTrendResult(tags, list, interval, hostIds, "month", serviceList);
			break;
		case 365 * 24 * 3600:
			map = getTrendResult(tags, list, interval, hostIds, "year", serviceList);
			break;
		}
		List<Map<String, List>> newList = new ArrayList<>();
		newList.add(map);
		page.setRecords(newList);
		page.setTotal(total);
		return ResultUtil.data(page);
	}

	// 获取deital数据
	// 分页获取
	@RequestMapping(value = "/alert", method = RequestMethod.GET)
	public ApiJson getThrendLog(PageVo pageVo, String service, String name) {
		List list = templateAlertsService.queryTrendLog(service, DateUtils.getDate(), pageVo.getPageNumber() - 1,
				pageVo.getPageSize(), name);
		Integer total = templateAlertsService.queryTrendLogCount(service, DateUtils.getDate(), name);
		Map<String, Object> map = new HashMap<>();
		map.put("total", total);
		map.put("records", list);
		map.put("size", pageVo.getPageSize());
		return ResultUtil.data(map);
	}

	// 获取deital数据
	// 分页获取
	@RequestMapping(value = "/alerthis", method = RequestMethod.GET)
	public ApiJson getThrendLogHis(PageVo pageVo, String service, String name) {
		IPage<TemplatesAlerts> page = new Page<TemplatesAlerts>(pageVo.getPageNumber(), pageVo.getPageSize());
		QueryWrapper<TemplatesAlerts> queryWrapper = new QueryWrapper<TemplatesAlerts>();
		if (StringUtils.isNotBlank(service)) {
			queryWrapper.eq("service", service);
		}
		if (StringUtils.isNotBlank(name)) {
			queryWrapper.eq("name", name);
		}
		queryWrapper.apply(" gmt_create >= curdate()");
		if (StringUtils.isNotBlank(pageVo.getSort()) && StringUtils.isNotBlank(pageVo.getOrder())) {
			if (pageVo.getOrder().toLowerCase().equals("asc")) {
				queryWrapper.orderByAsc(pageVo.getSort());
			} else {
				queryWrapper.orderByDesc(pageVo.getSort());
			}
		}
		page = templateAlertsService.page(page, queryWrapper);
		return ResultUtil.data(page);
	}

	// 分页获取
	@RequestMapping(value = "/dispbyhdetail", method = RequestMethod.GET)
	public ApiJson getDispbyhDetail(PageVo pageVo, String service, int interval, String search, String tags,
			String type) throws Exception {
		IPage page = new Page(pageVo.getPageNumber(), pageVo.getPageSize());
		List<Template> list = templatesHistoryService.queryTemplateName(service, pageVo.getPageNumber(),
				pageVo.getPageSize(), tags, search, type);
		Long total = templatesHistoryService.queryTemplateNameCount(service, tags, search, type);
		Map<String, List> map = new HashMap<>();
		List<String> serviceList = templatesHistoryService.queryServiceByName();
		switch (interval) {
		case 300:
			map = getMetricDetailResult(tags, interval, "minute", serviceList, list);
			break;
		case 3600:
			map = getMetricDetailResult(tags, interval, "hour", serviceList, list);
			break;
		case 24 * 3600:
			map = getMetricDetailResult(tags, interval, "day", serviceList, list);
			break;
		case 7 * 24 * 3600:
			map = getMetricDetailResult(tags, interval, "week", serviceList, list);
			break;
		case 30 * 24 * 3600:
			map = getMetricDetailResult(tags, interval, "month", serviceList, list);
			break;
		case 365 * 24 * 3600:
			map = getMetricDetailResult(tags, interval, "year", serviceList, list);
			break;
		}
		List<Map<String, List>> newList = new ArrayList<>();
		newList.add(map);
		page.setRecords(newList);
		page.setTotal(total);
		return ResultUtil.data(page);
	}

	// 获取全部历史数据
	// 获取deital数据
	// 分页获取
	@RequestMapping(value = "/detailAll", method = RequestMethod.GET)
	public ApiJson getDispbyhDetailAll(PageVo pageVo, String service, String sname) {
		QueryWrapper<TemplatesHistory> queryWrapper = new QueryWrapper<TemplatesHistory>();
		if (StringUtils.isNotBlank(service)) {
			queryWrapper.eq("service", service);
		}
		if (StringUtils.isNotBlank(sname)) {
			queryWrapper.eq("name", sname);
		}
		String gmtModified = DateUtils.getDate7Day();
		queryWrapper.gt("gmt_modified", gmtModified);
		if (StringUtils.isNotBlank(pageVo.getSort()) && StringUtils.isNotBlank(pageVo.getOrder())) {
			if (pageVo.getOrder().toLowerCase().equals("asc")) {
				queryWrapper.orderByAsc(pageVo.getSort());
			} else {
				queryWrapper.orderByDesc(pageVo.getSort());
			}
		}
		List<TemplatesHistory> list = templatesHistoryService.list(queryWrapper);
		return ResultUtil.data(list);
	}

	private List getCustomizeChart(String service, String tags, String name,
			String type, String sname, String templateType) throws Exception {
		Template template = templatesHistoryService.queryTemplate(name, sname);
		if ("minute, hour, day".indexOf(type) >= 0) {
			// 查询年月周数据 templatesHistoryService.queryCurvesMetric(service,
			List<Metrics> result = templatesHistoryService.queryCurvesMetric(service, sname, name, type, tags, templateType);
		    return result;
		} else {
			// 查询年月周数据
			List<Metrics> result = templatesHistoryService.getGt1DayMetrics(service, sname, name, type, tags, templateType);
			return result;
		}
	}

	// JAVA获取某段时间内的所有日期
	public static List<Date> findDates(Date dStart, Date dEnd, String type) {
		Calendar cStart = Calendar.getInstance();
		cStart.setTime(dEnd);

		List<Date> dateList = new ArrayList<>();
		// 别忘了，把起始日期加上
		dateList.add(dEnd);
		// 此日期是否在指定日期之后
		while (dStart.before(cStart.getTime())) {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			switch (type) {
			case "minute":
				cStart.add(Calendar.MINUTE, -1);
				dateList.add(cStart.getTime());
				break;
			case "hour":
				cStart.add(Calendar.MINUTE, -1);
				dateList.add(cStart.getTime());
				break;
			case "day":
				cStart.add(Calendar.HOUR_OF_DAY, -1);
				dateList.add(cStart.getTime());
				break;
			case "month":
				cStart.add(Calendar.DAY_OF_MONTH, -1);
				dateList.add(cStart.getTime());
				break;
			case "year":
				cStart.add(Calendar.DAY_OF_MONTH, -30);
				dateList.add(cStart.getTime());
				break;
			case "week":
				cStart.add(Calendar.DAY_OF_WEEK, -1);
				dateList.add(cStart.getTime());
				break;
			}
		}
		return dateList;
	}

	// string类型转换为date类型
	// strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
	// HH时mm分ss秒，
	// strTime的时间格式必须要与formatType的时间格式相同
	public static Date stringToDate(String strTime, String formatType) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
		Date date = null;
		date = formatter.parse(strTime);
		return date;
	}

	// 获取某天的开始时间
	public static Date getDayStartTime2(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d) {
			calendar.setTime(d);
		}
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	// 获取某天的结束时间
	public static Date getDayEndTime2(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d) {
			calendar.setTime(d);
		}
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	// 获取某月的开始时间
	public static Date getDayStartMonthTime2(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d) {
			calendar.setTime(d);
		}
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH) - 30, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	// 获取月的结束时间
	public static Date getDayEndMonthTime2(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d) {
			calendar.setTime(d);
		}
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	// 某分钟的开始时间
	private Date getDayEnd5Minute(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d) {
			calendar.setTime(d);
		}
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		return calendar.getTime();
	}

	// 某分钟的结束时间
	private Date getDayStart5Minute(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d) {
			calendar.setTime(d);
		}
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE) - 1, calendar.get(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	// 某天的最近一小时开始时间
	private Date getDayStart(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d) {
			calendar.setTime(d);
		}
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.HOUR_OF_DAY) - 1, calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	// 某天的最近一小时结束时间
	private Date getDayEnd(Date d) {
		Calendar calendar = Calendar.getInstance();
		if (null != d) {
			calendar.setTime(d);
		}
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		return calendar.getTime();
	}


	private List getCustomizeDetailChart(String tags, String name, String startTime, String endTime, String type,
			List<String> serviceList, String sname) throws Exception {
		Template template = templatesHistoryService.queryTemplate(name, sname);
		if ("minute, hour, day".indexOf(type) >= 0) {
			List<Object> dataList = new ArrayList<>();
			for (String service : serviceList) {
				// 查询年月周数据
				List<Metrics> result = templatesHistoryService.queryCurvesMetric(service, sname, name, type, tags, template.getType());
			    result.forEach(item -> {
				  dataList.add(item);
			  });
			}
			return dataList;
		} else {
			List<Object> dataList = new ArrayList<>();
			for (String service : serviceList) {
				// 查询年月周数据
			  List<Metrics> result = templatesHistoryService.getGt1DayMetrics(service, sname, name, type, tags, template.getType());
			  result.forEach(item -> {
				  dataList.add(item);
			  });
			}
			return dataList;
		}
	}

	private List getTrendChart(String tags, String name, String startTime, String endTime, String hostIds, String type,
			List<String> serviceList, String sname) throws Exception {
		Template template = templatesHistoryService.queryTemplate(name, sname);
		List<Object> dataList = new ArrayList<>();
		for (String service : serviceList) {
			List<Metrics> result = templatesHistoryService.queryTrendList(service, sname, name, type, tags, template.getType(), hostIds);
			result.forEach(item -> {
				dataList.add(item);
			});
		}
		return dataList;
	}

	public Map<String, List> getMetricDetailResult(String tags, int interval, String type, List<String> serviceList,
			List<Template> list) throws Exception {
		Map<String, List> map = new HashMap<>();
		if (list != null) {
			for (Template template : list) {
				String startTime = DateUtils.formatDate(new Date(new Date().getTime() - interval * 1000L),
						"yyyy-MM-dd HH:mm:ss");
				String endTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
				List result = getCustomizeDetailChart(tags, template.getName(), startTime, endTime, type, serviceList,
						template.getSname());
				if (result != null)
					map.put(template.getName() + template.getSname(), result);
			}
		}
		return map;
	}

	public Map<String, List> getMetricResult(String service, String tags, int interval,
			String type, String name, String sname, String templateType) throws Exception {
		Map<String, List> map = new HashMap<>();
		if (StringUtils.isNotBlank(service)) {
			List result = getCustomizeChart(service, tags, name, type,
					sname, templateType);
			 map.put(name + sname, result);
		}
		return map;
	}

	public Map<String, List> getTrendResult(String tags, List<Template> list, int interval, String hostIds, String type,
			List<String> serviceList) throws Exception {
		Map<String, List> map = new HashMap<>();
		if (list != null) {
			for (Template template : list) {
				String startTime = DateUtils.formatDate(new Date(new Date().getTime() - interval * 1000L),
						"yyyy-MM-dd HH:mm:ss");
				String endTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
				List result = getTrendChart(tags, template.getName(), startTime, endTime, hostIds, type, serviceList,
						template.getSname());
				map.put(template.getName() + template.getSname(), result);
			}
		}
		return map;
	}

	/**
	 * 查找所有模板@param service实例名称,@param tags标签名称
	 */
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	public ApiJson getTemplateName(String name, String tags) {
		List<Template> list = templatesHistoryService.queryTemplateAllName(name, tags);
		return ResultUtil.data(list);
	}
	
	/**
	 * 查找所有模板@param service实例名称,@param tags标签名称
	 */
	@RequestMapping(value = "/template_pagination", method = RequestMethod.GET)
	public ApiJson getTemplateByPage(PageVo pageVo, String service, int interval, String search, String tags, String type) {
		IPage page = new Page(pageVo.getPageNumber(), pageVo.getPageSize());
		List<Template> list = templatesHistoryService.queryTemplateName(service, pageVo.getPageNumber(),
				pageVo.getPageSize(), tags, search, type);
		Long total = templatesHistoryService.queryTemplateNameCount(service, tags, search, type);
		page.setRecords(list);
		page.setTotal(total);
		return ResultUtil.data(page);
	}
}

package com.mingheinfo.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mingheinfo.maxdata.core.entity.LicenseInfo;
import com.mingheinfo.maxdata.core.mapper.HostMapper;
import com.mingheinfo.maxdata.core.service.HostService;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

public class LicenseUtils {
    private static final Logger logger = LoggerFactory.getLogger(LicenseUtils.class);
    
    private static LicenseInfo licenseInfo = LicenseUtils.getLicenseInfo();
	private static String licenseCode = "";
	private static HostService hostService = SpringContextUtils.getBean(HostService.class);
	
	static{
		getLicenseCode();
	}

    public static String getRequestCode()
    {
    	Map<String,String> data=new HashMap<>();
    	
    	SystemInfo si = new SystemInfo();
    	HardwareAbstractionLayer hal = si.getHardware();
    	OperatingSystem os = si.getOperatingSystem();
    	
    	data.put("cpuName", hal.getProcessor().getName());
    	data.put("cpuProcessorId",hal.getProcessor().getProcessorID());
    	
    	String jsonStr = JSON.toJSONString(data);
    	String content = Base64.encode(jsonStr.getBytes());
    	
    	return content;
    }
    
	public static LicenseInfo getLicenseInfo()
	{
		if(StringUtils.isEmpty(licenseCode))
		{
			getLicenseCode();
		}
    	
		if(StringUtils.isEmpty(licenseCode))
			return null;
		
        @SuppressWarnings("restriction")
		String jstr = new String(Base64.decode(licenseCode));
        JSONObject obj = JSONObject.parseObject(jstr); 
        String lic = obj.getString("license");
        String aeskey = obj.getString("aeskey");
        @SuppressWarnings("restriction")
		byte[] aesKeyByte = Base64.decode(aeskey);
        LicenseInfo licenseInfo=null;
		try {
			String publicKey = RsaUtils.loadPublicKeyByFile(LicenseUtils.class.getResourceAsStream("/pub.key"));
	        RSAPublicKey pubKey = RsaUtils.loadPublicKeyByStr(publicKey);
	        byte[] realaeskey = RsaUtils.decrypt(pubKey, aesKeyByte);
	        byte[] dec = AesUtils.decrypt(realaeskey, lic);
	        licenseInfo = JSONObject.parseObject(new String(dec),LicenseInfo.class);
	        if(licenseInfo!=null)
	        {
	        	String reqCode = getRequestCode(); 
	        	if(licenseInfo.getReqCode().equals(reqCode)){
	        		return null;
	        	}
	        }
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}


        return licenseInfo;
	}
	
	
	public static void getLicenseCode()
	{
		String path = System.getProperty("user.dir")+"/license.txt";

		//String path="e:\\license.txt";
		File file=new File(path);
		if(file.exists())
		{
			try {
				InputStream license = new FileInputStream(file);
				String licbody = MimeUtils.verifySignature(license, MimeUtils.loadCert());
				licenseCode = licbody;
			}
			catch (FileNotFoundException e){
				e.printStackTrace();
				logger.error("license文件不存在",e);
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error("license文件异常",e);
			}	
		}
	}
	/**
	 * 校验license是否过期， 过期后没有查看权限
	 * */
	public static boolean isExpired() {
		LicenseInfo info = LicenseUtils.getLicenseInfo();
    	if(info != null) 
    	{
    		long time = info.getExpireDate().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    		long timestamp = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    		if(timestamp <= time) 
    		{
    			return false;
    		}
    	} 
    	return true;
	}
	/**
	 * 实例数是否超过最大值，超过最大值没有添加权限
	 * */
	public static boolean isLimitMax() {
		LicenseInfo info = LicenseUtils.getLicenseInfo();
	 	int instanceCount = hostService.hostCnt();
	 	if(info.getInstanceCount() < instanceCount) {
	 		return false;
	 	}
	 	return true;
	}
}

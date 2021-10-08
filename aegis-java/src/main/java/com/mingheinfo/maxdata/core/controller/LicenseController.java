package com.mingheinfo.maxdata.core.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mingheinfo.common.utils.MimeUtils;
import com.mingheinfo.common.json.ApiJson;
import com.mingheinfo.common.utils.AesUtils;
import com.mingheinfo.common.utils.CryptoUtils;
import com.mingheinfo.common.utils.LicenseUtils;
import com.mingheinfo.common.utils.RsaUtils;
import com.mingheinfo.maxdata.core.entity.LicenseInfo;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

@RestController
@CrossOrigin
@RequestMapping("/api/license")
public class LicenseController{
	private static final Logger logger = LoggerFactory.getLogger(LicenseController.class);

    @RequestMapping(value = "upload",method = RequestMethod.POST)
    @ResponseBody
    public ApiJson upload(@RequestParam("file") MultipartFile file,HttpServletRequest request){
    	ApiJson ajaxJson = new ApiJson();
    	String path=System.getProperty("user.dir")+"/license.txt";

		try {
			file.transferTo(new File(path));
			LicenseUtils.getLicenseCode();
			
			LicenseInfo info = LicenseUtils.getLicenseInfo();
			
			//System.out.println("===="+JSON.toJSON(info).toString());
			if(info==null)
				ajaxJson.setError("-1", "许可证不正确");
			else
			{
				ajaxJson.setResult(info);

			}
			
		} catch (IllegalStateException e) {
			ajaxJson.setError("-1", e.getMessage());
			logger.error("上传许可：",e);
		} catch (IOException e) {
			ajaxJson.setError("-2", e.getMessage());
			logger.error("上传许可：",e);
		}catch (Exception e) {
			ajaxJson.setError("-3", e.getMessage());
			logger.error("上传许可：",e);
		}

		return ajaxJson;
    }
    
    @RequestMapping(value = "getLicenseInfo",method = RequestMethod.GET)
    @ResponseBody
    public ApiJson getLicense(){
    	ApiJson ajaxJson = new ApiJson();

		try {
			LicenseInfo info = LicenseUtils.getLicenseInfo();
			
			if(info==null)
			{
				ajaxJson.setError("-1", "许可证不正确");								
			}
			ajaxJson.setResult(info);
		} catch (Exception e) {
			logger.error("获取许可信息：",e);
		}
		return ajaxJson;
    }    
    
    @RequestMapping(value = "getRequestCode",method = RequestMethod.GET)
    @ResponseBody
    public ApiJson getRequestCode() {
    	ApiJson ajaxJson = new ApiJson();
    	
    	String reqCode = LicenseUtils.getRequestCode();
    	
    	ajaxJson.setResult(reqCode);
    	return ajaxJson;
    }
    
    @RequestMapping(value = "downloadRequestCode",method = RequestMethod.GET)
    public void downloadRequestCode(HttpServletResponse response) {
    	
    	String reqCode = LicenseUtils.getRequestCode();
    	
    	response.reset();  
        response.setHeader("Content-Disposition", "attachment; filename=\"reqcode.txt\"");  
        response.addHeader("Content-Length", "" + reqCode.length());  
        response.setContentType("application/octet-stream;charset=UTF-8");  
        OutputStream outputStream;
		try {
			outputStream = new BufferedOutputStream(response.getOutputStream());
	        outputStream.write(reqCode.getBytes());    	
	        outputStream.flush();  	  
	        outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("下载请求码：",e);
		}  
    }
     
   
    @RequestMapping(value = "verifyLicense",method = RequestMethod.GET)
    @ResponseBody
    public ApiJson verifyLicense(HttpServletResponse response) throws Exception {
    	ApiJson ajaxJson = new ApiJson();
    	
    	InputStream license = MimeUtils.class.getClassLoader().getResourceAsStream("license.txt");
    	
		String licbody = MimeUtils.verifySignature(license, MimeUtils.loadCert());
		logger.info(licbody);
        String jstr = new String(Base64.decode(licbody));
        JSONObject obj = JSONObject.parseObject(jstr); 
        String lic = obj.getString("license");
        String aeskey = obj.getString("aesKey");
        
        byte[] aesKeyByte = Base64.decode(aeskey);
        
        String publicKey = RsaUtils.loadPublicKeyByFile(this.getClass().getClassLoader().getResourceAsStream("pub.key"));
        RSAPublicKey pubKey = RsaUtils.loadPublicKeyByStr(publicKey);
        byte[] realaeskey = RsaUtils.decrypt(pubKey, aesKeyByte);
        byte[] dec = AesUtils.decrypt(realaeskey, lic);
        String cc = new String(dec);
        JSONObject ff = JSONObject.parseObject(jstr); 
        ajaxJson.setResult(cc);
        
    	return ajaxJson;
    }
}

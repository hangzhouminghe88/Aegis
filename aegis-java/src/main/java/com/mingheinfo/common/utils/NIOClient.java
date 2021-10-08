package com.mingheinfo.common.utils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mingheinfo.common.json.ApiJson;

public class NIOClient {
	private static final Logger logger = LoggerFactory.getLogger(NIOClient.class);
	
	private static final int SIZE = 1024;
	private String ip = "127.0.0.1";
	private int port = 9501;
	private SocketChannel channel=null;
	private Selector selector = null;
 
	String encoding = System.getProperty("file.encoding");
	Charset charset = Charset.forName(encoding);
 
	public NIOClient() {
		
	}
	
	public NIOClient(String ip,int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public boolean connect() throws IOException{
		selector = Selector.open();

		try
		{
			channel = SocketChannel.open();
			InetSocketAddress remote = new InetSocketAddress(this.ip, this.port);

			channel.socket().connect(remote, 3000);
			//channel.connect(remote);

			
			while(!channel.finishConnect() ){
				System.out.print(".");
			}
		}
		catch(ConnectException e)
		{
			logger.error("connect "+this.ip+"：",e);
			return false;
		}
		catch(SocketTimeoutException e)
		{
			logger.error("connect "+this.ip+"：",e);
			return false;
		}
		
		logger.info(this.ip+" connect success!");
		
		// 设置该sc以非阻塞的方式工作
		channel.configureBlocking(false);
		
		// 将SocketChannel对象注册到指定的Selector
		// SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT
		synchronized (selector) {
			channel.register(selector, SelectionKey.OP_READ);//这里注册的是read读，即从服务端读数据过来
		}
		return true;
	}	
	
 
	public boolean connect(String ip,int port) throws IOException{
		selector = Selector.open();

		try
		{
			this.ip  = ip;
			this.port = port;
			
			channel = SocketChannel.open();
			InetSocketAddress remote = new InetSocketAddress(ip, port);
			channel.connect(remote);
			while(!channel.finishConnect() ){
				System.out.print(".");
			}
		}
		catch(ConnectException e)
		{
			logger.error("connect "+this.ip+"：",e);
			return false;
		}
		
		System.out.println("connect success!");
		// 设置该sc以非阻塞的方式工作
		channel.configureBlocking(false);
		
		// 将SocketChannel对象注册到指定的Selector
		// SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT
		channel.register(selector, SelectionKey.OP_READ);//这里注册的是read读，即从服务端读数据过来

		return true;
	}
	
	public void reconnect()
	{
		try {
			connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() throws IOException
	{
		selector.close();
		channel.socket().close();
		channel.close();
	}
	
	public boolean isConnected()
	{
		if(channel==null)
			return false;
		
		return channel.isConnected();
	}
	
	public boolean isOpen()
	{
		return channel.isOpen();
	}
	
	public static int length(String value) {
		int valueLength = 0;
		String chinese = "[\u0391-\uFFE5]";
		/* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
		for (int i = 0; i < value.length(); i++) {
			/* 获取一个字符 */
			String temp = value.substring(i, i + 1);
			/* 判断是否为中文字符 */
			if (temp.matches(chinese)) {
				/* 中文字符长度为2 */
				valueLength += 2;
			} else {
				/* 其他字符长度为1 */
				valueLength += 1;
			}
		}
		return valueLength;
	}

	/**
	 * 客户端发送数据
	 * 
	 * @param channel
	 * @param bytes
	 * @throws Exception
	 */
	protected void sendLen(byte[] bytes) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		channel.write(buffer);
	}	
	
	protected void sendMsg(byte[] bytes) throws IOException {
		ByteBuffer newBuffer = ByteBuffer.wrap(bytes);
		
		/*
		CharBuffer cb = Charset.forName("UTF-8").decode(buffer);
		
		Charset cs = Charset.forName("GB2312");
        ByteBuffer newBuffer = cs.encode(cb);*/
        
		channel.write(newBuffer);

	}
 
	protected synchronized void send(String data) throws IOException {
		int len = length(data);
		
		System.out.println("Len="+len+" =="+data);
		byte[] size = ByteUtils.int2Bytes(len);
		this.sendLen(size);
		this.sendMsg(data.getBytes("gb2312"));
	}

	public synchronized String receive(int timeout)
	{
		ByteBuffer buffer=ByteBuffer.allocate(200000);//分配缓冲区大小
		StringBuilder result = new StringBuilder();

		int dataLen = 0;
		int recLen = 0;
		
		try {
		        while(selector.select(timeout)>0){//select()方法只能使用一次，用了之后就会自动删除,每个连接到服务器的选择器都是独立的
		        	
		        	//遍历每个有可用IO操作Channel对应的SelectionKey
		            for(SelectionKey sk:selector.selectedKeys()){
		                //如果该SelectionKey对应的Channel中有可读的数据
		                if(sk.isReadable()){
		                    //使用NIO读取Channel中的数据
		                    SocketChannel sc=(SocketChannel)sk.channel();//获取通道信息

		                    int ret = sc.read(buffer);//读取通道里面的数据
		                    
		                    //服务端以断开
		                    if(ret==-1)
		                    {
		                    	this.connect();
		                    	logger.error("服务器 "+this.ip+"已断开");
		                    }
		                    
		                    buffer.flip();//调用此方法为一系列通道写入或获取操作做好准备 
		                    // 将字节转化为为UTF-8的字符串  
		                    //String receivedString=Charset.forName("UTF-8").newDecoder().decode(buffer).toString();
		                    // 控制台打印出来  
		                    //System.out.println("接收到来自服务器" + sc.socket().getRemoteSocketAddress() + "的信息:"  + receivedString);  
		                    // 为下一次读取作准备  
		                    //System.out.println("有数据==============");
		                    if(result.length()==0)
		                    {
		                    	dataLen = buffer.getInt(0);

								if(dataLen>1)
								{
									//System.out.println("");
									logger.info("接收到数据长度："+dataLen);
									//System.out.println(dataLen+"===="+buffer.limit());
									buffer.position(4);
									recLen=buffer.limit();
									
									if(recLen>dataLen)
									{
										buffer.limit(4+dataLen);
									}
									
									result.append(getString(buffer));
									//System.out.println("有数据1=============="+result.toString());
									logger.info("接收到数据："+result.toString());
								}
								else if(dataLen<1)
								{
									
									//System.out.println("有数据x=============="+dataLen);
									//无数据
									sk.interestOps(SelectionKey.OP_READ);
									selector.selectedKeys().remove(sk);
									ApiJson json=new ApiJson();
									json.setCode("99");
									return JSONObject.toJSONString(json);									
									
								}
								
							}
		                    else
		                    {
		                    	recLen+=buffer.limit();
	                    		result.append(getString(buffer));
	                    		//System.out.println("有数据2=============="+result.toString());
	                    		logger.info("接收到剩余数据："+result.toString());
		                    }
		                    
							buffer.clear();
							sk.interestOps(SelectionKey.OP_READ);
		                }
		                 //删除正在处理的SelectionKey
		                selector.selectedKeys().remove(sk);
		            }
		            
		            if(dataLen==-1)
		            {
		            	//System.out.println("有数据4=============="+result.toString());
		            	break;
		            }
		            
					if(recLen>dataLen)
					{
						buffer.clear();
						//System.out.println("有数据3=============="+result.toString());
						logger.info("接收全部数据已完成");
						return result.toString();
					}		            
		        }
		    } catch (IOException e) {
		        //e.printStackTrace();
		    	logger.error("接收数据异常",e);
		    }

		if(StringUtils.isEmpty(result.toString()))
		{
			ApiJson json=new ApiJson();
			json.setError(logger,"-1", "数据接收异常");
			
			return JSONObject.toJSONString(json);
		}
		return result.toString();		
	}
	
	public synchronized void clearBuffer()
	{
		ByteBuffer buffer=ByteBuffer.allocate(20000);//分配缓冲区大小
		
		try {
		        while(selector.select(2000)>0){
		        	
		        	//遍历每个有可用IO操作Channel对应的SelectionKey
		            for(SelectionKey sk:selector.selectedKeys()){
		                //如果该SelectionKey对应的Channel中有可读的数据
		                if(sk.isReadable()){
		                    //使用NIO读取Channel中的数据
		                    SocketChannel sc=(SocketChannel)sk.channel();//获取通道信息

		                    int ret = sc.read(buffer);//读取通道里面的数据
		                    
		                    buffer.flip();//调用此方法为一系列通道写入或获取操作做好准备 
                   
							buffer.clear();
							sk.interestOps(SelectionKey.OP_READ);
							
							System.out.println("=========================有缓存数据=========================");
		                }
		                 //删除正在处理的SelectionKey
		                selector.selectedKeys().remove(sk);
		            }
	            
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }

	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static String getString(ByteBuffer buffer)  
    {  
        Charset charset = null;  
        CharsetDecoder decoder = null;  
        CharBuffer charBuffer = null;  
        try  
        {  
            charset = Charset.forName("GB2312");  
            //decoder = charset.newDecoder();  
            //charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空  
            
            ByteBuffer dd = buffer.asReadOnlyBuffer();
            charBuffer = charset.decode(dd);  
            
            return charBuffer.toString();  
        }  
        catch (Exception ex)  
        {  
        	logger.info("getString:",ex);
            //ex.printStackTrace();  
            return "";  
        }  
    }  
	
	protected void finalize(){
	    try {
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		NIOClient client = new NIOClient();
		try {
			client.connect("10.10.10.199",9501);
			
			String userName = "admin";
			String password = CryptoUtils.MD5(CryptoUtils.MD5("123456"));
			String msg = String.format("login User=\"%s\" Password=\"%s\"", userName,"6698d0c2522ccc0401d9fada0a57318f");
			
			System.out.println(msg);
			
			client.send(msg);
			//String data = client.receiveData();
			//System.out.println("login1="+data);
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
	
}

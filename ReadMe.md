# Halo-Turbo #
Android Socket框架  

## 添加依赖 ##
###Step 1. Add the JitPack repository to your build file  
Add it in your root build.gradle at the end of repositories:  

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}  

###Step 2. Add the dependency  

	dependencies {
        compile 'com.github.EthanCo.Halo-Turbo:halo-turbo:2.1.0'
		compile 'com.github.EthanCo.Halo-Turbo:halo-turbo-mina:2.1.0'
		compile 'com.github.EthanCo.Halo-Turbo:json-convertor:2.1.0'
	}

> halo-turbo是核心库，带有有组播功能  
> halo-turbo-mina:使用TCP需依赖此库  
> json-convertor:将需发送的对象自动转换为json字符串

## 使用 ##

###TCP client

	Halo halo = new Halo.Builder()
            .setMode(Mode.MINA_NIO_TCP_CLIENT)
            .setTargetIP(targetIP)
            .setTargetPort(19701)
			.addHandler(new DemoHandler())
            .build();  
	boolean result = halo.start();

###TCP Server  

	 Halo halo = new Halo.Builder()
            .setMode(Mode.MINA_NIO_TCP_SERVER)
            .setSourcePort(19701)
			.addHandler(new DemoHandler())
            .build();
	boolean result = halo.start();

###组播  

	Halo halo = new Halo.Builder()
            .setMode(Mode.MULTICAST)
            .setSourcePort(19601)
            .setTargetPort(19602)
            .setTargetIP("224.0.0.1")
            .addHandler(new DemoHandler())
            .build();
	boolean result = halo.start();  

###UDP  
//TODO 待实现

###添加回调处理  

	new Halo.Builder().addHandler(new DemoHandler())  

**说明:**  
1. 可通过addHandler()设置回调  
2. 可使用session来发送数据和管理连接  

	public interface IHandler {
	    //会话(session)创建之后，回调该方法
	    void sessionCreated(ISession session);
	
	    //会话(session)打开之后，回调该方法
	    void sessionOpened(ISession session);
	
	    //会话(session)关闭后，回调该方法
	    void sessionClosed(ISession session);
	
	    //接收到消息时回调这个方法
	    void messageReceived(ISession session, Object message);
	
	    //发送数据时回调这个方法
	    void messageSent(ISession session, Object message);
	}  

##添加日志打印  

	new Halo.Builder().addHandler(new StringLogHandler(TAG))  

Halo默认实现了如下几个日志打印类，亦可自定义日志打印  

StringLogHandler:默认日志打印，打印成字符串  
HexLogHandler:Byte日志打印，如果是byte数组，将打印成十六进制  

##添加转换器  
通过连接转换器，可在发送数据时，自动转换为相关类别  

	new Halo.Builder().addConvert(new ObjectJsonConvertor())  

Halo默认提供了如下转换器，亦可自定义转换器  

ObjectJsonConvertor:发送Object对象时，自动将其转换为Json字符串  

	session.write(object);  //发送object时自动转换为json字符串再发送

ObjectJsonByteConvertor:如果是Object对象，则先转换为json字符串后，再转换为byte[]

使用如上转换器，需格外添加依赖  

	dependencies {
     	//...
		compile 'com.github.EthanCo.Halo-Turbo:json-convertor:2.1.0'
	}

##setBufferSize  
设置Buffer大小  

##setThreadPool  
设置自定义线程池  

##其他  

具体详见Sample


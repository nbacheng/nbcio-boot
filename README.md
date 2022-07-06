Nbcio-Boot V1.0.0 NBCIO亿事达企业管理平台
===============

基于jeecg-boot3.0版本（发布日期：2021-11-01）


## 后端技术架构
- 基础框架：Spring Boot 2.3.5.RELEASE

- 持久层框架：Mybatis-plus 3.4.3.1

- 安全框架：Apache Shiro 1.7.0，Jwt 3.11.0

- 数据库连接池：阿里巴巴Druid 1.1.22

- 缓存框架：redis

- 日志打印：logback

- 其他：fastjson，poi，Swagger-ui，quartz, lombok（简化代码）等。



## 开发环境

- 语言：Java 8

- IDE(JAVA)： STS安装lombok插件 或者 IDEA

- 依赖管理：Maven

- 数据库：MySQL5.7+  &  Oracle 11g & SqlServer & postgresql & 国产等更多数据库

- 缓存：Redis


## 技术文档


- 在线演示 ：  [http://boot.jeecg.com](http://boot.jeecg.com)

- 在线文档：  [http://doc.jeecg.com](http://doc.jeecg.com)

- 常见问题：  [http://jeecg.com/doc/qa](http://jeecg.com/doc/qa)

- QQ交流群 ：655054809，个人网页:https://blog.csdn.net/qq_40032778

注意： 如果本地安装了mysql和redis,启动容器前先停掉本地服务，不然会端口冲突。
       net stop redis
       net stop mysql
 
# 1.配置host

    # nbcioboot
    127.0.0.1   nbcio-boot-redis
    127.0.0.1   nbcio-boot-mysql
    127.0.0.1   nbcio-boot-system
	
# 2.修改项目配置文件 application.yml
    active: dev
	
# 3.修改application-dev.yml文件的数据库和redis链接
	修改数据库连接和redis连接，将连接改成host方式

# 4.先进JAVA项目jeecg-boot根路径 maven打包
    mvn clean package

# 5.访问后台项目（注意要开启swagger）
    http://localhost:8080/nbcio-boot/doc.html

## 增加的主要功能

   1、基于flowable 6.7.2 的工作流管理:
          包括流程设计、表单定义、流程发起、流程流转和消息提醒等功能，同时支持自定义业务的流程定义与流转。

   2、基于钉钉的薪资流程审批例子:
          写了一个薪资的钉钉流程流转，通过定义流程，同时结合钉钉，发起后通过钉钉来进行审批与流转。

   3、写了一个前端实现从表ERP格式选择，以便以后满足库存管理等ERP应用。

   4、参考了多个开源项目，在此表示感谢。

   5、以后希望能增加OA和ERP等相关功能。
   
   系统效果
   ----
   ![输入图片说明](https://static.oschina.net/uploads/img/201912/25133248_Ag1C.jpg "在这里输入图片标题")
   
     ![输入图片说明](https://img-blog.csdnimg.cn/863a745945624a45acfe8e7856c6134e.png "在这里输入图片标题")
	 
	 https://img-blog.csdnimg.cn/898e102ca3d5452bb2d23d989be008ab.png
	 
	 https://img-blog.csdnimg.cn/c63e81a73b0e40b8b3f21963e92199ec.png
	 
	 https://img-blog.csdnimg.cn/83441ac37474453a94181be59e844469.png
	 
	 https://img-blog.csdnimg.cn/45ef09d3626747858a750ada9a2206e3.png
	 
	 https://img-blog.csdnimg.cn/e5443bd5d38442969a7c20895d563712.png
	 
	 https://img-blog.csdnimg.cn/e5443bd5d38442969a7c20895d563712.png
	 
	 https://img-blog.csdnimg.cn/e5443bd5d38442969a7c20895d563712.png
	 
	 https://img-blog.csdnimg.cn/610bef9ed3ca4aa8a9d3e2e6ef242877.png
	 
	 https://img-blog.csdnimg.cn/a2991f0d55a64a7b882194aea3f0f3a2.png
	 
	 https://img-blog.csdnimg.cn/db30f76b9b2447a9a941a8541fb98587.png

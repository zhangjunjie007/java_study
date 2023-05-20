﻿﻿﻿﻿﻿# 1、idea 快捷键

**`都提前设置为eclipse的`**

  **查看方法在哪里调用**

> ctrl+鼠标点击
>
> 或者
>
> crtl+alt+h

  **查看变量在哪里使用（也可查看方法）**

> ctrl+G

**查看类的继承关系**

>  F4
>
> 或者
>
> 选中类名，右键选择 diagrams

# 2、debug

## 2.1 **快速跳转**

![alt](https://img-blog.csdnimg.cn/c5499753b3b14804abbca1bb4d4700bd.png)

> 断点打在上方的时候，单击下面的行数，断点直接就会跳转到对应行

## 2.2 **回退到上一个方法**

![图片](https://img-blog.csdnimg.cn/0406384c2a66476790fa1f9908be28d4.png)

> 假如此时直接让断点进入到getData()内，但是调用方法之前的一些逻辑没有看清，可以删除getData()这个栈帧，回到调用之前，如下图

![](https://img-blog.csdnimg.cn/806e108a1f704df5bd5ae3790a01228c.png)

***==注意：只有多个方法之前相互调用，也就是创建多个栈帧的时候会游泳，如果都在一个方法里那不能==***

## 2.3 条件断点

![](https://img-blog.csdnimg.cn/d199188101724f41ab9da36ba593ca8f.png)

> 条件断点可以用到循环中，也可以用到一个方法被多处调用的情况，就拿循环来打比方：
>
> ==在100次的循环中，想把断点打到第20次的地方==
>
> 1、现在想断点的行数正常断点
>
> 2、右键双击断点，弹出图中条件框，可以输入条件，完成后点击 done
>
> 3、释放之前的断点，会自动跳转到符合你设置条件的那个时刻

==这里如果选择threa，那就是对线程进行定位追踪==

## 2.4 固定表达式追踪

![](https://img-blog.csdnimg.cn/42c1c44c6046416a844a4b659fec99d2.png)

> 想要一直查看某一个属性的值，不用一直打开对象一个一个翻属性，可以直接按箭头标记的点击+号，配置好要跟踪的属性

## 2.5 断点开关

![](https://img-blog.csdnimg.cn/acf987103d2a4ee5ab468bb3bc3d682f.png)

> 想要临时关闭一些断点，可以点击这个，跳出所有的断点。把勾选去掉即可。
>
> 同理，想要把上次去掉的加上，可以在调出来，然后在勾选上就行
>
> 箭头下面的那个点，点击以后 断点全部失效

## 2.6 动态执行表达式

![在这里插入图片描述](https://img-blog.csdnimg.cn/c0d5a67bd7e74bff9ee3446aa7c098c8.png)



![在这里插入图片描述](https://img-blog.csdnimg.cn/f2ccf519e02d4bea97c164d14574a723.png)

> 如图：
>
>   想要在debug期间，动态改变某一个变量的值，可以右键，选择evaluate 表达式选项，然后输入你要改变的值，执行完以后就会发现变量里面的值是你新设置进去的

## 2.7 源码中想要打印变量值

![在这里插入图片描述](https://img-blog.csdnimg.cn/1e9bc397c48145d8948989f658f3f31c.jpeg)

> 想在源码中输出，可以根据以上的方式进行操作，查看代码运行状况

# 3、idea 集成docker

## 1、开启docker的远程访问

也可以用 TLS 开启远程访问，具体可[参考]([IDEA 集成 Docker 插件实现一键远程部署 SpringBoot 应用，无需三方依赖，开源微服务全栈有来商城线上部署方式 - 有来技术团队 - 博客园 (cnblogs.com)](https://www.cnblogs.com/haoxianrui/p/15322508.html#2-docker-启用-tls))：

Docker服务打开2375端口

> 登录docker所在服务器，修改docker.service文件

```shell
vi /usr/lib/systemd/system/docker.service
```

**修改如下内容：**

```bash
ExecStart=/usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
```

**改为(可以直接注释上一行，然后把这一行加进去)：**

```bash
ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock
```

**重新加载配置文件**

``` bash
systemctl daemon-reload  
```

**重启服务**

```bash
systemctl restart docker.service
```

***启动以后查看端口是否开放***

```bash
netstat -nlpt
```

**需要注意的是，在生产环境中，开放2375端口会增加安全风险，因此需要配合安全设置，如防火墙和身份验证等。**

## docker 指定运行环境

>  docker run -d --name eureka_02 -p 8762:8762  b043548c08e1 --spring.profiles.active=dev

## 2、[安装docker](https://so.csdn.net/so/search?q=安装docker&spm=1001.2101.3001.7020)插件

![docker插件下载](https://img-blog.csdnimg.cn/bc2b9c0541244a8c979c94caac783d02.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6b6Z5bCn5bel5L2c5a6k,size_20,color_FFFFFF,t_70,g_se,x_16)

## 3、配置docker

![在这里插入图片描述](https://img-blog.csdnimg.cn/173affd3fac842249aed76f6ffccbcb5.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6b6Z5bCn5bel5L2c5a6k,size_20,color_FFFFFF,t_70,g_se,x_16)

> **填写**
> `tcp://地址:2375`

**注意：一定要Connection successful才成功了**

## 4、使用该插件进行各种镜像容器的操作

![在这里插入图片描述](https://img-blog.csdnimg.cn/5114d4c64d184f5bb13ba10e6808c663.png)



> 如图：先打开service窗口，然后可以对容器或者镜像操作

## 5、代码直接打包发布成为新镜像

### 1. Dockerfile 文件

在项目根目录添加 `Dockerfile` 文件

```bash
# 基础镜像
FROM openjdk:8-jre

# 维护者信息
MAINTAINER zhangjj <17310319450@163.com>

# 设置容器时区为当前时区
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone

# /tmp 目录作为容器数据卷目录，SpringBoot内嵌Tomcat容器默认使用/tmp作为工作目录，任何向 /tmp 中写入的信息不会记录进容器存储层
# 在宿主机的/var/lib/docker目录下创建一个临时文件并把它链接到容器中的/tmp目录
VOLUME /tmp

# 复制jar包
COPY *.jar /data/app/app.jar  #这个地址和启动命令里面的地址要一致

# 容器启动执行命令
ENTRYPOINT ["java", "-Xmx128m", "-jar", "/data/app/app.jar"]

# 声明容器提供服务端口
EXPOSE 8800


```

### 2. pom.xml文件

添加 spring-boot-maven-plugin 依赖为`SpringBoot` 应用打包，指定 `finalName` 不带版本号，这样打包出的 jar 包名称就没有版本号。

```xml
<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### 3. Run/Debug Configurations 配置

`Run/Debug Configurations` 运行/调试 配置

![image-20210922082012314](https://img-blog.csdnimg.cn/img_convert/e324355f09b9773d3bea8306c0acf757.png)

`Dockerfile` 配置

![image-20210922083446011](https://img-blog.csdnimg.cn/img_convert/7e048e6bdbe4f1089516160397e6551b.png)

添加执行目标 `Maven Goal`

![image-20210922083520500](https://img-blog.csdnimg.cn/img_convert/fabc71f1e42cfaf569909492bf0eab2c.png)



Command line ` 输入 `  clean package -U -DskipTests

![img](https://img-blog.csdnimg.cn/img_convert/a19ba8a8a4f8ea500e9ff09a18cecdef.png)

点击 `OK` 保存配置，至此，已完成所有的配置，接下来就是一键部署。

### 4. SpringBoot应用一键构建部署

工具栏 `Run/Debug` 选择上文的 `Docker`配置

![img](https://img-blog.csdnimg.cn/img_convert/7758034ba834a63a783357e11d2c9ff1.png)

点击 ▶️开始执行

![img](https://img-blog.csdnimg.cn/img_convert/148db99ad2cdf7bad90e8c2c74411241.png)

稍等一会儿，就可以看到启动的容器



![在这里插入图片描述](https://img-blog.csdnimg.cn/b4dd01351e17475985d6b634ad73aeae.png)



**docker构建项目的方式也可以通过pom文件创建mvn命令，进行构建操作**



# 常用单词

archive ：存档

destination ：目的地

extract ：提取

device：设备

volume: 卷

inspect ：检查 检阅

Mountpoint ：挂载点














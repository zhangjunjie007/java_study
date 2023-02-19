# 为什么Docker比虚拟机快？

1.Docker有着比虚拟机更少的抽象层，由于Docker不需要Hypervisor实现硬件资源虚拟化，运行在Docker容器上的程序直接使用的都是实际物理机的硬件资源，因此在Cpu、内存利用率上Docker将会在效率上有明显优势。

2.Docker利用的是宿主机的内核，而不需要Guest OS，因此，当新建一个容器时，Docker不需要和虚拟机一样重新加载一个操作系统，避免了引导、加载操作系统内核这个比较费时费资源的过程，当新建一个虚拟机时，虚拟机软件需要加载Guest OS，这个新建过程是分钟级别的，而Docker由于直接利用宿主机的操作系统则省略了这个过程，因此新建一个Docker容器只需要几秒钟。

![alt](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9yYXcuZ2l0aHVidXNlcmNvbnRlbnQuY29tL2NoZW5nY29kZXgvY2xvdWRpbWcvbWFzdGVyL2ltZy9pbWFnZS0yMDIwMDUxNTEwNDExNzMyOS5wbmc?x-oss-process=image/format,png)



# Docker的常用命令

## 1.帮助命令

- docker version    *#显示docker的版本信息。*
- docker info       *#显示docker的系统信息，包括镜像和容器的数量* 
- docker 命令 --help *#帮助命令*

```shell
[root@VM-8-2-centos ~]# docker ps --help
Usage:  docker ps [OPTIONS]
List containers
Options:
  -a, --all             Show all containers (default shows just running)
  -f, --filter filter   Filter output based on conditions provided
      --format string   Pretty-print containers using a Go template
  -n, --last int        Show n last created containers (includes all states) (default -1)
  -l, --latest          Show the latest created container (includes all states)
      --no-trunc        Don't truncate output
  -q, --quiet           Only display container IDs
  -s, --size            Display total file sizes

```

> 如上 --help命令，返回的  -a 后面跟的--all，本意就是-a可以用来表示 --all 的缩写，效果是一样的

## 2.镜像命令

- docker images #查看所有本地主机上的镜像 可以使用docker image ls代替

- docker search #搜索镜像

- docker pull    #下载镜像 docker image pull

- docker rmi     #删除镜像 docker image rm

## 3.容器命令

- docker run 镜像id #新建容器并启动

- docker ps 列出所有运行的容器 docker container list

- docker rm 容器id #删除指定容器

- docker start 容器id	#启动容器
- docker restart 容器id	#重启容器
- docker stop 容器id	#停止当前正在运行的容器
- docker kill 容器id	#强制停止当前容器

> **运行容器的命令**
>
> docker run [可选参数] image 
>
> 或者
>
> docker container run [可选参数] image 
>
> `#参数说明（可用 docker run --help 查看可以带哪些运行参数）`
>
> - --name="Name"		#容器名字 tomcat01 tomcat02 用来区分容器
> - -d					#后台方式运行
> - -i：以交互模式运行容器
> - -t：为容器重新分配一个伪输入终端
> - -it 	#使用交互方式运行，进入容器查看内容
> - -p	 #指定容器的端口 -p 8080(宿主机):8080(容器)
>   		-p ip:主机端口:容器端口
>     		-p 主机端口:容器端口(常用)
>     		-p 容器端口
>     		容器端口
> - -P(大写) 				随机指定端口

### 列出所有运行的容器

> [root@VM-8-2-centos ~]# docker ps --help
>
> Usage:  docker ps [OPTIONS]
>
> List containers
>
> Options:
>   -a, --all             Show all containers (default shows just running) 
>
> ​                          **#列出当前正在运行的容器 + 带出历史运行过的容器**
>
>   -f, --filter filter   Filter output based on conditions provided
>       --format string   Pretty-print containers using a Go template
>   -n, --last int        Show n last created containers (includes all states) (default -1)
>
> ​                          **#列出最近创建的?个容器 ?为1则只列出最近创建的一个容器,为2则列出2个**
>
>    -l, --latest          Show the latest created container (includes all states)
>       --no-trunc        Don't truncate output
>   -q, --quiet           Only display container IDs
>
> ​                            **#只列出容器的编号**
>
>    -s, --size            Display total file sizes
>
> ​                           **#展示文件大小**

### 退出容器

> exit 		*#容器直接退出* 
>
> ctrl +P +Q  *#容器不停止退出 	---注意：这个很有用的操作*

### 删除容器

> docker rm 容器id   				*#删除指定的容器，不能删除正在运行的容器，如果要强制删除 rm -rf* 
>
> docker rm -f $(docker ps -aq)  	 *#删除所有的容器* 
>
> docker ps -a -q|xargs docker rm  *#删除所有的容器*

### 启动和停止容器的操作

> docker start 容器id	*#启动容器* 
>
> docker restart 容器id	*#重启容器* 
>
> docker stop 容器id	 *#停止当前正在运行的容器* 
>
> docker kill 容器id	 *#强制停止当前容器*

## 4.常用其他命令

### 后台启动命令

```shell
# 命令: docker run -d 镜像名
[root@iz2zeak7sgj6i7hrb2g862z ~]# docker run -d centos
a8f922c255859622ac45ce3a535b7a0e8253329be4756ed6e32265d2dd2fac6c

[root@iz2zeak7sgj6i7hrb2g862z ~]# docker ps    
CONTAINER ID      IMAGE       COMMAND    CREATED     STATUS   PORTS    NAMES
# 问题docker ps. 发现centos 停止了
# 常见的坑，docker容器使用后台运行，就必须要有要一个前台进程，docker发现没有应用，就会自动停止
# nginx，容器启动后，发现自己没有提供服务，就会立刻停止，就是没有程序了

```

### **那如何后台启动呢？**

**问题分析**

> docker容器之后台运行:
>
>   我们 docker run 启动容器的时候，常需要将其在后台运行，通常我们设置参数 -d 即可。 
>
>    但后台运行，其实是有前提的，如果没有前台进程，那么实际运行完docker run命令后，会处于退出状态，即exited。
>
>  例子： docker run -d --name my-redis redis bash # 指定命令是bash，但显然bash在容器起来后，很快就会结束，导致没有前台进程，故容器处于退出状态.

**解决办法**

目前了解的可以使用以下3种方法，具体用哪种方法，依据实际情况，生产情况下，通常用`docker-compose`结合具体`cmd`作为容器初起的命令

#### **1、指定挂起阻塞命令启动容器**

> docker run -d --name my-redis redis sleep 99999999999999

命令执行后，通过**`docker ps -a | grep my-redis`**，可以看到容器处于运行状态，即**`up`**

#### 2、使用交互界面后退出容器

> docker run -it --name my-redis redis bash

之后会进入容器，如果想保持容器后台运行，我们可以 **`Ctrl + p + q`**，再次查看容器运行状态，也可以看到容器处于**`up`**状态。

#### 3、-td命令结合使用

> docker run -itd --name my-redis2 redis bash  # -i 不是必需，加了，容器起来后终端返回容器ID

刚也提到，就是`-d`运行容器时，需要有前台进程，`-t`提供一个伪终端，类似前台进程，查看容器运行状态，也可以看到，容器处于`up`状态。

### 容器数据卷

#### **方式一：**

> 方式一：直接使用命令来挂载 -v
>
> ---
>
>  docker run -it -v 主机目录：容器目录
>
> [root@iZ2zeg4ytp0whqtmxbsqiiZ home]# docker run -it -v /home/ceshi:/home centos /bin/bash

#### 匿名和具名挂载

> -v 容器内路径 (只跟一个路径)
>
> -P 随机指定端口（大写的P 是随机指定端口）
>
> -p 8080:8080 （映射端口）

---

- **匿名挂载**

   -v 容器内路径（不主动指定主机的挂载路径，就是匿名挂载）


   ```shell
    docker  run -d -P --name nginx01 -v /etc/nginx nginx 
   ```

 查看所有volume的情况

```shell
[root@VM-8-2-centos /]# docker volume ls
DRIVER    VOLUME NAME
local     5ce3679fd70d91b1bb3d1bef3ebb186e6dff30c708cbe9bdc7fe1e07729e199a
local     2901a560d13915cabda7de126dbb1e84e127dc0b8ba81deccea0a5bad8934e1f
```

`这里发现，这种情况就是匿名挂载，我们在-v 后面只写了容器内的路径，没有写容器外的路径！`

---

- **具名挂载**

> 第一步：**通过-v 卷名：容器内的路径启动**

```shell
docker run -d -p 8085:8080 --name tomcat05 -v zjj_volume:/usr/local/tomcat/logs 009259e972e1
```

> 查看挂载的volume

```shell
[root@VM-8-2-centos /]# docker volume ls
DRIVER    VOLUME NAME
local     5ce3679fd70d91b1bb3d1bef3ebb186e6dff30c708cbe9bdc7fe1e07729e199a
local     2901a560d13915cabda7de126dbb1e84e127dc0b8ba81deccea0a5bad8934e1f
local     zjj_volume
```

> 查看一下 `zjj_volume` 这个卷:
>
>  **docker volume inspect juming-nginx**

![在这里插入图片描述](https://img-blog.csdnimg.cn/5c7bc6caf18344c18ce3f97d498fdd3e.png)

所有docker容器内的卷，没有指定目录的情况下都是在`/var/lib/docker/volumes/xxxxx/_data`

我们通过具名挂载可以方便的找到我们的一个卷，大多数情况下使用的是`具名挂载`

#### **区分三种挂载方式**

> 如何确定是具名挂载还是匿名挂载，还是`指定路径挂载`！
>
> -v  容器内路径                   # 匿名挂载
> -v  卷名:容器内路径               # 具名挂载
> -v /主机路径:容器内路径            # 指定路径挂载

**拓展**

> 通过 -v 容器内容路径 ro rw 改变读写权限
>
> ro  readonly    # 只读
> rw  readwrite   # 可读可写
>
> docker run -d -P --name nginx02 -v juming-nginx:/etc/nginx:ro nginx
> docker run -d -P --name nginx02 -v juming-nginx:/etc/nginx:rw nginx
>
> ro 只要看到ro就说明这个路径只能通过宿主机来操作，容器内容无法操作

### **进入容器的4种方法**

**docker提供了多种进入容器的方法，这里补充4种常见的方法**

- 使用docker attach
- 使用SSH
- 使用nsenter
- 使用exec

#### 1.**docker attach**

> docker attach 容器ID|容器name 
>
> ps:可以认为这是一个过时的命令，更多的docker用户会考虑使用docker exec来实现相同的功能，但是出于docker官方并没有删除这个命令，我们还是有必要学习一下的。(推荐第四种)

使用该命令有一个问题。当多个窗口同时使用该命令进入该容器时，所有的窗口都会同步显示。如果有一个窗口阻塞了，那么其他窗口也无法再进行操作，建议开发时使用.

#### **2.ssh**

在镜像（或容器）中安装SSH Server，这样就能保证多人进入，

   不建议使用，具体见[为什么不需要在 Docker 容器中运行 sshd](http://www.oschina.net/translate/why-you-dont-need-to-run-sshd-in-docker?cmp)

#### 3.**nsenter**

[nsenter使用方法](https://www.cnblogs.com/xhyan/p/6593075.html)

#### 4.**docker exec-推荐使用**

通常我们可以通过`容器name`或`容器ID`进入到容器中：

- 如果我们在启动容器的时候指定`-name`选项，我们可以：`docker exec -it container_name bash`
- 没指定name，我们可以通过`docker ps`查看容器的ID，之后再通过`docker exec -it containerID bash`进入容器

### 查看日志

> 命令格式：
> $ docker logs [options] 容器ID
>
> Options:
> 	--details 		  # 显示更多的信息
> 	-f或 --follow	  # 跟踪实时日志，按日志输出
> 	      --since 		  # 从某个时间开始显示，例如2013-01-02T13:23:37
> 	-n 或 --tail 			  # 从日志末尾多少行开始显示， 默认是all
> 	-t或 --timestamps  # 显示时间戳
> 	--until 		  # 打印某个时间以前的日志，例如2013-01-02T13:23:37

```shell
➜  ~ docker run -d centos /bin/sh -c "while true;do echo 6666;sleep 1;done" #模拟日志      
#显示日志
-tf		#显示日志信息（一直更新）
--tail number #需要显示日志条数
docker logs -t --tail n 容器id #查看n行日志
docker logs -ft 容器id #跟着日志

```

### 查看容器中进程信息ps

> **命令**:  docker top 容器id

### 查看镜像的元数据

>  **命令**:  docker inspect 容器id

#### **从容器内拷贝到主机上**

> docker cp 容器id:容器内路径  主机目的路径
>
>    eg: docker cp 80b7164fef36:/home/test.java  ./

### commit镜像

> docker commit 提交容器成为一个新的副本
>
> Options:
>   -a, --author string    Author (e.g., "John Hannibal Smith <hannibal@a-team.com>")
>   -c, --change list      Apply Dockerfile instruction to the created image
>   -m, --message string   Commit message
>   -p, --pause            Pause container during commit (default true)
>
> **命令和git原理类似**
>
> docker commit -m="描述信息" -a="作者" 容器id 目标镜像名:[版本TAG]

**Demo**

> ps: *将操作过的容器通过commit调教为一个镜像*
>
> *我们以后就使用我们修改过的镜像即可，而不需要每次都重新拷贝webapps.dist下的文件到webapps了，这就是我们自己的一个修改的镜像。*

**根据容器重新生成镜像**:

>  **docker commit -a "zjj" -m "diy my tomcat" 7dc2e7d083e6  tomcat_diy:1.0**

**挂载启动diy的tomcat镜像：**

> docker run -d --name diy_tomcat02  -v /home/mytomcat/logs:/usr/local/tomcat/logs  -p 8081:8080 009259e972e1





# Portainer 可视化面板安装

> Docker图形化界面管理工具！提供一个后台面板供我们操作！(可以自行百度)

# DockerFile

> **Dockerfile 就是用来构建docker镜像的构建文件**！
>
> 通过这个**脚本可以生成镜像**，镜像是一层一层的，脚本是一个个的命令，每个命令都是一层！

## **构建步骤**

1、编写一个dockerfile文件

2、docker build 构建成为一个镜像

3、docker run 运行制作好的镜像

4、docker push 发布镜像（Docker Hub、[阿里云镜像](https://so.csdn.net/so/search?q=阿里云镜像&spm=1001.2101.3001.7020)仓库）

---

## **DockerFile构建过程**

基础知识：

1. 每个保留关键字（指令）都必须是大写字母
2. 执行从上到下顺序执行
3. #表示注释
4. 每一个指令都会创建提交一个新的镜像层，并提交

![alt](https://img-blog.csdnimg.cn/20200813142333870.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70#pic_center)

DockerFile是面向开发的，我们以后要发布项目，做镜像，就需要编写dockerfile文件，这个文件十分简单，使用DockerFile创建镜像逐渐成为企业交付的标准。

## **DockerFile常用指令**

***创建一个dockerfile文件，名字可以随便 建议Dockerfile***

```shell
FROM centos
VOLUME ["volume01","volume02"]
CMD echo "-----end-----"
CMD /bin/bash
```

ps:编写Dockerfile 时，命令后面带上#注释内容，可能会编译失败

![alt](https://img-blog.csdnimg.cn/20200813144806291.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70#pic_center)

> FROM            # 基础镜像，一切从这里开始构建
> MAINTAINER      # 镜像是谁写的， 姓名+邮箱
> RUN             # 镜像构建的时候需要运行的命令
> ADD             # 步骤， tomcat镜像， 这个tomcat压缩包！添加内容
> WORKDIR         # 镜像的工作目录
> VOLUME          # 挂载的目录
> EXPOSE          # 保留端口配置
> CMD             # 指定这个容器启动的时候要运行的命令，只有最后一个会生效可被替代
> ENTRYPOINT      # 指定这个容器启动的时候要运行的命令， 可以追加命令
> ONBUILD         # 当构建一个被继承DockerFile 这个时候就会运行 ONBUILD 的指令，触发指令
>COPY            # 类似ADD, 将我们文件拷贝到镜像中
> ENV             # 构建的时候设置环境变量！

## 构建命令

> **docker build -f Dockerfile -t my_centos:1.1 ./**

 [docker](https://cloud.tencent.com/product/tke?from=10680) build 命令用于使用 Dockerfile 创建镜像。OPTIONS说明：

- -f :指定要使用的Dockerfile路径；
- –pull :尝试去更新镜像的新版本；
- –quiet, -q :安静模式，成功后只输出镜像 ID；
- –tag, -t: 镜像的名字及标签，通常 name:tag 或者 name 格式；可以在一次构建中为一个镜像设置多个标签。
- -t 参数设置镜像名称 my_centos 和 tag 标签名称 1.1，注意最后面有个点. 这个点代表上下文目录的路径，就是当前路径

### docker build 和 commit 区别

- **构建镜像的方法有2种：**

  - 使用docker commit命令
  - 使用docker build命令和Dockerfile文件

  **不推荐使用docker commit命令，建议使用docker build命令**（编写完Dockerfile然后使用docker build命令）

  > 区别：
  >
  > ​    commit 是基于容器的，在启动的容器内部修改一些配置或者文件内容以后，重新打包生成新的镜像
  >
  > ​    
  >
  > ​    build是利用Dockerfile基于基础镜像来生成新的镜像

   

**启动自己的容器**

> docker run -it --name centos_02 my_centos:1.1 

![在这里插入图片描述](https://img-blog.csdnimg.cn/e67db638ca4240f9b8d0943803d99d81.png)

这两个多出来的目录就是我们生成镜像的时候自动挂载的数据卷目录

**这个卷和外部一定有一个同步的目录！**

**查看容器的挂载信息**

> docker inspect 容器id

查看如下如

![在这里插入图片描述](https://img-blog.csdnimg.cn/e79611ce0c2841b786bbe507d92362f4.png)

假设构建镜像时候没有挂载卷，要手动镜像挂载 -v 卷名:容器内路径！

## 数据卷容器

![alt](https://img-blog.csdnimg.cn/20200813121031935.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70#pic_center)



ps:新启动容器时，可以挂载已经启动容器的数据卷，来达到容器间的数据同步，比如mysql



## 推送到dockerHup

### **打标签**

>  [docker](https://so.csdn.net/so/search?q=docker&spm=1001.2101.3001.7020) tag 用于给镜像打标签，语法如下：
>
> ​     docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]

**示例**

>  比如我现在有一个 centos 镜像：
>
> ```shell
> [root@localhost ~]$ docker images
> REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
> centos              latest              1e1148e4cc2c        2 weeks ago         202MB
> ```
>
> 我对 centos 进行开发，开发了第一个版本，我就可以对这个版本打标签，打完标签后会生成新的镜像：
>
> ```shell
> [root@localhost ~]$ docker tag centos centos:v1
> ```
>
> ```shell
> [root@localhost ~]$ docker images
> REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
> centos              latest              1e1148e4cc2c        2 weeks ago         202MB
> centos              v1                  1e1148e4cc2c        2 weeks ago         202MB
> ```
>
> 我继续对 centos 进行开发，开发了第二个版本，继续打标签：
>
> ```shell
> [root@localhost ~]$ docker tag centos centos:v2
> ```
>
> ```shell
> [root@localhost ~]$ docker images
> REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
> centos              latest              1e1148e4cc2c        2 weeks ago         202MB
> centos              v1                  1e1148e4cc2c        2 weeks ago         202MB
> centos              v2                  1e1148e4cc2c        2 weeks ago         202MB
> ```
>
> 以此类推，每开发一个版本打一个标签，如果以后我想回滚版本，就可以使用指定标签的镜像来创建容器：
>
> ```shell
> [root@localhost ~]$ docker run -itd centos:v1
> ```

需要给推送到仓库的镜像打标签时：

> docker tag 镜像id 你的账户名/镜像仓库名:tag名 

### 推送：

```shell
[root@VM-8-2-centos ~]# docker tag 009259e972e1 zhangjj007/diy_tomcat:v1
```

### 总结：

1、先 build/commit 一个新的镜像

> docker build -t diytomcat .

2、登录

>  docker login -u username -p password   登录hub仓库，再使用命令推送
>
> ps:用户名不是登陆账号，这里使用 zhangjj007

3、打标签

> docker tag 009259e972e1 zhangjj007/diy_tomcat:v1
>
> 标签要根据dockerHup内仓库的id来设置

![在这里插入图片描述](https://img-blog.csdnimg.cn/6df4002f73ec428289dad99fd369fa69.png)

4、push 

```shell
[root@VM-8-2-centos ~]# docker push zhangjj007/diy_tomcat:v1
The push refers to repository [docker.io/zhangjj007/diy_tomcat]
An image does not exist locally with the tag: zhangjj007/diy_tomcat
[root@VM-8-2-centos ~]# docker tag 009259e972e1 zhangjj007/diy_tomcat:v1
[root@VM-8-2-centos ~]# docker push zhangjj007/diy_tomcat:v1
The push refers to repository [docker.io/zhangjj007/diy_tomcat]
c8f326cb9375: Pushed 
850c57462c5e: Pushed 
971f0c572b95: Pushed 
06f1e4d6c406: Pushed 
4cf6b33083da: Pushed 
2428b038926e: Pushed 
2b1193862943: Pushed 
c5ff2d88f679: Pushed 
v1: digest: sha256:2972f2d814da156f3594fc51d7f60175ee89eb1d4073c5685e7a321c79460321 size: 1999

```

查看仓库

<img src="https://img-blog.csdnimg.cn/b3143e6c8af44e1ba61cca7d3f6d282e.png" alt="在这里插入图片描述" style="zoom:80%;" />

5、从个人仓库拉取镜像

```shell
[root@VM-8-2-centos home]# docker pull zhangjj007/diy_tomcat:v1
v1: Pulling from zhangjj007/diy_tomcat
10ac4908093d: Pull complete 
6df15e605e38: Pull complete 
2db012dd504c: Pull complete 
8fa912900627: Pull complete 
f8fe20946c04: Pull complete 
8093daf900d2: Pull complete 
49c22a329043: Pull complete 
209d49c9e8c4: Pull complete 
Digest: sha256:2972f2d814da156f3594fc51d7f60175ee89eb1d4073c5685e7a321c79460321
Status: Downloaded newer image for zhangjj007/diy_tomcat:v1
docker.io/zhangjj007/diy_tomcat:v1
# 查看所有镜像
[root@VM-8-2-centos home]# docker images
REPOSITORY              TAG       IMAGE ID       CREATED         SIZE
zhangjj007/diy_tomcat   v1        009259e972e1   18 hours ago    478MB
nginx                   latest    3f8a00f137a0   4 days ago      142MB
mysql                   latest    7484689f290f   2 months ago    538MB
centos                  latest    5d0da3dc9764   17 months ago   231MB
```

## demo: diy 自己的 tomcat

**启动镜像**

```shell
docker run -d --name mytomcat -p 8088:8080 
-v /home/testTomcat/webapp:/usr/local/tomcat/webapps/testapp  #外部目录挂载到tomcat下的webapps目录下，可以直接发布项目
-v /home/testTomcat/logs:/usr/local/tomcat/logs  009259e972e1 #外部目录挂载tomca的日志目录，可以查看tomcat相关日志
```

**访问**

![在这里插入图片描述](https://img-blog.csdnimg.cn/6ca194a569fa49c2a9fab425f2593c47.png)

> 想要访问自己写的项目，需要把项目放到tomcat的webapps下，然后根据目录来访问即可。
>
> `这个testapp就是挂载的时候新建的目录`

# Docker网络

**获取所有容器名称及其IP地址**

> docker inspect -f '{{.Name}} - {{.NetworkSettings.IPAddress }}' $(docker ps -aq)

```shell
[root@VM-8-2-centos /]# docker inspect -f '{{.Name}} - {{.NetworkSettings.IPAddress }}' $(docker ps -aq)
/tomcat02 - 172.17.0.3
/tomcat01 - 172.17.0.2
```

ps:以后估计会用

## 链接Docker0

![alt](https://img-blog.csdnimg.cn/20200814091723905.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70#pic_center)



- **问题： linux 能不能ping通容器？**

```shell
[root@VM-8-2-centos /]# docker inspect -f '{{.Name}} - {{.NetworkSettings.IPAddress }}' $(docker ps -aq)#查看所有容器名称以及分配的ip
/tomcat01 - 172.17.0.2
[root@VM-8-2-centos /]# ping 172.17.0.2
PING 172.17.0.2 (172.17.0.2) 56(84) bytes of data.
64 bytes from 172.17.0.2: icmp_seq=1 ttl=64 time=0.072 ms
64 bytes from 172.17.0.2: icmp_seq=2 ttl=64 time=0.050 ms
64 bytes from 172.17.0.2: icmp_seq=3 ttl=64 time=0.063 ms
64 bytes from 172.17.0.2: icmp_seq=4 ttl=64 time=0.049 ms  # linux 是可以 ping 通docker容器内部！

```

**原理**

> 我们每启动一个docker容器， docker就会给docker容器分配一个ip， 我们只要安装了docker，就会有一个网卡 docker0桥接模式，使用的技术是veth-pair技术！

**再次测试 ip addr**

```shell
[root@VM-8-2-centos /]# ip addr
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
2: eth0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc mq state UP group default qlen 1000
    link/ether 52:54:00:d9:02:62 brd ff:ff:ff:ff:ff:ff
    inet 10.0.8.2/22 brd 10.0.11.255 scope global eth0
       valid_lft forever preferred_lft forever
    inet6 fe80::5054:ff:fed9:262/64 scope link 
       valid_lft forever preferred_lft forever
3: docker0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP group default 
    link/ether 02:42:b3:23:5e:26 brd ff:ff:ff:ff:ff:ff
    inet 172.17.0.1/16 brd 172.17.255.255 scope global docker0
       valid_lft forever preferred_lft forever
    inet6 fe80::42:b3ff:fe23:5e26/64 scope link 
       valid_lft forever preferred_lft forever
75: veth6401276@if74: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue master docker0 state UP group default 
    link/ether 76:ab:0f:dc:55:1b brd ff:ff:ff:ff:ff:ff link-netnsid 0
    inet6 fe80::74ab:fff:fedc:551b/64 scope link 
       valid_lft forever preferred_lft forever # 启动一个容器 多了一对网卡

```

**再启动一个容器测试， 发现又多了一对网卡**

```shell
[root@VM-8-2-centos /]# docker run -d --name tomcat02 -P 009259e972e1
48e4345d0a488d9206054085f839f147ed7a76d955c1b4770579e924b720995c
[root@VM-8-2-centos /]# ip addr
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host 
       valid_lft forever preferred_lft forever
2: eth0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc mq state UP group default qlen 1000
    link/ether 52:54:00:d9:02:62 brd ff:ff:ff:ff:ff:ff
    inet 10.0.8.2/22 brd 10.0.11.255 scope global eth0
       valid_lft forever preferred_lft forever
    inet6 fe80::5054:ff:fed9:262/64 scope link 
       valid_lft forever preferred_lft forever
3: docker0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP group default 
    link/ether 02:42:b3:23:5e:26 brd ff:ff:ff:ff:ff:ff
    inet 172.17.0.1/16 brd 172.17.255.255 scope global docker0
       valid_lft forever preferred_lft forever
    inet6 fe80::42:b3ff:fe23:5e26/64 scope link 
       valid_lft forever preferred_lft forever
75: veth6401276@if74: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue master docker0 state UP group default 
    link/ether 76:ab:0f:dc:55:1b brd ff:ff:ff:ff:ff:ff link-netnsid 0
    inet6 fe80::74ab:fff:fedc:551b/64 scope link 
       valid_lft forever preferred_lft forever
77: vethf485f03@if76: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue master docker0 state UP group default 
    link/ether 6e:a3:e0:d6:03:06 brd ff:ff:ff:ff:ff:ff link-netnsid 1
    inet6 fe80::6ca3:e0ff:fed6:306/64 scope link 
       valid_lft forever preferred_lft forever # 再启动一个容器 多了一对网卡

```

```shell
# 我们发现这个容器带来网卡，都是一对对的
# veth-pair 就是一对的虚拟设备接口，他们都是成对出现的，一端连着协议，一端彼此相连
# 正因为有这个特性，veth-pair充当一个桥梁， 连接各种虚拟网络设备
# OpenStac， Docker容器之间的链接，OVS的链接， 都是使用veth-pair技术
```

**我们测试一下tomcat01和tomcat02之间是否可以ping通！**

```shell
[root@VM-8-2-centos /]# docker inspect -f '{{.Name}} - {{.NetworkSettings.IPAddress }}' $(docker ps -aq) # 查看容器ip
/tomcat02 - 172.17.0.3
/tomcat01 - 172.17.0.2
[root@VM-8-2-centos /]# docker exec -it tomcat02 ping 172.17.0.2 #在容器2里面去ping 容器1的地址 可以ping通
PING 172.17.0.2 (172.17.0.2) 56(84) bytes of data.
64 bytes from 172.17.0.2: icmp_seq=1 ttl=64 time=0.070 ms
64 bytes from 172.17.0.2: icmp_seq=2 ttl=64 time=0.062 ms
64 bytes from 172.17.0.2: icmp_seq=3 ttl=64 time=0.076 ms
64 bytes from 172.17.0.2: icmp_seq=4 ttl=64 time=0.081 ms

```

**结论：容器与容器之间是可以相互ping通的！**

网络模型图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200814101617604.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70#pic_center)



结论：tomcat01和tomcat02是共用的一个路由器，docker0

所有容器不指定网络的情况下，都是docker0路由的，doucker会给我们的容器分配一个默认的可用IP

Docker使用的是Linux的桥接，宿主机中是一个Docker容器的网桥docker0.

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200814102155735.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70#pic_center)

Docker中的所有的网络接口都是虚拟的，虚拟的转发效率高！（内网传递文件！）

只要容器删除，对应的网桥一对就没有了！

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200814103808900.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70#pic_center)

## -- link

> 思考一个场景，我们编写了一个微服务，database url =ip； 项目不重启，数据ip换掉了，
>
>   我们希望可以处理这个问题，`可以按名字来进行访问容器`?

```shell
[root@VM-8-2-centos /]# docker exec -it tomcat02 ping tomcat01
ping: tomcat01: Name or service not known

# 如何可以解决呢？
# 通过--link既可以解决网络连通问题
[root@VM-8-2-centos /]# docker run -d --name tomcat03 --link tomcat02 009259e972e1 # 创建容器时关联tomcat02

[root@VM-8-2-centos /]# docker exec -it tomcat03 ping tomcat02  #就可以ping 通
PING tomcat02 (172.17.0.3) 56(84) bytes of data.
64 bytes from tomcat02 (172.17.0.3): icmp_seq=1 ttl=64 time=0.076 ms
64 bytes from tomcat02 (172.17.0.3): icmp_seq=2 ttl=64 time=0.077 ms
64 bytes from tomcat02 (172.17.0.3): icmp_seq=3 ttl=64 time=0.060 ms


# 反向可以ping通吗？
[root@VM-8-2-centos /]# docker exec -it tomcat02 ping tomcat03
ping: tomcat03: Name or service not known
```

**探究：inspect！**

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200814104403761.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70#pic_center)



**其实这个tomcat03就是在本地配置了tomcat02的配置**

```shell
[root@VM-8-2-centos /]# docker exec -it tomcat03 cat /etc/hosts
127.0.0.1	localhost
::1	localhost ip6-localhost ip6-loopback
fe00::0	ip6-localnet
ff00::0	ip6-mcastprefix
ff02::1	ip6-allnodes
ff02::2	ip6-allrouters
172.17.0.3	tomcat02 48e4345d0a48
172.17.0.4	45419ceaa85f
```

本质探究：--link 就是我们在hosts配置中增加了一个 172.17.0.3   tomcat02   48e4345d0a48,可以解析出ip

我们现在玩Docker已经不建议使用--link了！

自定义网络！不使用Docker0！

Docker0的问题：它不支持容器名链接访问！

## 自定义网络

> 查看所有的docker网络

```shell
[root@VM-8-2-centos /]# docker network ls
NETWORK ID     NAME      DRIVER    SCOPE
fcb3e5e75cb5   bridge    bridge    local
b3064a98cd0b   host      host      local
be8844dcaa25   none      null      local

```

**网络模式**

- bridge： 桥接模式，桥接 docker 默认，自己创建的也是用brdge模式
- none： 不配置网络
- host： 和宿主机共享网络
- container：容器网络连通！（用的少， 局限很大）

```shell
# 我们直接启动的命令默认有一个 --net bridge，而这个就是我们的docker0 默认是忽略的
docker run -d -P --name tomcat01 tomcat
docker run -d -P --name tomcat01 --net bridge tomcat
 
# docker0特点，默认，容器名不能访问， --link可以打通连接！

# 我们可以自定义一个网络！
# --driver bridge
# --subnet 192.168.0.0/16 可以支持255*255个网络 192.168.0.2 ~ 192.168.255.254  子网
# --gateway 192.168.0.1  路由
[root@iZ2zeg4ytp0whqtmxbsqiiZ ~]# docker network create --driver bridge --subnet 192.168.0.0/16 --gateway 192.168.0.1 mynet
26a5afdf4805d7ee0a660b82244929a4226470d99a179355558dca35a2b983ec
[root@iZ2zeg4ytp0whqtmxbsqiiZ ~]# docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
30d601788862        bridge              bridge              local
226019b14d91        host                host                local
26a5afdf4805        mynet               bridge              local
7496c014f74b        none                null                local

# 我们自己创建的网络就ok了
```



```shell
[root@VM-8-2-centos /]# docker inspect mynet
[
    {
        "Name": "mynet",
        "Id": "2ee628965c1a78e89f19d75064ca8f153d8f845e8e6bfa2e7ae945aa4fd8a999",
        "Created": "2023-02-14T16:31:32.457081976+08:00",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "192.168.0.0/16",
                    "Gateway": "192.168.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {},
        "Options": {},
        "Labels": {}
    }
]
```

**在自己创建的网络里面启动两个容器**

```shell
[root@VM-8-2-centos /]# docker run -d -P --name tomcat-net-01 --net mynet 009259e972e1
035ef894bd5f7cc82796f83b576a8db9a6eabd8ea1abed665daed1077c87e8a9
[root@VM-8-2-centos /]# docker run -d -P --name tomcat-net-02 --net mynet 009259e972e1
66972174a4ac5fcc43cccbf948d1eb3fc6d9e09ba7dcad2d0a598efe3a846baa

# 查看网络信息
[root@VM-8-2-centos /]# docker inspect mynet
[
    {
        "Name": "mynet",
        "Id": "2ee628965c1a78e89f19d75064ca8f153d8f845e8e6bfa2e7ae945aa4fd8a999",
        "Created": "2023-02-14T16:31:32.457081976+08:00",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "192.168.0.0/16",
                    "Gateway": "192.168.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "035ef894bd5f7cc82796f83b576a8db9a6eabd8ea1abed665daed1077c87e8a9": {
                "Name": "tomcat-net-01",
                "EndpointID": "0232119a35e74ccad66bbfa68796ebadb25b540f5e9dc0956d9ee1b56a32f3c3",
                "MacAddress": "02:42:c0:a8:00:02",
                "IPv4Address": "192.168.0.2/16",
                "IPv6Address": ""
            },
            "66972174a4ac5fcc43cccbf948d1eb3fc6d9e09ba7dcad2d0a598efe3a846baa": {
                "Name": "tomcat-net-02",
                "EndpointID": "27226d704e538445c5a5561b360407f5487177f1badd209de5b9d058ee44d3d9",
                "MacAddress": "02:42:c0:a8:00:03",
                "IPv4Address": "192.168.0.3/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]

# 再次拼连接
[root@VM-8-2-centos /]# docker exec -it tomcat-net-01 ping 192.168.0.3
PING 192.168.0.3 (192.168.0.3) 56(84) bytes of data.
64 bytes from 192.168.0.3: icmp_seq=1 ttl=64 time=0.089 ms
64 bytes from 192.168.0.3: icmp_seq=2 ttl=64 time=0.061 ms
64 bytes from 192.168.0.3: icmp_seq=3 ttl=64 time=0.069 ms
64 bytes from 192.168.0.3: icmp_seq=4 ttl=64 time=0.062 ms
64 bytes from 192.168.0.3: icmp_seq=5 ttl=64 time=0.062 ms
# 现在不使用 --link也可以ping名字了！
```

我们自定义的网络docker都已经帮我们维护好了对应的关系，推荐我们平时这样使用网络

好处：

- redis - 不同的集群使用不同的网络，保证集群时安全和健康的
- mysql - 不同的集群使用不同的网络，保证集群时安全和健康的

## 网络连通 connect

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200814114621170.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70#pic_center)



**测试打通tomcat01 和mynet**

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020081411482318.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70#pic_center)



```shell
[root@iZ2zeg4ytp0whqtmxbsqiiZ ~]# docker network connect  mynet tomcat01
 
# 连通之后就是将 tomcat01 放到了mynet网路下
# 一个容器两个ip地址：
# 阿里云服务器，公网ip，私网ip
```

```shell
[root@VM-8-2-centos /]# docker inspect mynet
[
    {
        "Name": "mynet",
        "Id": "2ee628965c1a78e89f19d75064ca8f153d8f845e8e6bfa2e7ae945aa4fd8a999",
        "Created": "2023-02-14T16:31:32.457081976+08:00",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "192.168.0.0/16",
                    "Gateway": "192.168.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "035ef894bd5f7cc82796f83b576a8db9a6eabd8ea1abed665daed1077c87e8a9": {
                "Name": "tomcat-net-01",
                "EndpointID": "0232119a35e74ccad66bbfa68796ebadb25b540f5e9dc0956d9ee1b56a32f3c3",
                "MacAddress": "02:42:c0:a8:00:02",
                "IPv4Address": "192.168.0.2/16",
                "IPv6Address": ""
            },
            "1aa57176a0c93df05623250fe48cb25ead3c9673051d21efe2cf3bda38da7829": {
                "Name": "tomcat01",
                "EndpointID": "c4e0ad335fb3ce9b5d965d94b3105ab588bf94bdb787a0254fdccda5c3d5f6f6",
                "MacAddress": "02:42:c0:a8:00:04",
                "IPv4Address": "192.168.0.4/16",
                "IPv6Address": ""
            },
            "66972174a4ac5fcc43cccbf948d1eb3fc6d9e09ba7dcad2d0a598efe3a846baa": {
                "Name": "tomcat-net-02",
                "EndpointID": "27226d704e538445c5a5561b360407f5487177f1badd209de5b9d058ee44d3d9",
                "MacAddress": "02:42:c0:a8:00:03",
                "IPv4Address": "192.168.0.3/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
# tomcat01 也赋予了一个自定义的网络地址
# 连通ok
[root@VM-8-2-centos /]# docker exec -it tomcat01 ping tomcat-net-01
PING tomcat-net-01 (192.168.0.2) 56(84) bytes of data.
64 bytes from tomcat-net-01.mynet (192.168.0.2): icmp_seq=1 ttl=64 time=0.077 ms
64 bytes from tomcat-net-01.mynet (192.168.0.2): icmp_seq=2 ttl=64 time=0.064 ms
64 bytes from tomcat-net-01.mynet (192.168.0.2): icmp_seq=3 ttl=64 time=0.077 ms

# tomcat02 依旧无法连通 因为没有建立connect
[root@VM-8-2-centos /]# docker exec -it tomcat02 ping tomcat-net-01
ping: tomcat-net-01: Name or service not known

```

**结论：假设要跨网络 操作别人，就要使用docker network connect连通.....!**

## 网络个人总结

> 同一个路由下的容器是可以相互访问的，这几种网络能连接最终都要把他们搞到同一个路由下

1、 Docker0它不支持容器名链接访问，只能支持同一个路由下的容器互相访问。（ip变化时就需要改动代码），希望可以通过名称访问

2、--link 就是我们在hosts配置中增加了一个 容器ip到当前容器的一个映射（已经不建议使用）

3、自定义网络 就是自己定义好一个路由以及子网的范围，创建容器时使用自定义网络。由于是同一个路由，所以使用此网络的容器都是互通的，如果想要此路由之外的其他容器和本路由内的容器ping通，需要使用connect指令。

  该指令实际上就是把要连通的外部容器加一个映射ip 在此路由下。**`也就是这一个容器两个ip地址`**

# SpringBoot微服务打包Docker镜像

1、开发项目，打成jar

**如下demo**

```java
package com.example.testbuild.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/kangkang")
    public String getResult(){
     return "康康真帅! 牛P";
    }
}
```

2、linux下输入 `rz`命令,上传jar到linux服务器上

3、编写Dockerfile文件

```shell
FROM openjdk:8
 
COPY *.jar /app.jar
CMD ["--server.port=8080"]
 
EXPOSE 8080
 
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

4、执行build命令    docker build 

```shell
# 构建
[root@VM-8-2-centos ~]# docker build --tag diyapp:v1 ./
Sending build context to Docker daemon  17.72MB
Step 1/5 : FROM openjdk:8
8: Pulling from library/openjdk
001c52e26ad5: Pull complete 
d9d4b9b6e964: Pull complete 
2068746827ec: Pull complete 
9daef329d350: Pull complete 
d85151f15b66: Pull complete 
52a8c426d30b: Pull complete 
8754a66e0050: Pull complete 
Digest: sha256:86e863cc57215cfb181bd319736d0baf625fe8f150577f9eb58bd937f5452cb8
Status: Downloaded newer image for openjdk:8
 ---> b273004037cc
Step 2/5 : COPY *.jar /app.jar
 ---> 3ab8e48b0dcb
Step 3/5 : CMD ["--server.port=8080"]
 ---> Running in ed9d65684e55
Removing intermediate container ed9d65684e55
 ---> 329c24c7c59b
Step 4/5 : EXPOSE 8080
 ---> Running in d441eef39377
Removing intermediate container d441eef39377
 ---> 42f0a25dfa99
Step 5/5 : ENTRYPOINT ["java", "-jar", "/app.jar"]
 ---> Running in 190b03b2f540
Removing intermediate container 190b03b2f540
 ---> a6fec0015718
Successfully built a6fec0015718
Successfully tagged diyapp:v1
# 查看生成的镜像
[root@VM-8-2-centos ~]# docker images
REPOSITORY              TAG       IMAGE ID       CREATED         SIZE
diyapp                  v1        a6fec0015718   3 minutes ago   544MB  # 成功生成镜像
mycentos                v1        748e6f5d9065   6 hours ago     231MB
zhangjj007/diy_tomcat   v1        009259e972e1   25 hours ago    478MB
nginx                   latest    3f8a00f137a0   5 days ago      142MB
mysql                   latest    7484689f290f   2 months ago    538MB
openjdk                 8         b273004037cc   6 months ago    526MB
centos                  latest    5d0da3dc9764   17 months ago   231MB

```

5、发布镜像

```shell
# 启动容器
[root@VM-8-2-centos ~]# docker run -d -p 8080:8080 --name app01 diyapp:v1
bc475ad610c759b6abff16ccd2d935617db1cc9b33a5ac12a03432bfae3c570f
[root@VM-8-2-centos ~]# docker ps
CONTAINER ID   IMAGE       COMMAND                  CREATED         STATUS         PORTS                                       NAMES
bc475ad610c7   diyapp:v1   "java -jar /app.jar …"   5 seconds ago   Up 4 seconds   0.0.0.0:8080->8080/tcp, :::8080->8080/tcp   app01
#测试是否启动成功
[root@VM-8-2-centos ~]# curl localhost:8080/kangkang
康康真帅! 牛P[root@VM-8-2-centos ~]#  成功返回接口内容

```

# Docker Compose

## 概念

> Compose是一个用于**定义和运行多容器**Docker应用程序的**工具**。
>
> 使用Compose，您可以使用yaml文件配置应用程序的服务。然后，使用一个命令，从配置中创建并启动所有服务

## **执行三步骤：**

- Define your app’s environment with a Dockerfile so it can be reproduced anywhere.
       **Dockerfile保证我们的项目再任何地方可以运行**
- Define the services that make up your app in docker-compose.yml so they can be run together in an isolated environment.
       **services 什么是服务。**
- Run docker-compose up and Compose starts and runs your entire app.
      **启动项目**

---

Compose是Docker官方的开源项目，需要安装！

`Dockerfile`让程序在任何地方运行。web服务、redis、mysql、nginx... 多个容器。 run

Compose

```yml
version: '2.0'
services:
  web:
    build: .
    ports:
    - "5000:5000"
    volumes:
    - .:/code
    - logvolume01:/var/log
    links:
    - redis
  redis:
    image: redis
volumes:
  logvolume01: {}
```
























































































































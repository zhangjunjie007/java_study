# linux 常用命令

## 查看服务进程

```sql
--查看musql 服务是否启动
    service mysqld status
```

## 文件编辑相关

`打开文件：`

​            vi　　aaa.conf

`编辑：`

​              i

**编辑结束，按ESC 键 跳到命令模式，然后输入退出命令：**

- :w　　（write）保存文件但不退出vi 编辑

- :w!　　强制保存，不退出vi 编辑

- :w file 　　将修改另存到file中，不退出vi 编辑

- :wq 　　（write and quite）保存文件并退出vi 编辑

- :wq! 　　强制保存文件并退出vi 编辑

- q: 　　（quite）不保存文件并退出vi 编辑

- :q! 　　不保存文件并强制退出vi 编辑

- :e! 　　放弃所有修改，从上次保存文件开始在编辑

## 找不到IP时

**首先关闭网络服务**

>  systemctl stop NetworkManager

然后**启动网络服务**

> systemctl start network.service

或者`直接重启`：

> ​    service network restart
>
> **重启失败时，用上面两种方式**

## 火墙相关

 查看防火墙状态

>  service iptables status  

 暂时关闭防火墙。，重启后打开

>  service iptables stop

重启防火墙

>  systemctl restart iptables

## 服务重启

[linux重启](https://so.csdn.net/so/search?q=linux重启&spm=1001.2101.3001.7020)mysql的方法：

1、直接使用“service mysqld restart”或“service mysql restart”命令重启；

2、使用“/etc/init.d/mysqld restart”命令重启。



## 查看已知端口占用情况

比如，我们想知道[8080端口](https://so.csdn.net/so/search?q=8080端口&spm=1001.2101.3001.7020)的使用情况，或者说被谁占用了，命令如下：

```bash
 netstat -anp | grep 8080
```

查看全部端口占用情况

```bash
netstat -anp
# 或者
netstat -tln
```

# idea 连接docker注意事项

> **火墙不能是关闭状态，要不连接不上**

- ​     开启火墙：  systemctl start firewalld
- ​     开放端口号： firewall-cmd --permanent --zone=public --add-port=2375/tcp
- ​    重新加载火墙： firewall-cmd --reload
- ​    查看火墙放行的端口：firewall-cmd --permanent --zone=public --list-ports





# 各个文件夹的作用

1.  /bin `【常用】`(/usr/bin 、 /usr/local/bin)
       是 Binary 的缩写, 这个目录存放着最经常使用的命令

2. /sbin    (/usr/sbin 、 /usr/local/sbin)
        s 就是 Super User 的意思，这里存放的是系统管理员使用的系统管理程序。

3. /home  [常用]
        存放普通用户的主目录，在 Linux 中每个用户都有一个自己的目录，一般该目录名是以用户的账号命名

4. /root   [常用]
       该目录为系统管理员，也称作超级权限者的用户主目录

5. /lib 

      系统开机所需要最基本的动态连接共享库，其作用类似于 Windows 里的 DLL 文件。几乎所有的应用程序都需要用到这些共享库

6. /lost+found  这个目录一般情况下是空的，当系统非法关机后，这里就存放了一些文件

7. /etc [常用]
      所有的系统管理所需要的配置文件和子目录, 比如安装 mysql 数据库 my.conf

8. /usr     [常用]
      这是一个非常重要的目录，用户的很多应用程序和文件都放在这个目录下，类似与 windows 下的 program files 目录。

9. /boot [常用]   

     存放的是启动 Linux 时使用的一些核心文件，包括一些连接文件以及镜像文件

10. /proc [不能动] 

      这个目录是一个虚拟的目录，它是系统内存的映射，访问这个目录来获取系统信息

11. /srv [不能动]
    service 缩写，该目录存放一些服务启动之后需要提取的数据

12. /sys
      [不能动]这是 linux2.6 内核的一个很大的变化。该目录下安装了 2.6 内核中新出现的一个文件系统 sysfs =》【别动】

13. /tmp 这个目录是用来存放一些临时文件的

14. /dev
      类似于 windows 的设备管理器，把所有的硬件用文件的形式存储

15. /media [常用] 

     linux 系统会自动识别一些设备，例如 U 盘、光驱等等，当识别后，linux 会把识别的设备挂载          到这个目录下

16. /mnt [常用]
    系统提供该目录是为了让用户临时挂载别的文件系统的，我们可以将外部的存储挂载在/mnt/上，然后进入该目录就可以查看里的内容了。       d:/myshare                               

17. /opt
     这是给主机额外安装软件所存放的目录。如安装 ORACLE 数据库就可放到该目录下。默认为空

18. /usr/local  [常用]
    这是另一个给主机额外安装软件所安装的目录。一般是通过编译源码方式安装的程序

19. /var [常用]
    这个目录中存放着在不断扩充着的东西，习惯将经常被修改的目录放在这个目录下。包括各种日志文件

20. /selinux [security-enhanced linux]
    SELinux 是一种安全子系统,它能控制程序只能访问特定文件, 有三种工作模式，可以自行设置.

# 排查线上问题必须要掌握这几个 Linux 命令?
1. top 查看整个系统资源使用情况;
2. free -m 查看内存使用情况;
3. iostat 查看磁盘读写活动情况;
4. netstat 查看网络连接情况;
5. df -h 查看磁盘空间使用情况;
6. du -sh 查看文件大小情况;

# CPU冲高排查步骤

> 如果使用的docker启动的服务，先进入容器内部以后，再进行分析cpu占用率，否则的话类似jstack外部的linux无法识别（因为外部环境没有装jdk）
>
> ```shell
> [root@VM-8-2-centos ~]# docker exec -it 562bf478b51c bash   ## 进入容器内部
> root@562bf478b51c:/# 
> ```
>
> 

## 1、top 命令查看占用最多的那个进程

```shell
root@562bf478b51c:/# top
top - 08:57:33 up 44 days,  1:05,  0 users,  load average: 2.12, 2.18, 1.63
Tasks:   4 total,   1 running,   3 sleeping,   0 stopped,   0 zombie
%Cpu(s): 99.7 us,  0.3 sy,  0.0 ni,  0.0 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
MiB Mem :   1998.8 total,     78.0 free,    389.7 used,   1531.1 buff/cache
MiB Swap:      0.0 total,      0.0 free,      0.0 used.   1416.2 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND                                                                                                                                     
    1 root      20   0 2920708 198460  13040 S 199.0   9.7  39:26.65 java                                                                                                                                        
   42 root      20   0    5976   2228   1720 S   0.0   0.1   0:00.02 bash                                                                                                                                        
   75 root      20   0    5976   2064   1636 S   0.0   0.1   0:00.02 bash                                                                                                                                        
   81 root      20   0    8820   1936   1408 R   0.0   0.1   0:00.00 top   
```

> 如上结果，1 进程cpu占用将近200%

## 2、jstack 输出进程信息

```shell
# 在容器内部 找到合适的位置 打印堆栈信息
root@562bf478b51c:/tmp# cd /var/
root@562bf478b51c:/var# ls
backups  cache	lib  local  lock  log  mail  opt  run  spool  tmp
# 把进程1的堆栈信息进行打印
root@562bf478b51c:/var# jstack 1 > show.txt

```

## 3、top -p 1 -H 查看占用资源最多的线程

```shell
root@562bf478b51c:/var# top -p 1 -H
top - 09:02:26 up 44 days,  1:10,  0 users,  load average: 2.38, 2.19, 1.78
Threads:  29 total,   2 running,  27 sleeping,   0 stopped,   0 zombie
%Cpu(s): 99.5 us,  0.5 sy,  0.0 ni,  0.0 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
MiB Mem :   1998.8 total,     87.6 free,    389.0 used,   1522.2 buff/cache
MiB Swap:      0.0 total,      0.0 free,      0.0 used.   1416.8 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND                                                                                                                                     
   26 root      20   0 2920708 198460  13040 R  99.3   9.7  24:40.37 http-nio-8080-e                                                                                                                             
   29 root      20   0 2920708 198460  13040 R  99.0   9.7  24:15.17 http-nio-8080-e                                                                                                                             
   17 root      20   0 2920708 198460  13040 S   0.3   9.7   0:01.87 VM Periodic Tas                                                                                                                             
    1 root      20   0 2920708 198460  13040 S   0.0   9.7   0:00.03 java                                                                                                                                        
    7 root      20   0 2920708 198460  13040 S   0.0   9.7   0:03.41 java           
```

> jstack 打印的信息都是16进制，所以要把这里的pid 转为16进制，来进行搜索

```shell
root@562bf478b51c:/var#  printf "%x\n" 26
1a                   # 把26转为16进制以后是1a
```

## 4、jstack信息内搜索 026对应的相关信息

![在这里插入图片描述](https://img-blog.csdnimg.cn/83a775e9cdb344e1ba58a78b599ab095.png)

> 总结：
>
> 第一步，top 先看看是哪个进程 找到 cpu占用最高的，如果是 java 然后用，
>
> 第二步：jstat 进程 id 得到当前进程下，所有运行的线程，然后找到占用最高的线程，
>
> ​               jstack <pid>  > stack.log
>
> 第三步：通过进程 id 得到它的子线程 id
>
> ​                top -p <pid> -H
>
> 第三步：然后把线程 id 转成16进制字符串，
>
> ​                 printf "%x" <thread_id>
>
> 第四步：stack.log 查看线程 id (16 进制字符)
>
> ​                 less stack.log
>
> 死锁信息在 stack.log 最下面






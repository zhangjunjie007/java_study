# Mybatis 学习整理

## 表数据准备

```sql
-- 创建数据库
CREATE DATABASE mybatis_study;
-- 查看数据库
show DATABASES;
-- 展示字符集
SHOW CHARACTER SET;
-- 删除表
drop table `USER`;

-- 虚拟机登陆，操作数据库  其中root 是用户名  回车以后会输入密码
# mysql -uroot -p


-- ctrl + D
-- 退出 sql链接窗口

-- 基础建表语句
create table `user`(
  `id` int(20) not null AUTO_INCREMENT PRIMARY key ,
	`name` VARCHAR(30) ,
	`address` VARCHAR(100) 
)ENGINE = INNODB DEFAULT CHARSET=utf8mb4;

```

> utf8能兼容绝大部分的字符，为什么要扩展utf8mb4

![img](https://img-blog.csdnimg.cn/20200821105004444.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI3NjEwNjQ3,size_16,color_FFFFFF,t_70)

​      随着互联网的发展，产生了许多新类型的字符，也就是我们通常在聊天时发的`小黄脸表情`（***四个字节存储\***），所以，设计数据库时如果想要允许用户使用特殊符号，最好使用`utf8mb4`编码来存储，使得数据库有更好的兼容性，但是这样设计会导致耗费更多的存储空间。

## 入门

> pom 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>mybatis-study</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>2.7.0</version>
        </dependency>

        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.6</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.46</version>
        </dependency>

        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.12</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>RELEASE</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
            </resource>
        </resources>
    </build>
</project>

```

**获取SqlSession**

`构建SqlSessionFactory    ===》 SqlSession`

> 创建简单工具类

```java
package com.zjj.study.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

public class MybatisUtil {
    private  static SqlSessionFactory sqlSessionFactory ;
    static {
        try {
            String resource = "./mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static SqlSession  getSqlSession(){
        return sqlSessionFactory.openSession();
    }
}

```



## XML配置

### 配置文件 mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "https://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <properties resource="db.properties"/>
    <typeAliases>
        <typeAlias type="com.zjj.study.pojo.User" alias="user"/>
    </typeAliases>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <!--<mapper class="com.zjj.study.dao.UserMapper"/>-->
       <package name="com.zjj.study.dao"/>
    </mappers>

</configuration>

```

**`注意`**

>  mybatis-config.xml中标签是有顺序的，不能随意调换顺序

![image-20220925213407893](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220925213407893.png)

   **顺序不对会飘红，报错**

**数据库配置**

```properties
# 这里配置的是虚拟机的ip 以及上面的数据库
driver = com.mysql.jdbc.Driver
url =jdbc:mysql://192.168.59.128:3306/mybatis_study?useSSL=false&amp;useUnicode=true&amp;characterEncoding=utf8mb4&amp;serverTimezone=UTC&amp
username = root
password = root
```

**Demo如下**

```java
package com.zjj.study.dao;

import com.zjj.study.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {
     List<User> getUserList() ;

     User getUserById(int id);

     void updateUserById(@Param("id") int id, @Param("name") String name);

     void updateUser(User u);

     int insertUser(User u);

     int insertUser2(Map p);

     List<User> getUserMatch( @Param("name")String name);

     void delUser(int id);
}

```

**对应的mapper**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "https://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zjj.study.dao.UserMapper">

    <select id="getUserList" resultType="user">
        select * from user
    </select>

    <select id="getUserMatch" parameterType="string" resultType="com.zjj.study.pojo.User">
        <!--select * from user where name like #{name} 此种方式需要调用处手动拼接%  -->
        select * from user where name like "%${name}%" <!--此种方式不能预防sql注入-->
    </select>

    <!-- 根据id获取一个用户-->
    <select id="getUserById" parameterType="int" resultType="com.zjj.study.pojo.User">
        select * from user where id =#{id}
    </select>

    <!-- 根据id更新用户-->
    <update id="updateUserById" >
        update user set name = #{name} where id = #{id}
    </update>

    <!--根据实体对象去更新-->
    <update id="updateUser" parameterType="com.zjj.study.pojo.User">
        update user set address = #{address} where id = #{id}
    </update>

    <!--插入数据-->
    <insert id="insertUser" parameterType="com.zjj.study.pojo.User" useGeneratedKeys="true" keyProperty="id" >
        insert into user(name,address) values(#{name},#{address})
    </insert>

    <!--插入数据 map方式 begin-->
    <insert id="insertUser2" parameterType="map" >
        insert into user(name,address) values(#{aaa},#{bbb})
    </insert>

    <!--插入数据 map方式 end-->
    <!--删除数据-->
    <delete id="delUser" parameterType="int" >
        delete  from user where id = #{id}
    </delete>
</mapper>
```

**测试类进行测试**

```java
package com.zjj.study;

import com.zjj.study.dao.UserMapper;
import com.zjj.study.pojo.User;
import com.zjj.study.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTest {
    SqlSession sqlSession = MybatisUtil.getSqlSession();
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    @After
    public void mustDoIt(){
       sqlSession.close();
    }
    @Test
    public void testGetUserList(){
        List<User> userList = mapper.getUserList();
        System.out.println(userList);
    }
    @Test
    public void testGetUserById(){
        User user = mapper.getUserById(1);
        System.out.println(user);
    }
    @Test
    public void testUpdateUserById(){
         mapper.updateUserById(12,"一二一");
      //  System.out.println(user);
    }
    @Test
    public void testUpdateUser(){
        mapper.updateUser(new User(1,"大问问","你猜"));
        //  System.out.println(user);
    } //
    @Test
    public void testInsertUser(){
        User user = new User(12, "大问问", "你猜");
        int i = mapper.insertUser(user);
        List<User> userList = mapper.getUserList();
        System.out.println(userList);
    }
    @Test
    public void testInsertUser2(){
        Map paramMap = new HashMap<>();
        paramMap.put("aaa","二腿子子");
        paramMap.put("bbb","甘肃李家寺的");
        int i = mapper.insertUser2(paramMap);

        //区别
       // Map 传递参数，直接在sql中取出key就行
       // 对象传递参数，需要在sql中取出对象的属性
        // 只有一个基本类型参数的情况下 可以直接在sql中取到  也就是直接拿入参的形参
        //多个参数用map 或者注解 或者类
        List<User> userList = mapper.getUserList();
        System.out.println(userList);
    }
    @Test
    public void testDelUser(){
        mapper.delUser(2);
        List<User> userList = mapper.getUserList();
        System.out.println(userList);
    }


    @Test
    public void testGetUserMatch(){
        List<User> userList1 = mapper.getUserMatch("%一%");/*对应的xml #{} 占位符方式 */
        List<User> userList = mapper.getUserMatch("一");/*对应xml中 ${} 方式 有sql注入的风险*/
        System.out.println(userList);
    }
}

```



### 别名(typeAliases)

**方式一：**

> 类型别名可为 Java 类型设置一个缩写名字

```java

<typeAliases>
  <typeAlias alias="Author" type="domain.blog.Author"/>
  <typeAlias alias="Blog" type="domain.blog.Blog"/>
  <typeAlias alias="Comment" type="domain.blog.Comment"/>
  <typeAlias alias="Post" type="domain.blog.Post"/>
  <typeAlias alias="Section" type="domain.blog.Section"/>
  <typeAlias alias="Tag" type="domain.blog.Tag"/>
</typeAliases>
```

**方式二**

> 指定一个包名，MyBatis 会在包名下面搜索需要的 Java Bean
>
> 每一个在包 `domain.blog` 中的 Java Bean，在没有注解的情况下，会使用 Bean 的首字母小写的非限定类名来作为它的别名
>
> ​        比如 `domain.blog.Author` 的别名为 `author`；
>
> 若有注解，则别名为其注解值。见下面的例子：
>
> ```java
> @Alias("author")
> public class Author {
>     ...
> }
> ```

```java
<typeAliases>
  <package name="domain.blog"/>
</typeAliases>
```

**下面是一些为常见的 Java 类型内建的类型别名。它们都是不区分大小写的，注意，为了应对原始类型的命名重复，采取了特殊的命名风格。**

|           别名            |  映射的类型  |
| :-----------------------: | :----------: |
|           _byte           |     byte     |
|   _char (since 3.5.10)    |     char     |
| _character (since 3.5.10) |     char     |
|           _long           |     long     |
|          _short           |    short     |
|           _int            |     int      |
|         _integer          |     int      |
|          _double          |    double    |
|          _float           |    float     |
|         _boolean          |   boolean    |
|          string           |    String    |
|           byte            |     Byte     |
|    char (since 3.5.10)    |  Character   |
| character (since 3.5.10)  |  Character   |
|           long            |     Long     |
|           short           |    Short     |
|            int            |   Integer    |
|          integer          |   Integer    |
|          double           |    Double    |
|           float           |    Float     |
|          boolean          |   Boolean    |
|           date            |     Date     |
|          decimal          |  BigDecimal  |
|        bigdecimal         |  BigDecimal  |
|        biginteger         |  BigInteger  |
|          object           |    Object    |
|          date[]           |    Date[]    |
|         decimal[]         | BigDecimal[] |
|       bigdecimal[]        | BigDecimal[] |
|       biginteger[]        | BigInteger[] |
|         object[]          |   Object[]   |
|            map            |     Map      |
|          hashmap          |   HashMap    |
|           list            |     List     |
|         arraylist         |  ArrayList   |
|        collection         |  Collection  |
|         iterator          |   Iterator   |

### 映射器（mappers）

> 既然 MyBatis 的行为已经由上述元素配置完了，我们现在就要来定义 SQL 映射语句了。 但首先，我们需要告诉 MyBatis 到哪里去找到这些语句。 在自动查找资源方面，Java 并没有提供一个很好的解决方案，所以最好的办法是直接告诉 MyBatis 到哪里去找映射文件。 

![在这里插入图片描述](https://img-blog.csdnimg.cn/e2025609ac44477f865e0dcdebe09c74.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAbTBfOTk5OTk=,size_20,color_FFFFFF,t_70,g_se,x_16)

**方式一**

```xml
<!-- 使用相对于类路径的资源引用 -->
<mappers>
  <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
  <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
  <mapper resource="org/mybatis/builder/PostMapper.xml"/>
</mappers>

```

**可以通过target目录下查看文件是否编译生效**

**方式二**

```xml
<!-- 使用映射器接口实现类的完全限定类名 -->
<mappers>
  <mapper class="org.mybatis.builder.AuthorMapper"/>
  <mapper class="org.mybatis.builder.BlogMapper"/>
  <mapper class="org.mybatis.builder.PostMapper"/>
</mappers>

```

>  注意事项:
>
> - **sql映射接口文件与mapper接口名字要一致**
> - **sql接口文件与mapper接口所在包要一致**`(编译以后一致就可以)`

**可以把mapper文件放在java文件同一个文件夹下；**

**也可以在resource目录下创建同级目录，然后mapper文件会在编译后和对应的class在同一级；**

参考下图

![image-20220925195222481](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220925195222481.png)

***

**方式三**

```xml
<!-- 将包内的映射器接口全部注册为映射器 -->
<mappers>
  <package name="org.mybatis.builder"/>
</mappers>
```

**可以理解为是方式二的封装；也要满足`名称一致，所在包一致`，最终还是根据编译以后生成的target目录下的文件来确定的；**

```xml
src/
	com.mapper/
		mapperUser.class
		
		
resources/		(maven 的默认资源路径, 在resouce下目录符要用'.')
	com.mapper/
		mapperUser.xml
		
在项目启动 会自动归并为一处 例如↓
	src/
		com.mapper/
			mapperUser.class
			mapperUser.xml
```



> **注意：**
>
> ​    resource 目录下创建多级文件夹时，不能像java目录那样com.zjj.study  要 com/zjj/study

### settings---logImpl(日志)

mybatis-config.xml可以通过settings标签配置日志输出；

下面表格为部分settings 标签的属性

|       设置名       |                             描述                             |                            有效值                            | 默认值 |
| :----------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :----- |
|    cacheEnabled    |   全局性地开启或关闭所有映射器配置文件中已配置的任何缓存。   |                         true /false                          | true   |
| lazyLoadingEnabled | 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置 `fetchType` 属性来覆盖该项的开关状态。 |                         true /false                          | false  |
|      logImpl       |    指定 MyBatis 所用日志的具体实现，未指定时将自动查找。     | SLF4J \| LOG4J（3.5.9 起废弃） \| LOG4J2 \| JDK_LOGGING \| COMMONS_LOGGING \| `STDOUT_LOGGING` \| NO_LOGGING | 未设置 |

Mybatis内置的日志工厂提供日志功能，具体的日志实现有以下几种工具：

- SLF4J
- JDK_LOGGING
- LOG4J2
- LOG4J（3.5.9 起废弃）
- COMMONS_LOGGING
- `STDOUT_LOGGING`
-  NO_LOGGING



#### **STDOUT_LOGGING**

​    **STDOUT_LOGGING:标准日志输出**

```xml
<settings>
    <setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
```

![image-20220925214949192](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220925214949192.png)

#### LOG4J

>  1、导包

```xml
<!-- https://mvnrepository.com/artifact/log4j/log4j -->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

> 2、resource 目录下 增加log4j配置文件 log4j.properties

```xml
#将等级为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
log4j.rootLogger=DEBUG,console,file

#控制台输出的相关设置
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.Threshold=DEBUG
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=[%p][%d{yy-MM-dd}][%c]%m%n

#文件输出的相关设置
log4j.appender.file = org.apache.log4j.RollingFileAppender
log4j.appender.file.File=./log/zjjLoglog
log4j.appender.file.MaxFileSize=10mb
log4j.appender.file.Threshold=DEBUG
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=[%p][%d{yy-MM-dd}][%c]%m%n

#日志输出级别
log4j.logger.org.mybatis=DEBUG
log4j.logger.java.sql=DEBUG
log4j.logger.java.sql.Statement=DEBUG
log4j.logger.java.sql.ResultSet=DEBUG
log4j.logger.java.sql.PreparedStatement=DEBUG
```



> 3、使用

```xml
    <settings>
        <setting name="logImpl" value="LOG4J"/>
    </settings>
```

> 4、测试

![image-20220925221235963](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220925221235963.png)



**具体使用**

第一步：

> ```java
> Logger logger = Logger.getLogger(UserTest.class);
> ```

第二步：

> ```java
> //方式一
> logger.log(Level.INFO,"info:执行进来了。。。");
> logger.log(Level.DEBUG,"debugger:执行进来了。。。");
> logger.log(Level.ERROR,"error:执行进来了。。。");
> //方式二
> logger.info("info:执行进来了");
> logger.debug("debug:执行进来了");
> ```

注意：

>  logger日志的使用级别debug<-info<-error

以上只为初步使用，具体还要再探究





### ResultMap 结果集映射

#### result

当返回结果不能直接映射到Pojo实体类上的时候，可以通过此办法解决



![image-20220925205641066](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220925205641066.png)



如图：实体类和数据库字段对不上时，查询的结果映射到实体类时，不一致的字段为空

> 解决方案一：起别名

![image-20220925205844169](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220925205844169.png)



> 解决方案二：resultMap 结果集映射

```xml
    <!-- result 标签内可以只写需要映射的字段，不用全写  --> 
   <resultMap id="resultUser" type="user">
        <result column="address" property="ads" />
    </resultMap>
    <!-- 根据id获取一个用户-->
    <select id="getUserById" parameterType="int" resultMap="resultUser">
        select * from user where id =#{id}
    </select>
```



#### association 标签

用来查询多对一或者一对一的情况，也就是说一个实体类关联有别的类

> 关联（association）元素处理“有一个”类型的关系。MyBatis 有两种不同的方式加载关联：

（1）嵌套 Select 查询：通过执行另外一个 SQL 映射语句来加载期望的复杂类型。

（2）嵌套结果映射：使用嵌套的结果映射来处理连接结果的重复子集。

**标签的属性**

|    属性     | 描述                                                         |
| :---------: | :----------------------------------------------------------- |
|  property   | 映射到列结果的字段或属性。                                   |
|  javaType   | 一个 Java 类的完全限定名，或一个类型别名（关于内置的类型别名，可以参考上面的表格）。 |
|  jdbcType   | JDBC 类型，所支持的 JDBC 类型参见这个表格之前的“支持的 JDBC 类型”。 |
| typeHandler | 我们在前面讨论过默认的类型处理器。                           |
|   column    | 数据库中的列名，或者是列的别名。                             |
|   select    | 用于加载复杂类型属性的映射语句的 ID，它会从 column 属性指定的列中检索数据，作为参数传递给目标 select 语句。 |
|  fetchType  | 可选的。有效值为 lazy 和 eager。指定属性后，将在映射中忽略全局配置参数 lazyLoadingEnabled，使用属性的值。 |

##### 嵌套 Select 查询

![image-20220927212907044](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220927212907044.png)

表结构如图，学生表和老师表，学生对象存的是老师这个对象

**StudentMapper 接口**

```java
 List<Student> getStudents() ;
```

**StudentMapper.xml 文件**

```xml
 <!--
       1、查询所有的学生信息
       2、根据查询出来的学生tid 寻找对应的老师   ==》 子查询
    -->
    <select id="getStudents" resultMap="getStuAndTeacher">
        select u.id uid,u.name uname,t.id tid,t.name tname from student u,teacher t where u.tid = t.id
    </select>

    <resultMap id="getStuAndTeacher" type="student">
        <result property="id" column="uid"/>
        <result property="name" column="uname"/>
        <association property="teacher" column="tid" javaType="teacher"           select="getTeacher"/>
    </resultMap>

    <select id="getTeacher" resultType="teacher">
        select * from teacher where id =#{tid}
    </select>
```

**测试类**

```java
    static SqlSession sqlSession = MybatisUtil.getSqlSession();
    static StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
    Logger logger = Logger.getLogger(StudentMapper.class);
    @After
    public void mustDoIt(){
        sqlSession.close();
    }

    @Test
    public void testGetStudents(){
        List<Student> students = mapper.getStudents();
        System.out.println(students);  //输出完整的学生对象
    }
```

##### 嵌套结果映射（常用）

**StudentMapper 接口**

```java
 List<Student> getStudents2() ;
```

**StudentMapper.xml 文件**

```xml
 <!--
       按照结果嵌套处理
    -->
    <select id="getStudents2" resultMap="getStuAndTeacher2">
        select u.id uid,u.name uname,t.id tid,t.name tname from student u,teacher t where u.tid = t.id
    </select>

    <resultMap id="getStuAndTeacher2" type="student">
        <result property="id" column="uid"/>
        <result property="name" column="uname"/>
        <association property="teacher" javaType="teacher">
             <result property="id" jdbcType="INTEGER" column="tid"/>
             <result property="name" jdbcType="VARCHAR" column="tname"/>
        </association>
    </resultMap>
<!-- 这里的 jdbcType都要大写  -->

```

**测试类**

```java
    @Test
    public void testGetStudents2(){
        List<Student> students = mapper.getStudents2();
        System.out.println(students);  //输出完整的学生对象
    }
```

***



#### collection 标签

> <collection> 标签用于实现一对多关联关系映射。

此时改变Teacher类和Student类，老师类包含多个学生 如下：

![image-20220928144846919](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220928144846919.png)

##### 嵌套 Select 查询

**接口：**

```java
Teacher getTeacherById(@Param("id") int id);
```

```xml
 <!--嵌套 select 查询-->
    <select id="getTeacherById" resultMap="getTeacherMap">
        select t.id tid,
        t.name tname
        from teacher t
        where t.id = #{id}
    </select>
    <resultMap id="getTeacherMap" type="teacher">
        <result property="id" column="tid"/>
        <result property="name" column="tname"/>
        <collection property="students"  ofType="student" select="getStudent" column="tid"/>
    </resultMap>

    <select id="getStudent" resultType="student">
        select id ,name,tid from student where tid = #{tid}
    </select>

```

**测试结果**

```java
 @Test
    public void testGetStudents(){
        Teacher t = mapper.getTeacherById(1);
        System.out.println(t);
    }
===========================================
打印日志： Teacher(id=1, name=秦老师, students=[Student(id=1, name=小明, tid=1), Student(id=2, name=小红, tid=1), Student(id=3, name=小张, tid=1), Student(id=4, name=小李, tid=1), Student(id=5, name=小王, tid=1)])

```

注意下 `collection`  标签的常用属性：

- property="students"  

  ​          java实体类对应的属性名称; 这里对应的Teacher类中的 students 字段

- ofType="student" 

​                  集合中的类型 ； List<Student>students 所以这里的值是student（这里可以用别名）

- select="getStudent" 

​                  映射的查询id

- column="tid"

  ​          通过哪个字段去关联这个查询；这里用的是teacher表中的id

***



##### 嵌套结果映射

**接口方法：**

```java
Teacher getTeacherById2(@Param("id") int id);
```

**mapper文件**

```xml
   <!--嵌套 结果映射-->
    <select id="getTeacherById2" resultMap="getTeacherMap2">
        select t.id tid,
        t.name tname,
        s.id sid,
        s.name sname,
        s.tid stid
        from teacher t,student s
        where t.id = s.tid and t.id = #{id}
    </select>
    <resultMap id="getTeacherMap2" type="teacher">
        <result property="id" column="tid"/>
        <result property="name" column="tname"/>
        <collection property="students"  ofType="student">
                <result column="sid" property="id"/>
                <result column="sname" property="name"/>
                <result column="stid" property="tid"/>
        </collection>
    </resultMap>
```

**测试结果：**

```java
@Test
    public void testGetStudents2(){
        Teacher t = mapper.getTeacherById2(1);
        System.out.println(t);
    }
=======================================================================
打印日志:  Teacher(id=1, name=秦老师, students=[Student(id=1, name=小明, tid=1), Student(id=2, name=小红, tid=1), Student(id=3, name=小张, tid=1), Student(id=4, name=小李, tid=1), Student(id=5, name=小王, tid=1)])

```

注意区别;

> - 此种方式关联的`collection`标签，是提前把对象的需要的字段都查出来，在这里封装映射，和上面一种是有区别的；
>
> - 嵌套 Select 查询支持懒加载，这里不行

#### discriminator 标签

> 该标签用来根据列的值，选择不同的结果映射。例如：

```xml
<discriminator column="type" javaType="INTEGER">
  <case value="1" resultMap="BOOK_RESULT_MAP" />
  <case value="2" resultMap="CONTACT_RESULT_MAP" />
</discriminator>
```

上面映射中，将根据 type 数据类的值，动态决定使用 BOOK_RESULT_MAP 或 CONTACT_RESULT_MAP 结果集映射。其中：

- column：指定一个数据列名，该列名将用来实现动态选择结果集映射，如：type
- javaType：指定 column 指定的数据列在 Java 中的数据类型，如：INTEGER

<case> 标签用来映射一个条件分支，上面就使用两个 <case> 标签映射两个映射分支。其中：

- value：用来指定匹配当前条件分支的数据列值，如：type 列等于 1
- resultMap：用来指定一个结果集映射的ID，如：BOOK_RESULT_MAP

[discriminator 用法参考]([discriminator 标签 - MyBatis 教程 (hxstrive.com)](https://www.hxstrive.com/subject/mybatis/206.htm))

### 懒加载操作

​    **Mybatis 一对一关联的 association 和一对多的 collection 可以实现懒加载(在嵌套 Select 查询时)**

> 方式一：**mybatis-config.xml**中配置**settings**标签

```xml
<!-- 开启懒加载配置 -->
<settings>
    <!-- 全局性设置懒加载。如果设为‘false'，则所有相关联的都会被初始化加载。 -->
    <setting name="lazyLoadingEnabled" value="true"/>

    <!-- 当设置为‘true'的时候，懒加载的对象可能被任何懒属性全部加载。否则，每个属性都按需加载。 -->
    <setting name="aggressiveLazyLoading" value="false"/>
</settings>
```

> 方式二: association 或者 collection  标签内部 设置 `fetchType`属性

- **fetchType="lazy"**:表示使用延迟加载；

- **fetchType="eager "**:表示使用立即加载；

通过查看打印的sql语句可以判断是否开启了懒加载

***

### **动态 SQL** 

> 动态 SQL 是 MyBatis 一个强大的特性之一。MyBatis 采用功能强大的基于 OGNL（Object Graph Navigation Language，对象导航语言）的表达式来消除其他元素。MyBatis 动态 SQL 元素如下：

- **if**：作用和 Java 的 if 语句一致，用来根据条件动态决定是否执行 if 语句内部的 SQL 脚本。

- **choose(when,otherwise)**：作用和 Java 的 switch 语句一致，用来处理多条件判断。
- **trim(where,set)**：作用和 Java 中 String 类的 tirm() 方法类似，去除 SQL 字符串前后的字符。
- **foreach**：作用和 Java 中的 for 语句一致，用来根据传递的列表动态生成批量 SQL 脚本，例如：批量插入

#### if 

> 你们能判断，我也能判断！

```xml
 <select id="count" resultType="java.lang.Integer">
	select count(*) from user where <if test="id != null">id = #{id}</if> and username = 'xiaoming'
</select>
```

**注意：此种方法要看好where拼接时机**

- 如果传入的 id 不为 null， 那么才会 SQL 才会拼接 id = #{id}
- 如果传入的 id 为 null，那么最终的 SQL 语句就变成了 select count(*) from user where and username = ‘xiaoming’。这语句就会有问题，这时候 where 标签就该隆重登场了

***

#### where 

>  有了我，SQL 语句拼接条件神马的都是浮云！

```xml
  <select id="count" resultType="java.lang.Integer">
	select count(*) from user
	<where>
		<if test="id != null">id = #{id}</if>
		and username = 'xiaoming'
    </where>
 </select>

```

- where 元素只会在至少有一个子元素的条件返回 SQL 子句的情况下才去插入 WHERE 子句
- 若语句的开头为 AND、OR，where 元素也会将它们去除。还可以通过 trim 标签去自定义这种处理规则

#### trim

> 我的地盘，我做主！

**trim 标签一般用于拼接、去除 SQL 的前缀、后缀**

trim 标签中的属性:

|      属性       | 描述     |
| :-------------: | -------- |
|     prefix      | 拼接前缀 |
|     suffix      | 拼接后缀 |
| prefixOverrides | 去除前缀 |
| suffixOverrides | 去除后缀 |

```xml
<select id="count" result="java.lang.Integer">
	select count(*) from user
	<trim prefix ="where" prefixOverrides="and | or">
		<if test="id != null">id = #{id}</if>
		<if test="username != null"> and username = #{username}</if>
	</trim>
</select>

```

- 如果 id 或者 username 有一个不为空，则在语句前加入 where。如果 where 后面紧随 and 或 or 就会自动会去除
- 如果 id 或者 username 都为空，则不拼接任何东西

#### set 

>  信我，不出错！

```xml
<update id="UPDATE" parameterType="User">
	update user
    <set>
    	<if test="name != null">name = #{name},</if> 
        <if test="password != null">password = #{password},</if> 
        <if test="age != null">age = #{age},</if> 
   	</set>
</update>

```

- 三个 if 至少有一个不为空。会在前面加上 set，自动去除尾部多余的逗号



#### foreach

>  你有 for，我有 foreach

**foreach 标签中的属性**

|    属性    | 描述                           |
| :--------: | ------------------------------ |
|   index    | 下标                           |
|    item    | 每个元素名称                   |
|    open    | 该语句以什么开始               |
|   close    | 该语句以什么结尾               |
| separator  | 在每次迭代之间以什么作为分隔符 |
| collection | 参数类型                       |

**collection**：

- 如果参数类型为 List，则该值为 list

```xml
<select id="count" resultType="java.lang.Integer">
	select count(*) from user where id in
  	<foreach collection="list" item="item" index="index" open="(" separator="," close=")">
        #{item}
  	</foreach>
</select>

```

- 如果参数类型为数组，则该值为 array

```xml
<select id="count" resultType="java.lang.Integer">
	select * from user where id inarray
  	<foreach collection="array" item="item" index="index" open="(" separator="," close=")">
        #{item}
  	</foreach>
</select>

```

- 如果参数类型为 Map，则参数类型为 Map 的 Key

#### choose, when, otherwise 

>  我选择了你，你选择了我！

```xml
<select id="count" resultType="Blog">
	select count(*) from user
  	<choose>
    	<when test="id != null">
      		and id = #{id}
    	</when>
    	<when test="username != null">
      		and username = #{username}
    	</when>
    	<otherwise>
      		and age = 18
    	</otherwise>
  	</choose>
</select>

```

- 当 id 和 username 都不为空的时候， 那么选择二选一（前者优先）
- 如果都为空，那么就选择 otherwise 中的
- 如果 id 和 username 只有一个不为空，那么就选择不为空的那个

#### sql

> 相当于 Java 中的代码提重(封装)，需要配合 include 使用

```xml
<sql id="table"> user </sql>
```

#### include

> 相当于 Java 中的方法调用

```xml
<select id="count" resultType="java.lang.Integer">
	select count(*) from <include refid=“table（sql 标签中的 id 值）” />
</select>
```

#### bind

>  对数据进行再加工

```xml
<select id="count" resultType="java.lang.Integer">
	select count(*) from user
	<where>
		<if test="name != null">
			<bind name="name" value="'%' + username + '%'"
			name = #{name}
		</if>
</select>

```

### MyBatis 缓存

#### 一级缓存

> 此为一级session 缓存，默认开启，且无法关闭。中间如果有增删改或者关闭session 缓存失效

#### 二级缓存

> 一个mapper一个缓存，默认情况下是没有开启缓存的，开启的话需要在mapper配置文件中
>
> 增加`<cache />`

这个简单语句的效果如下：

- 映射语句文件中的所有 select 语句将会被缓存。
- 映射语句文件中的所有 insert， update 和 delete 语句会刷新缓存。
- 缓存会使用 Least Recently Used（LRU，最近最少使用的）算法来收回。
- 根据时间表（比如 no Flush Interval，没有刷新间隔），缓存不会以任何时间顺序来刷新。
- 缓存会存储列表集合或对象（无论查询方法返回什么）的 1024 个引用。
- 缓存会被视为是 read/write（可读/可写）的缓存，意味着对象检索不是共享的，而且可以安全地被调用者修改，而不干扰其他调用者或线程所做的潜在修改。

### 分页实现

####  limit 分页基础回顾

**select查询语句的执行顺序如下所示：**

```sql
select                  5
    ...
    字段        
    ...    
from                    1
    ...  
    表名
    ...
where                   2
    ...
    条件
    ...
group by                3
    ...
    分组字段
    ...
having                  4
    ...
    二次过滤
    ...
order by                6
    ...
    排序
    ... 
limit start, size       7
    ...
    分页
    ...    
 
 -- 可以看到limit在这些select的关键词中是最后一个使用的；
```

总结：MySQL的limit m n工作原理就是先读取前面m+n条记录，然后抛弃前m条，读后面n条想要的，所以m越大，偏移量越大，性能就越差。

`所以可以把过滤放在where 后提升limit效率`

> limit start, size的含义

- start是表示起始数据的行数

- size表示展示的数据数量
- 特别需要注意的是MySQL中的第一条数据是从**`下标index = 0开始`**算的。

```sql
-- 前1-5名
select ename, sal from emp order by sal desc  limit 0, 5

-- 前2-6名
select ename, sal from emp order by sal desc  limit 1, 5
```

limit [start], size中的start可以不写，如果不写等价于start = 0，size表示需要展示的数据条数。

> offeset  的使用

1、`select* from user limit 3`

表示直接取前三条数据

- 当 limit后面跟一个参数的时候，该参数表示要取的数据的数量

***



2、`select * from user limit 1,3;`

表示取1后面的第2,3,4三条条数据

- 当limit后面跟两个参数的时候，第一个数表示要跳过的数量，后一位表示要取的数量

***



3、`select * from user limit 3 offset 1;`

表示取1后面第2,3,4三条条数据

- 当 limit和offset组合使用的时候，limit后面只能有一个参数，表示要取的的数量,offset表示要跳过的数量 。

***

#### 1、Mybatis分页---limit 实现

1、增加接口

```java
     //分页查询1
     List<User> getUserListByLimit(Map<String,Integer>param) ;
```

2、mapper 中实现

```xml
    <!--分页方式一-->
    <select id="getUserListByLimit" parameterType="map" resultMap="resultUser">
         select * from user limit #{beginNum},#{pageSize}
    </select>
```

3、测试类调用

```java
    /**
     * 分页方式实现一
     */
    @Test
    public void testgetUserListByLimit(){
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("beginNum",2);
        hashMap.put("pageSize",2);
        List<User> userList = mapper.getUserListByLimit(hashMap);
        System.out.println(userList);
    }

```



#### 2、Mybatis分页---RowBounds 

> [Mybatis](https://so.csdn.net/so/search?q=Mybatis&spm=1001.2101.3001.7020)可以通过传递RowBounds对象，来进行数据库数据的分页操作，然而遗憾的是，该分页操作是对ResultSet结果集进行分页，也就是人们常说的逻辑分页，而非物理分页（物理分页当然就是我们在sql语句中指定limit和offset值）。

 `RowBounds`是将所有符合条件的数据全都查询到内存中，然后在内存中对数据进行分页，若数据量大，千万别使用RowBounds

​    如我们查询user表中id>0的数据，然后分页查询sql如下：

 ```sql 
 select * from user where id >0 limit 3,10
 ```

但使用RowBounds后，会将id>0的所有数据都加载到内存中，然后跳过offset=3条数据，截取10条数据出来，若id>0的数据有100万，则100w数据都会被加载到内存中，从而造成内存OOM。



1、接口中增加查询的抽象方法（这里复用之前的全表查询）

```java
List<User> getUserList() ;
```

2、mapper实现  （还是复用之前的，不用修改）

```xml
    <select id="getUserList" resultMap="resultUser">
        select * from user
    </select>
```

3、代码实现

```java
    /**
     * 分页方式实现二  RowBounds
        第一个参数也是接口+对应的方法拼接成串
     */
    @Test
    public void getUserList(){
        List<User> users = sqlSession.selectList("com.zjj.study.dao.UserMapper.getUserList", null, new RowBounds(1, 2));
        
        System.out.println(users);
    }
```



**注意：这是逻辑分页，加载全量后在内存中进行分页，一般不用了**

#### 3、Mybatis分页---PageHelper插件

[PageHelper官方使用文档](https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md)  --不能用Chrome浏览器打开

 1、导包

```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.2.3</version>
</dependency>
```

2、配置

```xml
<plugins>
    <!-- com.github.pagehelper为PageHelper类所在包名 -->
    <plugin interceptor="com.github.pagehelper.PageInterceptor">
        <!-- 使用下面的方式配置参数，后面会有所有的参数介绍   根据需要查看文档 -->
        <property name="param1" value="value1"/>
    </plugin>
</plugins>
```

3、开始使用（参考文档的多种用法，这里只写一种）

  `PageHelper.startPage` 静态方法调用实现

​      在你需要进行分页的 MyBatis 查询方法前调用 `PageHelper.startPage` 静态方法即可，紧跟在这个方法后的第一个**MyBatis 查询方法**会被进行分页。

```java
    /**
     * 分页方式实现三  PageHelper
     */
    @Test
    public void getUserList2(){
        PageHelper.startPage(2,2);
        Page<User> userList = (Page<User>) mapper.getUserList();
        System.out.println("=====================");
        for (User user : userList) {
            System.out.println(user);
        }  
```

**注意**：如果不便利输出的话而是直接打印userList  有可能不能打印出具体信息

[具体原因分析参考](https://blog.csdn.net/cnds123321/article/details/117132224)

### 注解开发

1、java接口

```java
     @Select("select * from user")
     List<User> getUserList3() ;
```

2、具体使用

```java
/**
 * 注解方式查询
 */
@Test
public void getUserList3(){
    List<User> userList3 = mapper.getUserList3();
    System.out.println(userList3);
}
```









































## 常用总结

### 记录一个大bug

> 数据库表是utf8mb4, idea设置的也都是utf8，但是保存、修改一直乱码

```xml
<!-- 最终解决方式 -->
修改mysql数据库的配置文件/etc/my.cnf，[mysqld]下添加一句character_set_server= utf8

[mysqld]
character_set_server= utf8
```

[参考](https://blog.csdn.net/crj123abcd/article/details/124527528)

### mybatis 传参方式

**只有一个基本类型参数的情况下 可以直接在sql中取到  也就是直接拿入参的形参**



1、 **顺序传参法（不推荐）**

![img](https://img-blog.csdnimg.cn/img_convert/a082fc3925d53e732849a253a852c39a.png)

\#{}里面的数字代表你传入参数的顺序。

这种方法不建议使用，sql层表达不直观，且一旦顺序调整容易出错

***



2、**@Param注解传参法（推荐）**

![img](https://img-blog.csdnimg.cn/img_convert/f8da8822e53fd8ea3d653b064d418fb0.png)

\#{}里面的名称对应的、是注解 @Param括号里面修饰的名称。

这种方法在参数不多的情况还是比较直观的，推荐使用。

> 当接口中只有一个参数(并且没有用@Param())时候，需要在xml中添加响应的参数类型parameterType；
>
> 如果是多个参数(每个参数都是用@Param())的时候，就不会去读参数类型parameterType，
> 直接取得参数里面的值。

***



3、**Map传参法（推荐）**

![img](https://img-blog.csdnimg.cn/img_convert/ec6cf7ae49bb020fdd851c5433c0e37b.png)

\#{}里面的名称对应的是 Map里面的key名称。

这种方法适合传递多个参数，且参数易变能灵活传递的情况

***



4、**Java Bean传参法（推荐）**

![img](https://img-blog.csdnimg.cn/img_convert/ec316af4ce711ca7e6af82c0cfe59f02.png)

\#{}里面的名称对应的是 User类里面的成员属性。

这种方法很直观，但需要建一个实体类，扩展不容易，需要加属性，看情况使用。



### 如何设置自动提交事务



>  //创建session 时 ，可以设置参数来确定
>
>   sqlSessionFactory.openSession(false);   
>
>  `如果不设置参数或者参数为false就是手动提交事务，参数设置为true就是自动提交事务`

### mybatis 返回自增主键

> - 对于支持生成自增主键的数据库，可以使用useGenerateKeys和keyProperty 来返回插入后的主键；
> - 不支持生成自增主键的数据库，使用<selectKey>（例如oracle不支持自增主键）

**方式1**

```xml
< insert id = “doSomething" parameterType = "map" useGeneratedKeys = "true" keyProperty = “yourId" >
...
</ insert >

< insert   id = “doSomething"   parameterType = “com.xx.yy.zz.YourClass"   useGeneratedKeys = "true"   keyProperty = “yourId" >
...
</ insert >
```

**方式2**

```xml
<insert id="insertProduct-Mysql" parameterClass="com.domain.Product">  
  insert into PRODUCT(PRD_DESCRIPTION)  
  values (#description#)  
  <selectKey resultClass="int" keyProperty="id">  
    SELECT LAST_INSERT_ID()  
  </selectKey>  
</insert>
```



### mybatis 怎么防止sql注入

**假设mapper.xml文件中sql查询语句为：**

```html
<select id="findById" resultType="String">
    select name from user where id = #{userid};
</select>
```

对应的接口为：

```java
public String findById(@param("userId")String userId);
```

当传入的参数为3;drop table user; 当我们执行时可以看见打印的sql语句为：

```sql
select name from usre where id = ?;
```

不管输入何种参数时，都可以防止sql注入，因为mybatis底层实现了预编译，底层通过prepareStatement预编译实现类对当前传入的sql进行了预编译，这样就可以防止sql注入了。

****

**如果将查询语句改写为：**

```html
<select id="findById" resultType = "String"> 
    select name from user where id=${userid} 
</select>
```

当输入参数为3；drop table user; 执行sql语句为

```sql
select name from user where id = 3;drop table user ;
```

此时`mybatis `没有进行预编译语句，它先进行了字符串拼接，然后进行了预编译。这个过程就是sql注入生效的过程。

### 总结

因此在编写mybatis的映射语句时，尽量采用“#{xxx}”这样的格式。若不得不使用“${xxx}”这样的参数，**要手工地做好过滤工作，来防止sql注入攻击。**








































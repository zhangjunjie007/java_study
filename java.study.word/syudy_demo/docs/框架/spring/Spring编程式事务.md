# 项目说明

本项目重点理解实践 java学历中的知识点

## 1、项目启动前的配置

主要是springBoot 集成Mybatis 配置，以及对应的日志实现

### 配置方式一

> 此配置就是把所有的配置都写在application.yml 文件内

```yaml
server:
  port: 8081
logging:
  config: classpath:log/logback.xml
# mybatis 配置
mybatis:
  #标注mybatis配置文件的位置
  type-aliases-package: com.zjj.domain
  mapper-locations: classpath:mapper/*.xml
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
#  config-location: classpath:mybatis.xml (这个是另外一种方式)
spring:
  datasource:
    username: root
    password: root
    #url中database为对应的数据库名称
    url: jdbc:mysql://192.168.59.128:3306/mybatis_study?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
```

别忘了在启动类加上注解

```java
@MapperScan("com.zjj.mapper")
```



注意：此种方式配置以后，mapper.xml可以不必强制要求必须与mapper接口路径保持一致，只需要额外配置一个logback.xml文件即可

​     logback.xml 是用来实现注解 @Slf4j 的，当然也可以用log4j2 等日志实现

## 配置方式二

> 此配置主要是在application.xml 文件中引入其他配置文件来实现



[参考](https://www.jianshu.com/p/25dd95ce3060)

## 2、Spring中编程式事务



![alt](https://img-blog.csdnimg.cn/7272254001d8479b90337470e1a31f63.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Y-456m656uL5LiJ,size_20,color_FFFFFF,t_70,g_se,x_16)

如上图，Spring事务管理高层抽象主要有3个：

PlatformTransactionManager :事务管理器(用来管理事务，包含事务的提交，回滚)

TransactionDefinition :事务定义信息(隔离，传播，超时，只读)

TransactionStatus :事务具体运行状态

### PlatformTransactionManager  

> 核心事务管理器, 是Spring的事务管理器核心接口。
>
> Spring本身并不支持事务实现，只是负责包装底层事务，应用底层支持什么样的事务策略，Spring就支持什么样的事务策略。里面提供了常用的操作事务的方法：
>

- TransactionStatus getTransaction(TransactionDefinition definition):获取事务状态信息

- void commit(TransactionStatus status):提交事务

- void rollback(TransactionStatus status):回滚事务

包含了一些常用实现类：

DataSourceTransactionManager：使用JDBC和[iBatis](https://so.csdn.net/so/search?q=iBatis&spm=1001.2101.3001.7020)进行持久化数据时使用

HibernateTransactionManager：使用Hibernate 3.0进行持久化数据时使用

JpaTransactionManager：使用JPA进行持久化时使用

JtaTransactionManager :如果没有使用上述的事务管理，或者在一个事务跨越多个事务管理源时使用

### TransactionDefinition   信息对象

该接口定义了一些基本事务属性 

```java
public interface TransactionDefinition {
    int getPropagationBehavior(); // 返回事务的传播行为
    int getIsolationLevel(); // 返回事务的隔离级别，事务管理器根据它来控制另外一个事务可以看到本事务内的哪些数据
    int getTimeout();  // 返回事务必须在多少秒内完成
    boolean isReadOnly(); // 事务是否只读，事务管理器能够根据这个返回值进行优化，确保事务是只读的
} 
```

#### 事务隔离级别

TransactionDefinition 中一共定义了 5 种事务隔离级别：

1. ISOLATION_DEFAULT，使用数据库默认的隔离级别，MySql 默认采用的是 REPEATABLE_READ，也就是可重复读。

2. ISOLATION_READ_UNCOMMITTED，最低的隔离级别，可能会出现脏读、幻读或者不可重复读。

3. ISOLATION_READ_COMMITTED，允许读取并发事务提交的数据，可以防止脏读，但幻读和不可重复读仍然有可能发生。

4. ISOLATION_REPEATABLE_READ，对同一字段的多次读取结果都是一致的，除非数据是被自身事务所修改的，可以阻止脏读和不可重复读，但幻读仍有可能发生。

5. ISOLATION_SERIALIZABLE，最高的隔离级别，虽然可以阻止脏读、幻读和不可重复读，但会严重影响程序性能。通常情况下，我们采用默认的隔离级别 ISOLATION_DEFAULT 就可以了，也就是交给数据库来决定。

   

###  TransactionStatus   运行状态

该接口定义了事务具体的运行状态

```java
public interface TransactionStatus{
    boolean isNewTransaction(); // 是否是新的事物
    boolean hasSavepoint(); // 是否有恢复点
    void setRollbackOnly();  // 设置为只回滚
    boolean isRollbackOnly(); // 是否为只回滚
    boolean isCompleted; // 是否已完成
} 
```

### 编程式和声明式事务的区别

​      Spring提供了对编程式事务和声明式事务的支持，编程式事务允许用户在代码中精确定义事务的边界，而声明式事务（基于AOP）有助于用户将操作与事务规则进行解耦。
​      简单地说，编程式事务侵入到了业务代码里面，但是提供了更加详细的事务管理；而声明式事务由于基于AOP，所以既能起到事务管理的作用，又可以不影响业务代码的具体实现

### 如何实现编程式事务

#### 使用TransactionTemplate

```java
TransactionTemplate tt = new TransactionTemplate(); // 新建一个TransactionTemplate
    Object result = tt.execute(
        new TransactionCallback(){  
            public Object doTransaction(TransactionStatus status){  
                updateOperation();  
                return resultOfUpdateOperation();  
            }  
    }); // 执行execute方法进行事务管理
```

#### 使用PlatformTransactionManager

> 重点是这几个方法
>
> ```Java
> DefaultTransactionDefinition def = new DefaultTransactionDefinition();
> //事务的传播属性
> def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
> TransactionStatus status = transactionManager.getTransaction(def);
> try{
>     ...
>     transactionManager.commit(status);
> }catch(){
>    transactionManager.rollback(status);
> }
>  
> ```

```java
package com.zjj.controller;

import com.zjj.domain.CommonRespo;
import com.zjj.service.UserService;
import com.zjj.util.CommonRespoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 测试编程式事务的使用
 */
@RestController
@RequestMapping("/transaction/test")
@Slf4j
public class TransactionalDemoController {
    @Resource
    private UserService userService ;

    @Resource
    PlatformTransactionManager transactionManager ;

    @RequestMapping(value = "",method = {RequestMethod.GET,RequestMethod.POST})
    public CommonRespo<String> testTransaction(@RequestParam int param_1,@RequestParam int param_2){
        log.info("进入到方法【{}】",param_1);
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        //事务的传播属性
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            //正常修改
            userService.updateUserStatus(14,0);

            //状态改为10 会报错  后台status为char（1）
            userService.updateUserStatus(15,10);
            transactionManager.commit(status);
        } catch (Exception e) {
            System.out.println("报错了:"+e.getMessage());
            log.error("执行更新报错"+e.getMessage());
            //报错以后这里会回滚
            transactionManager.rollback(status);
        }

        return CommonRespoUtil.isOk("测试一下");
    }
}


```

如下图，直接注入的时候默认创建的是JdbcTransactionManager 



![在这里插入图片描述](https://img-blog.csdnimg.cn/0b509703536e42c9beb697ac008b7df9.png)

JdbcTransactionManager 继承了DataSourceTransactionManager

![在这里插入图片描述](https://img-blog.csdnimg.cn/277dd5e9a46e4f8c9edeb831451efd43.png)

从断点里可以看到，构建事务管理器时，如果不指定，springboot默认采用 HikariDataSource  连接池

### @Transaction失效场景

- 作用于非public方法上

  > ​      之所以会失效是因为在Spring AOP 代理时，如下图所示 TransactionInterceptor （事务拦截器）在目标方法执行前后进行拦截，DynamicAdvisedInterceptor（CglibAopProxy 的内部类）的 intercept 方法或 JdkDynamicAopProxy 的 invoke 方法会间接调用 AbstractFallbackTransactionAttributeSource的 computeTransactionAttribute
  > 方法，获取Transactional 注解的事务配置信息。
  > 此方法会检查目标方法的修饰符是否为 public，不是 public则不会获取@Transactional 的属性配置信息。

![alt](https://img-blog.csdnimg.cn/54ae21c045aa498ebc4a11c2b4b7ab44.png)

注意：protected、private修饰的方法上使用 @Transactional 注解，虽然事务无效，但不会有任何报错，这是我们很容犯错的一点。

- propagation设置问题，会导致事务不生效，也就事务不会回滚
- rollbackFor指定事务回滚的异常类型
- `同个类中的调用被@transaction修饰的方法，会失效，因为只有当事务方法被当前类以外的代码调用，才会由spring生成的代理对象来管理。`
- try catch导致失效
- 数据库不支持事务

### Spring事务的传播机制

> ​      事务的传播指的是，一个事务方法A，被另外一个方法B调用的时候，对方法A有何种影响（两个方法事务独立执行、A方法事务合并到B方法事务、或以非事务方式执行等）。

传播机制	说明

| 传播机制                  | 说明                                                         |
| ------------------------- | ------------------------------------------------------------ |
| PROPAGATION_REQUIRED      | 如果当前没有事务，就新建一个事务，如果已经存在一个事务中，加入到这个事务中。这是最常见的选择。 |
| PROPAGATION_SUPPORTS      | 支持当前事务，如果当前没有事务，就以非事务方式执行。         |
| PROPAGATION_MANDATORY     | 使用当前的事务，如果当前没有事务，就抛出异常。               |
| PROPAGATION_REQUIRES_NEW  | 新建事务，如果当前存在事务，把当前事务挂起。                 |
| PROPAGATION_NOT_SUPPORTED | 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。   |
| PROPAGATION_NEVER         | 以非事务方式执行，如果当前存在事务，则抛出异常。             |
| PROPAGATION_NESTED        | 如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则执行与PROPAGATION_REQUIRED类似的操作。 |

**因Spring声明式事务的实现原理为[动态代理](https://so.csdn.net/so/search?q=动态代理&spm=1001.2101.3001.7020)，所以事务的传播特性仅限于不同类方法间的相互调用，同类中事务方法被调用时，动态代理不生效，所以事务的传播也会失效。可通过上下文的方式注入类自身解决**

```
@Transactional的事务开启 ，或者是基于接口的 或者是基于类的代理被建立。因此在同一个类中一个无事务的方法调用另外一个有事务的方法，事务是不会起做用的。若是在有事务的方法中调用另一个有事务的方法，那么事务会起做用，而且共用事务。若是在有事务的方法中调用另一个没有事务的方法，那么事务也会起做用
```

**不生效的缘由：**
    **当从类外调用没有添加事务的方法a()时，从spring容器获取到的serviceImpl对象实际是包装好的proxy对象，所以调用a()方法的对象是动态代理对象。而在类内部a()调用b()的过程当中，实质执行的代码是this.b()，此处this对象是实际的serviceImpl对象而不是本该生成的代理对象，所以直接调用了b()方法。**

**解决办法：**

1. 放到不一样的类中进行调用
2. 在spring配置文件中加入配置
   <aop:aspectj-autoproxy/>
   <aop:aspectj-autoproxy proxy-target-class=“true” expose-proxy=“true” />
3. 将以前使用普通调用的方法,换成使用代理调用
   ((TestService)AopContext.currentProxy()).testTransactional2();
   获取到TestService的代理类，再调用事务方法，`强行通过代理类，激活事务切面。`
4. 使用异步操做，另外开启一个线程或者将这个消息写入到队列里面，在其余的地方进行处理










































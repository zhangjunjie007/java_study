# 常用sql优化总结
## 优化器
> 概念：
Oracle的优化器共有两种的优化方式,即**基于规则的优化方式**(Rule-Based Optimization,简称为RBO)和**基于代价的优化方式**(Cost-Based Optimization,简称为CBO)。

1. RBO方式：优化器在分析SQL语句时,所遵循的是Oracle内部预定的一些规则。比如我们常见的,当一个where子句中的一列有索引时去走索引。


2. CBO方式：依词义可知,它是看语句的代价(Cost)了,这里的代价主要指Cpu和内存。优化器在判断是否用这种方式时,主要参照的是表及索引的统计信息。统计信息给出表的大小 、有少行、每行的长度等信息。这些统计信息起初在库内是没有的,是你在做analyze后才出现的,很多的时侯过期统计信息会令优化器做出一个错误的执行计划,因些我们应及时更新这些信息。在Oracle8及以后的版本,Oracle列推荐用CBO的方式。


**我们要明了,不一定走索引就是优的 ,比如一个表只有两行数据,一次IO就可以完成全表的检索,而此时走索引时则需要两次IO,这时对这个表做全表扫描(full table scan)是最好的。**

>优化器的优化模式(Optermizer Mode)


优化模式包括Rule,Choose,First rows,All rows这四种方式,也就是我们以上所提及的。如下我解释一下：


- Rule:不用多说,即走基于规则的方式。


- Choolse:这是我们应观注的,默认的情况下Oracle用的便是这种方式。指的是当一个表或或索引有统计信息,则走CBO的方式,如果表或索引没统计信息,表又不是特别的小,而且相应的列有索引时,那么就走索引,走RBO的方式。


- First Rows:它与Choose方式是类似的,所不同的是当一个表有统计信息时,它将是以最快的方式返回查询的最先的几行,从总体上减少了响应时间。


- All Rows:也就是我们所说的Cost的方式,当一个表有统计信息时,它将以最快的方式返回表的所有的行,从总体上提高查询的吞吐量。没有统计信息则走基于规则的方式。
> 如何设定选用哪种优化模式

1. Instance级别

我们可以通过在init.ora文件中设定
**OPTIMIZER_MODE=RULE、
OPTIMIZER_MODE=CHOOSE、
OPTIMIZER_MODE=FIRST_ROWS、
OPTIMIZER_MODE=ALL_ROWS**
去选用3所提的四种方式,如果你没设定OPTIMIZER_MODE参数则默认用的是Choose这种方式。


2. Sessions级别


通过SQL> ALTER SESSION SET OPTIMIZER_MODE=;来设定。


3. 语句级别

```sql
这些需要用到Hint,比如:

SQL> SELECT /*+ RULE */ a.userid, 
    b.name, 
   b.depart_name 
  FROM tf_f_yhda a, 
  tf_f_depart b 
 WHERE a.userid=b.userid
```
>假如我们先用CBO的方式,就应当及时去更新表和索引的统计信息,以免生形不切合实的执行计划。

```javascript
SQL> ANALYZE TABLE table_name COMPUTE STATISTICS; 
SQL> ANALYZE INDEX index_name ESTIMATE STATISTICS;
```
**具体使用时可自行百度**
## 执行计划
主要内容
![alt](https://img-blog.csdnimg.cn/20200923065855767.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_5Y2a5a6i77ya6bG85Li45Li257KX6Z2i,size_26,color_FFFFFF,t_70#pic_left)
[查看oracle执行计划的几种方式（推荐）](https://blog.csdn.net/Dongguabai/article/details/84306751?spm=1001.2014.3001.5506)
[查看oracle执行计划示例](https://blog.csdn.net/qq_34745941/article/details/106068346?spm=1001.2014.3001.5506)


## sql 优化相关
1. order by ,distinct 等影响性能的慎用
2. union 和 union all 的使用
3. is null /is not null 的优化使用
4. in 和 exists 的使用
5. 驱动表选择好，如果可以的话先确定最小范围的驱动表，然后再去关联其它表
6. 表分区
7. 并行


==sql优化相关可以参考== **[sql优化](https://blog.csdn.net/dtjiawenwang88/article/details/74892245?spm=1001.2101.3001.6650.3&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-3.pc_relevant_antiscanv2&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-3.pc_relevant_antiscanv2&utm_relevant_index=6)**
### 页面响应比较慢的
1. ajax 同步改异步（根据实际情况）
2. 初始化默认查询时，增加默认的查询条件
3. 提升sql查询性能


## 案例1 大数据量的表修改数据
# 大数据量的表怎么更新
> 现在有一张表数据量达很大，要把里面记录时间的那行更新到当前最新日期，每次更新都很卡，机器变慢影响业务，怎么优化?
     
   总结自[大数据量插入或更新]([https://blog.csdn.net/knuuy/article/details/47667655](https://blog.csdn.net/knuuy/article/details/47667655));

一般数据库都是在归档模式下运行（Oracle数据库有联机重做日志，这个日志是记录对数据库所做的修改，比如插入，删除，更新数据等，对这些操作都会记录在联机重做日志里），跳过就要关闭归档模式，还有就是修改表在nologging状态下。这样两个状态下就可以不写日志，但是也是在万分确定不会出错的情况下才能使用。
 不过这样操作，数据库就要关闭重启到mount的状态，再关闭归档，这样在生产库是不允许的，而且关闭归档是影响整个数据库的，其他业务也会陷入无法恢复的境地，整个数据库无法使用rman做增量备份，很多备份方案都会受到限制。



所以我最后说了先清空缓存，然后把那个表设置为nologging的状态，再进行操作。（单独这个表没有写日志，其他表也照常。）
面试官：是基本这样，但是还有一种更加优化的方法。我最后也没想出来，面试官就告诉我：**用create table 复制....+ nologging的方法**，当时就心领神会大。虽然不明白为什么同样在nologging的情况下create 会比 直接update 会优化的好，是因为create 是 DDL 吗？继续百度。但我还是先整理这个最终优化方案。
##总结：
 ==这个nologging+append是在归档模式下性能优化的好方法==
##  思考1：是不是update表产生的日志要远高于insert？
>思考2中有记录，update时，undo表空间内会记录所修改的旧值，所以大数据量的表修改某一个字段时，可能用重新insert的效率更高
>参考    ==[为什么大表不要直接update]==(https://blog.csdn.net/weixin_39587238/article/details/116385126)

![alt](https://img-blog.csdnimg.cn/30eee92d4dff4a9d963fb61ba65bef87.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQXJ5cw==,size_20,color_FFFFFF,t_70,g_se,x_16)
## 思考2：数据库模式怎么看，日志怎么控制？
>undo表空间与redo日志文件在oracle中的作用非常重要，本文重点介绍undo回滚段的作用与特点，同时简单介绍undo与redo的区别和各自己的作用。


具体undo和redo区别

**参考1** [undo和redo区别](https://blog.csdn.net/IndexMan/article/details/7747720)

**参考2：[nologging和append总结](https://blog.csdn.net/linminqin/article/details/6602476?spm=1001.2014.3001.5506)**
![在这里插入图片描述](https://img-blog.csdnimg.cn/734cbf5275dc4c3b96bc94cac5ae6fe3.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQXJ5cw==,size_20,color_FFFFFF,t_70,g_se,x_16)

**参考3：[重做日志和归档日志概念](https://blog.csdn.net/u013196348/article/details/110098822?spm=1001.2014.3001.5506)**



**一、undo中数据的特点：**
1. 是数据修改前的备份，主要是保证用户的读一致性（为了实现这种功能，类似Redo，Oracle用Undo来记录前备份信息，==insert、update、delete的相关信息记录在Undo表空间的回滚段内；记录的信息量，insert最少只需添加记录的rowid、update其次记录所修改的旧值，delete最多记录所删除记录的整行数据；如一事务的修改还未提交，另一事务所查询的数值会由Undo信息提供==）
2. 在事务修改数据时产生
3. 至少保存到事务结束
**二、undo数据的作用：**
1. 回滚(rollback)操作
2. 实现读一致性与闪回查询
3. 从失败的事务中还原数据
4. 非正常停机后的实例恢复
**三、undo回滚段的特点：**
1. 回滚段是由实例自动创建用于支持事务运行的专用段，同样是区和块组成，回滚顶会按实际需要自动进行增长或收缩，是一段可以给指定事务循环使用的存储缓冲区
2. 每个事务只会使用一个回滚段，一个回滚段在同一时刻可能会服务于多个事务
3. 当一个事务开始的时候，会指定一个回滚段，在事务进行的过程中，当数据被修改时，原始的数据会被复制到回滚段
4. 在回滚段中，事务会不断填充盘区，直到事务结束或所有的空间被用完，如果当前的盘区不够用，事务会在段中请求扩展下一个盘区，如果所有已分配的盘区都被用完，事务会覆盖最初的盘区或者在回滚段允许的情况下扩展新的盘区来使用
5. 回滚段存在于undo表空间中，在数据库中可以存在多个undo表空间，但同一时刻只能使用一个undo表空间
**四、回滚段中的数据类型:**
回滚段中的数据主要分为以下三种：
1. Uncommitted undo information; 未提交的回滚数据，该数据所关联的事务并未提交，用于实现读一致性，所以该数据不能被其它事务的数据所覆盖
2. Committed undo information;已经提交但未过期的回滚数据，该数据关联的事务已经提交，但是仍受到undo retention参数保持时间的影响
3. Expired undo information;事务已经提交，而且数据保存时间已经超过undo retention参数指定的时间，属于已经过期的数据
当回滚段满了后，会优先覆盖Expired undo information，当过期数据空间用完后，会再覆盖Committed undo information的区域，这时undo retention参数所规定的保持时间会被破坏，Uncommitted undo information的数据是不允许覆盖的，如果要求提交的数据在undo retention参数规定的时间内不会被覆盖，可以在undo表空间上指定RETENTION GUARANTEE，语法如下：
ALTER TABLESPACE UNDOTBS1 RETENTION GUARANTEE
**五、undo数据与redo数据的区别：**
1. undo记录数据修改之前的操作，redo记录磁盘数据将要进行的操作
2. undo用于数据的回滚操作，和实现一致性读，redo用于前滚数据库操作
3. undo存储在回滚段里，redo存储在重做日志文件里
4. undo用于在多用户并发的系统里保证一致性读，redo用于防止数据丢失

## 思考3：怎么批量插入大量数据最快？
```
ORACLE 大数据insert可以使用下面hint来提高SQL的性能

insert /*+ append parallel(a, 4) nologging */ 
into target_table a 
select /*+ parallel(b, 4) */ * 
from source_table b; 

APPEND的作用是在表的高水位上分配空间,不去寻找 freelist 中的free block , 直接在table HWM 上面加入数据； 
nologging 会大量减少日志； 
parallel 并行。
```
## 思考4：对每个表都可以用并行吗，并行度设置成多少都可以吗？

1、表的并行度默认是1，没有开启。可以从 user_tables 进行查询；<br>
```
  **select  table_name ,degree from user_tables where table_name = 'xxx' ;**<br>
```
2、修改表得并行度
```
alter table emp parallel 8;
**经测试，设置完表的并行度以后，查询时不写hints 也会用到并行查询**
```
3、使用并行Hint
 暗示hints式，临时有效
```
select /*+parallel(table_name num)*/ count(*) from table_name;
多表关联时多表并行：
select /*+parallel(table_name1,num1) parallel(table_name2,num2)*/ count(*) from table_name1, table_name2;
```
 **有如下一些并行Hint可以用来控制是否启用并行及指定并行度**
```

1) /*+ parallel(table[,degree]) */  #用于指定并行度去访问指定表，如果没有指定并行度degree，则使用Oracle默认并行度

2) /*+ noparallel(table) */  #对指定表不使用并行访问

3) /*+ parallel_index(table[,index[,degree]]) */  #对指定的分区索引以指定的并行度去做并行范围扫描

4) /*+ no_parallel_index(table[,index]) */  #对指定的分区索不使用并行访问

5) /*+ pq_distribute(table,out,in) */ #对指定表以out/in所指定的方式来传递数据，这里out/in的值可以是HASH/NONE/BROADCAST/PARTITION中的任意一种如/*+ pq_distribute(table,none,partition) */
```
4、把表EMP修改回并行度为1 <br>
    &nbsp;&nbsp;&nbsp;  **alter table emp noparallel;** 

注意：并行度受系统整体参数的影响
![image.png](https://img-blog.csdnimg.cn/img_convert/750c68a83e482539c07d3542922db6a6.png)


5、使用alter session命令
使用alter session命令，可以在当前session中强制启用并行查询或并行DML。如果强制启用了并行查询或者并行DML，那就意味着从执行alter session命令强制开启并行的那个时间点开始，在这个session中随后执行的所有SQL都将以并行的方式执行，有如下四种方法在当前session中强制开启并行
参考：[oracle并行度]([https://blog.csdn.net/weixin_39952800/article/details/116291388](https://blog.csdn.net/weixin_39952800/article/details/116291388)
);

**1) alter session parallel query**

在当前session中强制开启并行查询，没有指定并行度，Oracle使用默认并行度

**2) alter session parallel query  parallel n**

在当前session中强制开启并行查询，并且指定并行度为n

**3) alter session parallel dml**

在当前session中强制开启并行DML,没有指定并行度，Oracle使用默认并行度

**4) alter session parallel dml  parallel n**

在当前session中强制开启并行DML，并且指定并行度为n
==注意：不建议对对象开启并行，容易失控，可以在会话级别==




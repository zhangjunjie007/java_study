# oracle和mysql 部分细节对比

> 1、黑窗口登录oracle和mysql 区别

1. oracle 登录

   - sqlplus sys/system#123@83.25.78.42/TIMSDB as sysdba
   - sqlplus  TIMS/TIMS#123@83.25.78.42/TIMSDB

2. mysql 登录（此方法限于本地有mysql服务）

   mysql -uroot -p+password(paddword为数据库登录密码)

> 2、oracle 和 mysql 的b+树索引有什么区别？

```javascript
mysql 索引分为聚集索引，和非聚集索引；
      一般主键索引就是聚集索引，也就是在叶子节点上，存储主键和行数据。
      普通索引则是非聚集索引，叶子节点上除了存储索引列的值，还有对应的主键值。
oracle 主键索引上存储的除了主键值外还有行数据的rowid，普通索引也是索引列值和rowid，oracle中获取数据最快的方式就是通过rowid
       主键索引和普通索引的最大差别是：主键索引不能有空值和重复值；一个表里只能有一个主键索引，普通索引可以有多个。

```

```sql
       #Oracle创建普通索引
        CREATE INDEX index_emp_deptno ON emp_test(deptno);
        #Oracle删除普通索引
        drop INDEX index_emp_deptno ;
        #Oracle创建主键索引
        ALTER TABLE emp_test ADD CONSTRAINT pk_emp_deptno PRIMARY KEY (deptno);
        #Oracle删除主键索引
        ALTER TABLE emp_test DROP CONSTRAINT pk_emp_deptno;

        #查看当前用户的索引详细信息
        SELECT * FROM user_indexes
        #与MySQL的创建语法有所不同
        alter table account add primary key (account_number);
```

**主要区别**

**1、mysql 如果使用到了覆盖索引可以不需要回表，但是oracle查询时，首先是通过 索引定位到rowid ，然后再拿rowid去表里查数据。所以sql优化时 mysql可以根据情况适当建立联合索引，减少回表次数**



索引一定要参考文章：

  [索引原理](https://blog.csdn.net/weixin_43477531/article/details/121648548)

# PLSQL介绍
**plsql是Oracle对sql999 的一种拓展**，基本每一种数据库都会对sql进行拓展。Oracle对sql的拓展就叫做plsql<br>

### sql999 是什么？
-  (1)**是操作所有关系型数据库得规则**
-  (2)是第四代语言
-  (3)**是一种结构化查询语言**
-  (4)只需发出合法合理的命令，就有对应的结果显示

### sql的特点
*  (1)**交互性强，非过程话**
*  (2)数据库操纵能力强，只需发送命令，无需关注如何实现
*  (3)多表操作时，自动导航简单，例如
```
select emp.empno,emp.sal,dept.dname
   from emp,dept
where emp.deptno = dept.deptno
```
*  (4)容易调试，错误提示，直接了当
*  (5)**sql强调结果**
### PLSQL是什么

1. 是专用于Oracle服务器，在sql基础之上，**添加了一些过程化控制语句，叫做PLSQL**
2. 过程化包括有：类型定义，判断，循环，游标，异常或例外处理。。。
3. PLSQL强调过程
![alt](https://img-blog.csdn.net/20170708192624517?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvaG9uXzN5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
### 为什么要用PLSQL
- 因为SQL是第四代命令式语言，**无法显示处理过程化的业务，所以得用一个过程化程序设计语言来弥补SQL的不足之处**
- SQL和PLSQL不是替代关系，是**弥补**关系

# PLSQL语法
**declare和exception都是可以省略的，<font color = "red">*begin和end;/*</font>是不能省略的**
```
[declare]
          变量声明;
      变量声明;
     begin
          DML/TCL操作;
      DML/TCL操作;
     [exception]
          例外处理;
      例外处理;
     end;
     /
```
在PLSQL程序中：**；号表示每条语句的结束，/表示整个PLSQL程序结束**
### PLSQL与SQL执行有什么不同：
* （1）SQL是单条执行的
* （2）PLSQL是整体执行的，不能单条执行，整个PLSQL结束用/，其中每条语句结束用；号
# PLSQL变量
> 变量和常量的数据类型可以概括为三类
1. 标量类型：单一类型，不存在组合
2. 复合类型：由几种单一类型组合而成的
3. 参照类型：主要有游标、对象类型 <br>

**标量类型**<br>
标量类型是我们最常用的，主要有下面几个
```
数字类型
number(p,s)
                          
字符类型
char、varchar2、nchar、nvarchar2、long

日期类型
date、timestamp

布尔类型 (表中的字段不能使用布尔类型，但是PL/SQL中可以)
boolean

%type类型     
根据已有的变量数据类型或者数据库表中字段的类型
```
这里只列出常用的一些标量类型，下面举例演示：
```
declare 
  v_pid       product.pid%type;
  v_name      varchar2(20);
  v_price     number(8,2);
  v_num       number(10);
  v_address   v_pid%type;
  v_cid       varchar(36); 

  c_desc      constant varchar2(20) := '测试常量';
  v_date      date  := sysdate;
  
begin
  select p.pid, p.name, p.price, p.num, p.address, p.cid
    into v_pid, v_name, v_price, v_num, v_address, v_cid
    from product p
    where p.pid = '1';
  
  DBMS_OUTPUT.PUT_LINE('商品id:'   || v_pid);
  DBMS_OUTPUT.PUT_LINE('商品名称:' || v_name);
  DBMS_OUTPUT.PUT_LINE('商品价格:' || v_price);
  DBMS_OUTPUT.PUT_LINE('商品数量:' || v_num);
  DBMS_OUTPUT.PUT_LINE('商品生产地址:' || v_address);
  DBMS_OUTPUT.PUT_LINE('商品分类id:'   || v_cid);
  DBMS_OUTPUT.PUT_LINE('常量:' || c_desc);
  DBMS_OUTPUT.PUT_LINE('时间:' || v_date);
end;
/
```
**复合类型**
> 复合类型主要有：记录类型、表类型、varray类型
1. 记录类型
```
形式1：
TYPE  type_name IS RECORD   --声明记录类型
(   
  variable_name1  datatype1,
  variable_name2  datatype2,
  ...
);
variable_name  type_name;   --记录类型变量
示例：
declare
    TYPE product_rec IS RECORD  
    (
        v_name  varchar2(20),
        v_price number(8,2)
    );
    v_product   product_rec;         
                        
begin
    select p.name, p.price
        into v_product
        from product p
        where p.pid = '1';
    
        DBMS_OUTPUT.PUT_LINE('商品名称:' || v_product.v_name);
        DBMS_OUTPUT.PUT_LINE('商品价格:' || v_product.v_price);
end;
/




形式2：利用 %rowtype 声明记录类型
示例：
declare
  v_product product%rowtype;
                        
begin
    select *
        into v_product
        from product p
        where p.pid = '1';
            
      DBMS_OUTPUT.PUT_LINE('商品名称:' || v_product.name);
      DBMS_OUTPUT.PUT_LINE('商品价格:' || v_product.price);
      DBMS_OUTPUT.PUT_LINE('商品数量:' || v_product.num);
      DBMS_OUTPUT.PUT_LINE('生产地址:' || v_product.address);
      DBMS_OUTPUT.PUT_LINE('商品分类:' || v_product.cid);
end;
/
```
**2. 表类型**
> 表类型类似其它语言中的数组类型，不过PL/SQL中的表类型下标可以为负值，也可以为字符串，并且元素个数没有限制.
```
示例1：
declare
  TYPE name_table_type IS TABLE OF product.name%type     --声明表类型 
    INDEX BY BINARY_INTEGER;   --表明索引是数字
  name_table  name_table_type;               --定义表类型变量 
  
begin
    select name
        into name_table(-1)
        from product
        where pid = '1';
        
        DBMS_OUTPUT.PUT_LINE('商品名称:' || name_table(-1));
end;
/


示例2：
declare
  TYPE name_table_type IS TABLE OF product%rowtype     --声明表类型 
    INDEX BY BINARY_INTEGER;
  name_table  name_table_type;    --定义表类型变量 
  
begin
    select *
        into name_table(-1)
        from product
        where pid = '1';
            
      DBMS_OUTPUT.PUT_LINE('商品名称:' || name_table(-1).name);
      DBMS_OUTPUT.PUT_LINE('商品价格:' || name_table(-1).price);
      DBMS_OUTPUT.PUT_LINE('商品数量:' || name_table(-1).num);
      DBMS_OUTPUT.PUT_LINE('生产地址:' || name_table(-1).address);
      DBMS_OUTPUT.PUT_LINE('商品分类:' || name_table(-1).cid);
end;
/

示例3：
declare
  TYPE str_table_type IS TABLE OF varchar2(20)       --声明表类型 
    INDEX BY varchar2(20);          --表明索引是字符
  str_table  str_table_type;        --定义表类型变量 
  
begin
    str_table('name')  := 'GTX1080Ti';
    str_table('price') := 6000; 
    str_table('num')   := 99;    
            
      DBMS_OUTPUT.PUT_LINE('商品名称:' || str_table('name'));
      DBMS_OUTPUT.PUT_LINE('商品价格:' || str_table('price'));
      DBMS_OUTPUT.PUT_LINE('商品数量:' || str_table('num'));
end;
/
```
**3. varray类型**
> 该类型和数组也很相似，它的元素个数需要限制，下标是从1开始的，适合较少的数据时使用
```
declare
  TYPE varr IS VARRAY(100) OF varchar2(20);    --声明varray类型
 
  str_array varr := varr('1','2');       --定义varray类型变量
  
begin
    str_array(1) := 'A';
    str_array(2) := 'B';     
            
        DBMS_OUTPUT.PUT_LINE(str_array(1));
    DBMS_OUTPUT.PUT_LINE(str_array(2));
end;
/
```
**参照类型**
> 引用类型是一个指向不同存储位置的指针，引用类型包含REF CURSOR和REF这两种。

**具体数据类型相关可参考[w3cplsql数据类型](https://www.w3cschool.cn/pl_sql/data_types.html)**
# 语法
### 判断体
![alt](https://img-blog.csdn.net/20170708193436811?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvaG9uXzN5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
==值得注意的是：eslif并没有写错的，它是少了一个e的==<br>

**例子**
```
 
使用if-else-end if显示今天星期几，是"工作日"还是"休息日"
declare
    pday varchar2(10);
begin
    select to_char(sysdate,'day') into pday from dual;
    dbms_output.put_line('今天是'||pday);
    if pday in ('星期六','星期日') then
    dbms_output.put_line('休息日');
    else
    dbms_output.put_line('工作日');
    end if;
end;
/
 
从键盘接收值，使用if-elsif-else-end if显示"age<16"，"age<30"，"age<60"，"age<80"
declare
    age number(3) := &age;
begin
    if age < 16 then
       dbms_output.put_line('你未成人');
    elsif age < 30 then
       dbms_output.put_line('你青年人');
    elsif age < 60 then
       dbms_output.put_line('你奋斗人');
    elsif age < 80 then 
       dbms_output.put_line('你享受人');
    else
       dbms_output.put_line('未完再继');
    end if;
end;
/
```
### 循环
> 在PLSQL中，循环的语法有三种<br>
- wuile 循环
- loop 循环
- for 循环 <br>

**WHILE循环：** <br>
while后面跟的是循环条件，与java的差不多，LOOP和END LOOP是关键字
```
 
WHILE  total  <= 25000  
 
LOOP
    total : = total + salary;
END  LOOP;
```
**LOOP循环：** <br>
exit后面的条件成立了才退出循环
```
Loop
   exit [when 条件成立];
   total:=total+salary;
end loop;
```
**FOR循环：** <br>
循环的递增只能是1，不能自定义步长
```
FOR   I   IN   1 . . 3  
 
LOOP
 
语句序列 ;
 
END    LOOP ; 
```
**例子使用**
```
 
使用loop循环显示1-10
declare
    i number(2) := 1;
begin
    loop
        --当i>10时，退出循环
        exit when i>10;
        --输出i的值
        dbms_output.put_line(i);
        --变量自加
        i := i + 1;  
    end loop;
end;
/
 
使用while循环显示1-10
declare
    i number(2) := 1;
begin
    while i<11 
    loop
        dbms_output.put_line(i);
        i := i + 1;
    end loop;
end;
/
 
使用while循环，向emp表中插入999条记录
declare
    i number(4) := 1;
begin 
    while( i < 1000 )
    loop
        insert into emp(empno,ename) values(i,'哈哈');
        i := i + 1;
    end loop;   
end;
/
 
使用while循环，从emp表中删除999条记录
declare
    i number(4) := 1;
begin 
    while i<1000
    loop
        delete from emp where empno = i;
        i := i + 1;
    end loop;
end;
/
 
使用for循环显示20-30
declare
    i number(2) := 20;
begin
    for i in 20 .. 30
    loop
        dbms_output.put_line(i);
    end loop;
end;
/
```
# 游标的使用
 详见  [游标介绍](https://www.yiibai.com/plsql/plsql_cursors.html)
 # 例外（异常）
 **我们在上面看PLSQL中的语法已经知道，有一个exception，这个在Oracle中称为例外，我们也可以简单看成就是Java中的异常。。。**
 > 语法
 ```
  
在declare节中定义例外   
out_of   exception ;
 
 在begin节中可行语句中抛出例外  
raise out_of ；
 
 在exception节处理例外
when out_of then …
 ```
 ![alt](https://img-blog.csdn.net/20170708194904998?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvaG9uXzN5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
 
 **例如：**
 ```
  
使用oracle系统内置例外，演示除0例外【zero_divide】
declare
    myresult number;
begin
    myresult := 1/0;
    dbms_output.put_line(myresult);
exception
    when zero_divide then 
     dbms_output.put_line('除数不能为0');
     delete from emp;  
end;
/
 
使用oracle系统内置例外，查询100号部门的员工姓名，演示没有找到数据【no_data_found】
declare
    pename varchar2(20);
begin
    select ename into pename from emp where deptno = 100;
    dbms_output.put_line(pename);
exception
    when NO_DATA_FOUND then 
     dbms_output.put_line('查无该部门员工');
     insert into emp(empno,ename) values(1111,'ERROR');
end;
/
 ```
 # 存储过程和存储函数
 在Oracle中，存储过程和存储函数的概念其实是差不多的，一般地，我们都可以混合使用。只不过有的时候有的情况使用过程好一些，有的情况时候函数的时候好一些。下面会讲解在什么时机使用过程还是函数的。

其实存储过程和函数就是类似与我们在Java中的函数的概念….

**到目前为止，我们的PLSQL是有几个缺点的：**<br>

- PLSQL不能将其封装起来，每次调用的时候都要将整片代码复制来调用
- 有的时候，我们想要将PLSQL的代码保存起来，只能自己手动保存在硬盘中，非常麻烦
- 我们学数据库就是为了让程序能够调用的，但是PLSQL不能让程序（java）调用<br>

**==因此，存储过程和存储函数就能解决上面的问题了，能够将代码封装起来，保存在数据库之中，让编程语言进行调用==**
![alt](https://img-blog.csdn.net/20170708195622190?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvaG9uXzN5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
### 存储过程与函数语法（略）

# 触发器
> 在PLSQL中也有个类似与我们Java Web中过滤器的概念，就是触发器…触发器的思想和Filter的思想几乎是一样的….
![alt](https://img-blog.csdn.net/20170711143404138?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvaG9uXzN5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

值得注意的是：对于触发器而言，是不针对查询操作的。也就是说：==触发器只针对删除、修改、插入操作！==
### 触发器语法
```
 
   CREATE  [or REPLACE] TRIGGER  触发器名
   {BEFORE | AFTER}
   { INSERT | DELETE|-----语句级
      UPDATE OF 列名}----行级
   ON  表名
 
    -- 遍历每一行记录
   [FOR EACH ROW]
   PLSQL 块【declare…begin…end;
   /
```
![alt](https://img-blog.csdn.net/20170711143647169?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvaG9uXzN5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
**例子1** <br>

1. 创建语句级触发器insertEmpTrigger，当对表【emp】进行增加【insert】操作前【before】，显示”hello world”
```
 
CREATE OR REPLACE TRIGGER insertempTiriger
BEFORE
INSERT
  ON EMP
  BEGIN
    dbms_output.put_line('helloword');
 
  END;
```
2. 调用：
```
 
INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (1, '2', '3', 4, NULL, NULL, NULL, 10);
```
3. 结果：
![alt](https://img-blog.csdn.net/20170711144022691?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvaG9uXzN5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

**例子2：**<br>
星期一到星期五，且9-20点能向数据库emp表插入数据，否则使用函数抛出异常, <br>
语法：raise_application_error(‘-20000’,’例外原因’)
```
 
CREATE OR REPLACE TRIGGER securityTrigger
BEFORE
INSERT
  ON EMP
  DECLARE
    pday  VARCHAR2(10);
    ptime NUMBER;
  BEGIN
    /*得到星期几*/
    SELECT to_char(sysdate, 'day')
    INTO pday
    FROM dual;
 
    /*得到时间*/
    SELECT to_char(sysdate, 'hh24')
    INTO ptime
    FROM dual;
 
    IF pday IN ('星期六', '星期日') OR ptime NOT BETWEEN 7 AND 23
    THEN
      RAISE_APPLICATION_ERROR('-20000', '非工作事件，请工作时间再来！');
 
    END IF;
 
  END;
```
插入数据、响应触发器：
```
 
INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (3, '2', '3', 4, NULL, NULL, NULL, 10);
```
![alt](https://img-blog.csdn.net/20170711145043194?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvaG9uXzN5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
**例子3**<br>
创建行级触发器checkSalaryTrigger，涨后工资这一列，确保大于涨前工资<br>
语法：==for each row/:new.sal/:old.sal== <br>
可以使用 **语法:new.sal和:old.sal** 来对比插入之前的值和插入之后的值
```
 
CREATE OR REPLACE TRIGGER checkSalTrigger
BEFORE
UPDATE OF sal
  ON EMP
FOR EACH ROW
  BEGIN
    IF :new.sal <= :old.sal
    THEN
      RAISE_APPLICATION_ERROR('-20001', '你涨的工资也太少了把！！！！');
 
    END IF;
 
  END;
```
**调用：**
```
 
UPDATE emp
SET sal = sal - 1
WHERE empno = 7369;
```
![alt](https://img-blog.csdn.net/20170711145459741?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
具体触发器可参考 [触发器讲解](http://www.codebaoku.com/it-oracle/it-oracle-238556.html#:~:text=Oracle%E8%A7%A6%E5%8F%91,QL%E7%A8%8B%E5%BA%8F%E4%BB%A3%E7%A0%81%E5%99%A8%E3%80%82)

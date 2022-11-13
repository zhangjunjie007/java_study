﻿## 1、编写一个 SQL 查询，查找所有至少连续出现三次的数字。
![在这里插入图片描述](https://img-blog.csdnimg.cn/eed319251f174f37826a817e2f1c8959.png)

### 答案1：

> **借此学习mysql定义变量的用法**

```sql
SELECT
   distinct	num AS ConsecutiveNums 
FROM
	(
	SELECT
		r.num,
	IF
		( @num = r.num, @i := @i + 1, @i := 1 ) count,
		@num := r.num tt 
	FROM
		( SELECT * FROM `Logs` l,( SELECT @i := 0 ) t ) r 
	) z 
WHERE
	count > 2;
```

### 答案2：

> 二维数组也可以用in

```sql
SELECT DISTINCT
	Num AS ConsecutiveNums 
FROM
	`Logs` 
WHERE
    	( Id + 1, Num ) IN ( SELECT * FROM `Logs` ) 
	AND ( Id + 2, Num ) IN (SELECT* FROM`Logs`);
```

## 2、[部门工资最高的员工](https://leetcode.cn/problems/department-highest-salary/)

答案一：[主要用到开窗函数](https://blog.csdn.net/mr__sun__/article/details/124257213)
**MySQL8.0以上版本才能使用窗口函数**

```sql
SELECT
	d.NAME Department,
	k.NAME EMPLOYEE,
	k.salary Salary 
FROM
	(
	SELECT
		p.* 
	FROM
		EMPLOYEE p,(
		SELECT
			g.NAME,
			g.salary,
			g.departmentId,
			ROW_NUMBER() over ( PARTITION BY g.departmentId ORDER BY g.salary DESC ) rn 
		FROM
			EMPLOYEE g 
		) kk -- 这一层查询为了查出每个部门最高的工资
	WHERE
		kk.salary = p.salary 
		AND kk.departmentId = p.departmentId 
		AND kk.rn = 1 
	) k,   -- 重新关联员工表是因为最高工资可能不止一个人
	DEPARTMENT d 
WHERE
	k.departmentId = d.id;
          
```

### 答案2：

```sql
SELECT
	d.NAME Department,
	g.NAME EMPLOYEE,
	g.salary Salary 
FROM
	EMPLOYEE g,
	Department d 
WHERE
	( g.departmentId,g.salary) IN (SELECT departmentId, max(salary) FROM EMPLOYEE  GROUP BY departmentId) 
	  -- 二维数组也可以用in 和上面分组以后获取有异曲同工之妙
	AND g.departmentId = d.id;
```


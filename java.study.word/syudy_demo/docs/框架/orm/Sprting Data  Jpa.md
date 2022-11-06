## 定义：
  JPA（Java Persistence API）即java持久化API，它的出现主要是为了简化持久层开发以及整合ORM技术，结束Hibernate、TopLink、JDO等ORM框架各自为营的局面。JPA是在吸收现有ORM框架的基础上发展而来，易于使用，伸缩性强。总的来说，JPA包括以下三方面的技术：

- ORM映射元数据：支持XML和注解两种元数据的形式，元数据描述对象和表之间的映射关系
- API：操作实体对象来执行CRUD操作
- 查询语言：通过面向对象而非面向数据库的查询语言查询数据，避免程序的SQL语句紧密耦合

> jpa 并不是一个框架，是一类框架的总称，持久层框架 Hibernate 是 jpa 的一个具体实现，本文要谈的 spring data jpa 又是在 Hibernate 的基础之上的封装实现。

## SpringBoot 整合Spring Data JPA
### **导入依赖**
```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
 </dependency>
```
### **数据库配置：**
```xml
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/chapter05?characterEncoding=utf8&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
#表示Jpa对应的数据库是mysql
spring.jpa.show-sql=true
#项目启动时根据实体类更新数据库中的表
spring.jpa.hibernate.ddl-auto=update
```
**spring.jpa.hibernate.ddl-auto:**

- create：每次运行程序时，都会重新创建表，故而数据会丢失
- create-drop：每次运行程序时会先创建表结构，然后程序结束时清空表
- update：每次运行程序，没有表时会创建表，如果对象发生改变会更新表结构，原有数据不会清空，只会更新（推荐使用）
- validate：运行程序会校验数据于数据库的字段类型是否相同，字段不同会报错
- none：禁用DDL处理

### Spring Data JPA的使用：

#### **创建实体类：**

```java
import lombok.Data;
 
import javax.persistence.*;
 
@Data
@Entity(name = "t_book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "book_name")
    private String name;
    @Column(name = "book_author")
    private String author;
    private Float price;
    @Transient
    private String description;
}
```

##### **代码解释：**

1. @Entity注解表示该类是一个实体类，在项目启动时会根据该类自动生成一张表，表的名称即@Entity注解中name的值，如果不配置name，默认表名为类名
2. 所有的实体类都要有主键，@Id注解表示该属性是一个主键，@GneeratedValue注解表示主键自动生成，strategy则表示主键的生成策略
3. 默认情况下，生成的表中字段的名称时实体类中属性的名称，通过@Column注解可以定制生成的字段的属性，name表示该属性对应的数据表中字段的名称，nullable表示该字段非空
4. @Transient注解表示在生成数据库的表时，该属性被忽略，即不生成对应的字段

##### **JPA自带的几种主键生成策略**

1. TABLE：使用一个特定的数据库表格来保存主键
2. SEQUENCE：根据底层数据库的序列来生成主键，条件是数据库支持序列。这个值要于generator一起使用，generator指定生成主键的生成器
3. IDENTITY：主键由数据库自动生成（主要支持自动增长的数据库，如mysql）
4. AUTO：主键由程序控制，也是GenerationType的默认值

#### Dao层：

​    **创建BookDao接口，继承JpaRepository，代码如下**

```java
import com.example.demo.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
 
import java.util.List;

@Component
public interface BookDao extends JpaRepository<Book,Integer> {
    //查询以某个字符开始的所有书
    List<Book> getBooksByAuthorStartingWith(String author);
    //查询单价大于某个值的所有书
    List<Book> getBooksByPriceGreaterThan(Float price);
    @Query(value = "select * from t_book where id=(select max(id) from t_book)",nativeQuery = true)
    Book getMaxIdBook();
    @Query("select b from t_book b where b.id>:id and b.author=:author")
    List<Book> getBookByIdAndAuthor(@Param("author") String author,@Param("id") Integer id);
    @Query("select b from t_book b where b.id<?2 and b.name like = ?1")
    List<Book> getBookByIdAndName(String name,Integer id);
}
```

##### 代码解释：

- 自定义BookDao继承自JpaRepositiory。JpaRepositiory中提供了一些基本的数据操作方法，有基本的增删改查、分页查询、排序查询等
- 在Spring Data JPA中，只要方法的定义符合既定规范，Spring Data就能分析出开发者意图，从而避免开发者定义SQL

#### Service层：

```java
import com.example.demo.dao.BookDao;
import com.example.demo.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
public class BookService {
    @Autowired
    BookDao bookDao;
 
    //save方法由JpaRepository接口提供
    public void addBook(Book book){
        bookDao.save(book);
    }
 
    //分页查询
    public Page<Book> getBookByPage(Pageable pageable){
        return bookDao.findAll(pageable);
    }
 
    public List<Book> getBooksByAuthorStartingWith(String author){
        return bookDao.getBooksByAuthorStartingWith(author);
    }
 
    public List<Book> getBooksByPriceGreaterThan(Float price){
        return bookDao.getBooksByPriceGreaterThan(price);
    }
 
    public Book getMaxIdBook(){
        return bookDao.getMaxIdBook();
    }
 
    public List<Book> getBookByIdAndName(String name,Integer id){
        return bookDao.getBookByIdAndName(name,id);
    }
 
    public List<Book> getBookByIdAndAuthor(String author,Integer id){
        return bookDao.getBookByIdAndAuthor(author,id);
    }
}
```

#### 补充（重点）：



---



##### 查询方式

######  1、使用关键字自定义查询
> 使用 jpa 提供的 find 和 get 关键字完成常规的查询操作,使用 delete 关键字完成删除，使用 count 关键字完成统计等

```java
public interface StudentRepository extends JpaRepository<Student,Integer> {

    // 查询数据库中指定名字的学生
    List<Student> findByName(String name);

    // 根据名字和年龄查询学生
    List<Student> getByNameAndAge(String name, Integer age);

    //删除指定名字的学生
    Long deleteByName(String name);

    // 统计指定名字学生的数量
    Long countByName(String name);

}
```



---



######  2、JPQL

> - 使用的 JPQL 的 sql 形式 from 后面是类名
> - ?1 代表是的是方法中的第一个参数

```java
    @Query("select s from ClassRoom s where s.name =?1")
    List<ClassRoom> findClassRoom1(String name);
```



---



###### 3、普通sql

> - 这是使用正常的 sql 语句去查询
>
> -  :name 是通过 @Param 注解去确定
>
>   ​    **sql 中的参数传递也有两种形式**：
>
>   1. 使用问号 ?，紧跟数字序列，数字序列从1 开始，如 ?1 接收第一个方法参数的值。
>   2. 使用冒号：，紧跟参数名，参数名是通过@Param 注解来确定。

```java
   @Query(nativeQuery = true,value = "select * from class_room c where c.name =:name")
    List<ClassRoom> findClassRoom2(@Param("name")String name);
```



---



###### 注意：@query 注解

> ​    jpa一般在单表增删改查的时候使用比较方便，如果涉及多个表，不如直接写sql来的方便。以上query注解中的sql，返回的只能用Map<String,Object>接收，用实体类映射不进去，后面需要手动映射到实体类中



例如以下代码：

```java
//Dao层：
    /**
     * 获取各部室模块使用情况
     * @return
     */
    @Query(value = "SELECT" +
            " t.`name` deptName," +
            " sum( CASE WHEN u.module_code = '01' THEN 1 ELSE 0 END ) policyCounts," +
            " sum( CASE WHEN u.module_code = '02' THEN 1 ELSE 0 END ) surveyCounts," +
            " sum( CASE WHEN u.module_code = '03' THEN 1 ELSE 0 END ) standardAssistantCounts," +
            " sum( CASE WHEN u.module_code = '04' THEN 1 ELSE 0 END ) surveyAssistantCounts," +
            " sum( CASE WHEN u.module_code = '05' THEN 1 ELSE 0 END ) waterMarkCameraCounts," +
            " sum( CASE WHEN u.module_code = '06' THEN 1 ELSE 0 END ) customerCollectionCounts  " +
            "FROM" +
            " module_use_info u," +
            " department t " +
            "WHERE" +
            " u.three_department_id = t.department_id " +
            "GROUP BY" +
            " t.`name`",nativeQuery = true)
    List<Map<String,Object>> getDeptModuleUse(); //这里返回的只能用这种方式接收
```

数据处理：

> 由于返回的是<String,Object> 类型，只能强转处理。如下图

![image-20221106083745783](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20221106083745783.png)

> 多表查询时也可以不用@Query注解，使用：
>
> 1. 一对一的关系，jpa 使用的注解是 @OneToOne
>
> 2. 一对多的关系，jpa 使用的注解是 @OneToMany
>
> 3. 多对一的关系，jpa 使用的注解是 @ManyToOne
>
> 4. 多对多的关系，jpa 使用的注解是 @ManyToMany
>
>    ​           使用起来相对麻烦，不如直接使用mybatis，了解的话可自行百度



###### **4、Specification 的使用**

> 使用findAll()查询时，可以使用Specification  动态新增查询条件
>
> ​       适合用于单表查询，在findAll() 中分页查询

```java
    /**
     * 14  * 组装查询条件
     * 15  * @param user -查询相关对象
     * 16  * @return 返回组装过的多查询条件
     * 17
     */
     
    private Specification<ReportCase> getSpecification(GetReportCaseVO caseVO) {
        Specification<ReportCase> specification = new Specification<ReportCase>() {
            @Override
            public Predicate toPredicate(Root<ReportCase> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Path<Object> insuredName = root.get("insuredName");
                    Path<Object> identifynumber = root.get("identifynumber");
                    Path<Object> registNo = root.get("registNo");
                    Path<Object> caseStatus = root.get("caseStatus");

                List<Predicate> predicates = new ArrayList<>();
                    // 判断条件不为空
                    if (!StringUtils.isEmpty(caseVO.getInsuredName())) {
                        Predicate predicate = criteriaBuilder.equal(insuredName, caseVO.getInsuredName());
                        predicates.add(predicate);
                    }
                // 等值查询
                    if (!StringUtils.isEmpty(caseVO.getIdentifynumber())) {
                        Predicate predicate2 = criteriaBuilder.equal(identifynumber, caseVO.getIdentifynumber());
                        predicates.add(predicate2);
                    }
                // 模糊查询
                    if (!StringUtils.isEmpty(caseVO.getRegistNo())) {
                        Predicate predicate3 = criteriaBuilder.like(registNo.as(String.class), "%"+caseVO.getRegistNo()+"%");
                        predicates.add(predicate3);
                    }

                    Predicate predicate4 = criteriaBuilder.equal(caseStatus, "1");
                    predicates.add(predicate4);
                
                // 组装所有的查询条件
                     return  criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
        };
        return specification;
    }
```

###### 5、分页查询

> 分页时，组建Pageable 对象
>
>            1. 排序设置
>
> ​          Sort id = Sort.by(Sort.Direction.DESC,"id");
>
> 2. 分页参数设置（Jpa分页从0开始）
>
> ​          PageRequest pageRequest = PageRequest.of(reportCaseVO.getPageNo()-1, reportCaseVO.getPageSize(), id);
>
> ​                                                                                                                                                           --这里的id是sort对象
>
>   3、把PageRequest 传入 Dao的查询语句中去，注意传的是 Pageable，不能直接传PageRequest 
>
> ​                                                                                                                Pageable 是PageRequest 的父类

```java
 /**
     * 分页查询我的报案
     * @param reportCaseVO
     * @return
     */
    @Override
    public ReportCaseResponse getUserReportCases(GetReportCaseVO reportCaseVO) {
       //1、排序规则
        Sort id = Sort.by(Sort.Direction.DESC,"id");

        //2、因为Jpa分页参数默认从0开始 所以来的要-1
        PageRequest pageRequest = PageRequest.of(reportCaseVO.getPageNo()-1, reportCaseVO.getPageSize(), id);
        
        //3、根据入参条件，获取Specification （上面的方法）
        Specification<ReportCase> specification = getSpecification(reportCaseVO);
        
        //4、调用查询接口
        Page<ReportCase> all= caseRepository.findAll(specification,pageRequest);
        
        //组装参数
        List<ReportCase> reportCaseList = all.getContent();
        int number = (int) all.getTotalElements();

        CtPagination pagination = new CtPagination(reportCaseVO.getPageNo(), reportCaseVO.getPageSize(), number);
        ReportCaseResponse reportCaseResponse = new ReportCaseResponse();
        reportCaseResponse.setReportCaseList(reportCaseList);
        reportCaseResponse.setPagination(pagination);
        return reportCaseResponse;
    }

```



> ​     分页对象类型是 Pageable ，不要直接写 PageRequest，会不生效

```java
    /**
     *  对应的 findAll()
          注意：分页对象类型是 Pageable ，不要直接写 PageRequest，会不生效
     * @param specification
     * @param pageRequest
     * @return
     */
    Page<ReportCase> findAll(Specification<ReportCase> specification, Pageable pageRequest);
```

###### 6、动态sql查询

1、 使用Specification 动态拼接查询条件（适用于单表）

2、如下图：使用是否为null 来处理。

​                      但是这种情况前端经常会传送空串，在mysql里空串和null是两个概念，所以如果没有值的话让前端不要传这个属性，否则传空串的话默认会用空串去匹配

​                      模糊匹配时使用：`a.user_code like CONCAT('%',?4,'%') ` 方式拼接

![image-20221106093646374](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20221106093646374.png)

3、EntityManager 的使用

​      可以使用EntityManager  执行原生sql，原生sql可以根据 前端传的哪些入参是有值的，利用StringBuilder动态拼接，然后执行

```java
/**
 * 实体类管理器（动态注入表名）
 *
 * @author jtt
 * @date [2022-09-09]
 */
@Slf4j
// 加入spring容器管理
@Component
public class EntityManagerRepo {

    // 在持久层注入
    @PersistenceContext
    EntityManager entityManager;

    public List<LabelValue> select(String tableName, String labelFieldName, String valueFieldName) {
        // 可使用字符串拼接
          StringBuilder sql_where = new StringBuilder();
        eg:
         if(StringUtils.isNotBlank(param1)){
             sql_where.append("where ...");  //按此种方式拼接
         }
        
        String sql = "select " + labelFieldName + " as label, " + valueFieldName + " as value from " + tableName + sql_where.toString() ;
        //执行，获取结果（前面也可以设置分页条件，手动加进去）
        List<LabelValue> resultList = entityManager.createNativeQuery(sql).getResultList();
        return resultList;
    }
}
```








# <center><font size = 50>前端学习笔记 </font>

## 知识点1 ：js中代码块要不都执行要不都不执行
```java
    {
       console.log(123);
       alert(234);             
     }      
     //这两行代码写在一个括号内以后，程序要么都会执行，要么都不会
    
      当然，遇到异常后面的也会终止执行 例如 
    {
        alert(123);
        a();  //a 方法未定义  会报异常 下面的代码就不再执行了
        alert(456);
    }
```
## 知识点2：js 对象属性的删除
```javascript                     /*
			 * 删除对象的属性
			 * 	语法：delete 对象.属性名
			 */
			delete obj.name;
```

## 知识点3：立即执行函数 ,不用定义函数名名称 只执行一次
```javascript
	//函数对象()
			/*
			 * 立即执行函数
			 * 	函数定义完，立即被调用，这种函数叫做立即执行函数
			 * 	立即执行函数往往只会执行一次
			 */
			/*(function(){
				alert("我是一个匿名函数~~~");
			})();*/
			
			(function(a,b){
				console.log("a = "+a);
				console.log("b = "+b);
			})(123,456);
```


## 知识点4：获取未知对象的全部属性以及属性值  可以用 for  in  循环
```javascript
           var obj = {
						name:"孙悟空",
						age:18,
						gender:"男",
						address:"花果山"
					 };
					 
			//枚举对象中的属性
			//使用for ... in 语句
			/*
			 * 语法：
			 * 	for(var 变量 in 对象){
			 * 	
			 *  }
			 * 
			 * for...in语句 对象中有几个属性，循环体就会执行几次
			 * 	每次执行时，会将对象中的一个属性的名字赋值给变量
			 */
			
			for(var n in obj){
				console.log("属性名:"+n);
				
				console.log("属性值:"+obj[n]);
			}
```


## 知识点5：js中  this的使用  
```javascript
                   /*
			 * 解析器在调用函数每次都会向函数内部传递进一个隐含的参数,
			 * 	这个隐含的参数就是this，this指向的是一个对象，
			 * 	这个对象我们称为函数执行的 上下文对象，
			 * 	根据函数的调用方式的不同，this会指向不同的对象
			 * 		1.以函数的形式调用时，this永远都是window
			 * 		2.以方法的形式调用时，this就是调用方法的那个对象
    
                                         说白了谁调用方法（函数） this就是谁
			 */
			
			function fun(){
				//console.log("a = "+a+", b = "+b);
				console.log(this.name);
			}
			
			//fun();
			
			//创建一个对象
			var obj = {
				name:"孙悟空",
				sayName:fun
			};
			
			var obj2 = {
				name:"沙和尚",
				sayName:fun
			};
			
			//console.log(obj.sayName == fun);
			var name = "全局的name属性";
			//obj.sayName();
			//以函数形式调用，this是window
			//fun();
			
			//以方法的形式调用，this是调用方法的对象
			//obj.sayName();
			obj2.sayName();

```

## 知识点6：函数原型的理解
```javascript
   说白了就是函数作为构造函数使用时，他的原型对象就会被该构造函数所创建出来的对象共同使用，每个对象获取属性或者方法时，如果对象本身没有该属性都会去原型里面找，（就和java的父类有点像）；
    利用这一点，以后我们创建构造函数时，可以将这些对象共有的属性和方法，统一添加到构造函数的原型对象中，
	这样不用分别为每一个对象添加，也不会影响到全局作用域，就可以使每个对象都具有这些属性和方法了

函数的额原型对象是： prototype   例如：MyClass.prototype
对象的是：mc2.__proto__   ；     mc2是函数MyClass的对象
                      /*
			 * 原型 prototype
			 * 
			 * 	我们所创建的每一个函数，解析器都会向函数中添加一个属性prototype
			 * 		这个属性对应着一个对象，这个对象就是我们所谓的原型对象
			 * 	如果函数作为普通函数调用prototype没有任何作用
    
			 * 	当函数以构造函数的形式调用时，它所创建的对象中都会有一个隐含的属性，
			 * 		指向该构造函数的原型对象，我们可以通过__proto__来访问该属性
			 * 
			 * 	原型对象就相当于一个公共的区域，所有同一个类的实例都可以访问到这个原型对象，
			 * 		我们可以将对象中共有的内容，统一设置到原型对象中。
			 * 
			 * 当我们访问对象的一个属性或方法时，它会先在对象自身中寻找，如果有则直接使用，
			 * 	如果没有则会去原型对象中寻找，如果找到则直接使用
			 * 
			 * 以后我们创建构造函数时，可以将这些对象共有的属性和方法，统一添加到构造函数的原型对象中，
			 * 	这样不用分别为每一个对象添加，也不会影响到全局作用域，就可以使每个对象都具有这些属性和方法了
			 */
			
			function MyClass(){
				
			}
			
			//向MyClass的原型中添加属性a
			MyClass.prototype.a = 123;
			
			//向MyClass的原型中添加一个方法
			MyClass.prototype.sayHello = function(){
				alert("hello");
			};
			
			var mc = new MyClass();
			
			var mc2 = new MyClass();
			
			//console.log(MyClass.prototype);
			//console.log(mc2.__proto__ == MyClass.prototype);
			
			//向mc中添加a属性
			mc.a = "我是mc中的a";
			
			//console.log(mc2.a);
			
			mc.sayHello();
			
```


## 知识点7：原型的补充
```javascript
             重点：原型对象也是对象，他也有对应的原型，但是不会一直找下去 ，Object对象的原型没有原型，如果在Object原型中依然没有找到，
                          则返回 undefined
           判断对象是否含有某个属性  可以用  in  和  hasOwnProperty 详细见下面具体分析

                       /*
			 * 创建一个构造函数
			 */
			function MyClass(){
				
			}
			
			//向MyClass的原型中添加一个name属性
			MyClass.prototype.name = "我是原型中的名字";
			
			var mc = new MyClass();
			mc.age = 18;
			
			//console.log(mc.name);
			
			//使用in检查对象中是否含有某个属性时，如果对象中没有但是原型中有，也会返回true
			//console.log("name" in mc);
			
			//可以使用对象的hasOwnProperty()来检查对象自身中是否含有该属性
			//使用该方法只有当对象自身中含有属性时，才会返回true
			//console.log(mc.hasOwnProperty("age"));
			
			//console.log(mc.hasOwnProperty("hasOwnProperty"));
			
			/*
			 * 原型对象也是对象，所以它也有原型，
			 * 	当我们使用一个对象的属性或方法时，会现在自身中寻找，
			 * 		自身中如果有，则直接使用，
			 * 		如果没有则去原型对象中寻找，如果原型对象中有，则使用，
			 * 		如果没有则去原型的原型中寻找,直到找到Object对象的原型，
			 * 		Object对象的原型没有原型，如果在Object原型中依然没有找到，则返回undefined
			 */
			
			//console.log(mc.__proto__.hasOwnProperty("hasOwnProperty"));
			
			//console.log(mc.__proto__.__proto__.hasOwnProperty("hasOwnProperty"));
			
			//console.log(mc.__proto__.__proto__.__proto__);
			
			//console.log(mc.hello);
			
			//console.log(mc.__proto__.__proto__.__proto__)

```

## 知识点8：tostring（）；
```javascript
              每次我门在console.log()的时候其实会调用的对象的tostring（）。该方法不是对象本身的，也不是原型的，而是原型的原型的，也就是object对象的，如果想要不弹出【object,object】 可以重写原型的toString（），详见下面分析

               function Person(name , age , gender){
				this.name = name;
				this.age = age;
				this.gender = gender;
			}
			
			//修改Person原型的toString
			Person.prototype.toString = function(){
				return "Person[name="+this.name+",age="+this.age+",gender="+this.gender+"]";
			};
			
			
			//创建一个Person实例
			var per = new Person("孙悟空",18,"男");
			var per2 = new Person("猪八戒",28,"男");
			
			//当我们直接在页面中打印一个对象时，事件上是输出的对象的toString()方法的返回值
			//如果我们希望在输出对象时不输出[object Object]，可以为对象添加一个toString()方法
			//Person[name=孙悟空,age=18,gender=男]
			/*per.toString = function(){
				return "Person[name="+this.name+",age="+this.age+",gender="+this.gender+"]";
			};*/
			
			var result = per.toString();
			//console.log("result = " + result);
			//console.log(per.__proto__.__proto__.hasOwnProperty("toString"));
			console.log(per2);
			console.log(per);
```

## 知识点9：数组小tab
```javascript
	/*
			 * 内建对象
			 * 宿主对象
			 * 自定义对象
			 * 
			 * 数组（Array）
			 * 	- 数组也是一个对象
			 * 	- 它和我们普通对象功能类似，也是用来存储一些值的
			 * 	- 不同的是普通对象是使用字符串作为属性名的，
			 * 		而数组时使用数字来作为索引操作元素
			 * 	- 索引：
			 * 		从0开始的整数就是索引
			 * 	- 数组的存储性能比普通对象要好，在开发中我们经常使用数组来存储一些数据
			 */
			
			//创建数组对象
			var arr = new Array();
			
			//使用typeof检查一个数组时，会返回object
			//console.log(typeof arr);
			
			/*
			 * 向数组中添加元素
			 * 语法：数组[索引] = 值
			 */
			arr[0] = 10;
			arr[1] = 33;
			arr[2] = 22;
			arr[3] = 44;
			/*arr[10] = 31;
			arr[100] = 90;*/
			
			/*
			 * 读取数组中的元素
			 * 语法：数组[索引]
			 * 	如果读取不存在的索引，他不会报错而是返回undefined
			 */
			
			//console.log(arr[3]);
			
			/*
			 * 获取数组的长度
			 * 可以使用length属性来获取数组的长度(元素的个数)
			 * 	语法：数组.length
			 * 
			 * 对于连续的数组，使用length可以获取到数组的长度（元素的个数）
			 * 对于非连续的数组，使用length会获取到数组的最大的索引+1
			 * 		尽量不要创建非连续的数组
			 */
			/*console.log(arr.length);
			console.log(arr);*/
			
			/*
			 * 修改length
			 * 	如果修改的length大于原长度，则多出部分会空出来
			 *  如果修改的length小于原长度，则多出的元素会被删除
			 */
			//arr.length = 10;
			
			/*arr.length = 2;
			
			console.log(arr.length);
			console.log(arr);*/
			
			arr[4] = 50;
			arr[5] = 60;
			
			//向数组的最后一个位置添加元素
			//语法：数组[数组.length] = 值;
			arr[arr.length] = 70;
			arr[arr.length] = 80;
			arr[arr.length] = 90;
			
			console.log(arr);
```

## 知识点10：js中数组的过滤
 可参考： [参考1](https://m.py.cn/web/js/22635.html)
 &emsp;&emsp;&emsp;&emsp;&nbsp;[官方参考2](https://www.runoob.com/jsref/jsref-filter.html)
  &emsp;&emsp;&emsp;&emsp;&nbsp;[参考3](https://www.jb51.net/article/82429.htm)
```javascript
  
    /**
    *   注意：当操作dom 时 遍历的方法和这个不太一样 见后半段
    */
    
    function Person(name, age, gender) {
        this.name = name;
        this.age = age;
    }
    var per = new Person("孙悟空", 18);
    var per2 = new Person("猪八戒", 28);
    var per3 = new Person("红孩儿", 8);
    var per4 = new Person("蜘蛛精", 16);
    var per5 = new Person("二郎神", 38);
    var arr = [per, per2, per3, per4, per5];
    
    let result =  arr.filter(e =>e.age>30);
    console.log(result);
    
    --如果是dom元素进行遍历过滤的话
    
   
    注意：dom元素进行过滤的话 filter() 中参数不一样 意义也不一样  注意用法
```
## 知识点11：数组的遍历
```javascript

                  注意：单纯的数组数据过滤或者遍历 function（）内的方法是一样的，如果涉及dom另外使用其他的
                     /*
			 * 一般我们都是使用for循环去遍历数组，
			 * 	JS中还为我们提供了一个方法，用来遍历数组
			 * forEach()
			 * 		- 这个方法只支持IE8以上的浏览器
			 * 			IE8及以下的浏览器均不支持该方法，所以如果需要兼容IE8，则不要使用forEach
			 * 			还是使用for循环来遍历
			 */
			
			//创建一个数组
			var arr = ["孙悟空","猪八戒","沙和尚","唐僧","白骨精"];
			
			/*
			 * forEach()方法需要一个函数作为参数
			 * 	- 像这种函数，由我们创建但是不由我们调用的，我们称为回调函数
			 * 	- 数组中有几个元素函数就会执行几次，每次执行时，浏览器会将遍历到的元素
			 * 		以实参的形式传递进来，我们可以来定义形参，来读取这些内容
			 * 	- 浏览器会在回调函数中传递三个参数：
			 * 		第一个参数，就是当前正在遍历的元素
			 * 		第二个参数，就是当前正在遍历的元素的索引
			 * 		第三个参数，就是正在遍历的数组
			 * 		
			 */
			arr.forEach(function(value , index , obj){
				console.log(value);
			});
   
  ```

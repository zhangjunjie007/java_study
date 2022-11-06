# JUC并发编程学习

# 基础回顾

## 什么是JUC？

> JUC实际上就是我们对于jdk中**java.util .concurrent**工具包的简称。这个包下都是Java处理线程相关的类，自jdk1.5后出现。

## 线程和进程

- 进程：指的是操作系统进行调度的基本单位，一个程序的运行就是一个进程。

- 线程：指的是处理器进行资源调度的基本单位，也是程序执行的最小单位，一个进程包括一个或多个线程。

## 并发、并行

并发：多线程操作同一个资源

- CPU一核：模拟出来多条线程，快速交替

并行：多个人一起行走

- CPU多核：多个线程可以同时执行，线程池

```java
package com.zjj.juc_study;

public class testCpuNum {
    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
}
```

并发编程的本质：**充分利用CPU的资源**

## 线程的几种状态

```java
public enum State {
   //新生
    NEW,

   //运行
    RUNNABLE,

  //阻塞
    BLOCKED,
    
   //等待
    WAITING,

   //超时等待
    TIMED_WAITING,

    //终止
    TERMINATED;
}
```

**注意：操作系统线程有`5种`状态，Java有六种**

## wait/sleep 区别

**共同点：**

​       都是使线程暂停一段时间的方法。

**区别**

1. 来自不同的类

   >   wait => Object
   >
   >  sleep =>Thread 

2. 关于锁的释放

   > wait 会释放锁
   >
   >  sleep 睡觉了，抱着锁睡觉，不会释放！

3. 使用的范围不同

   > wait:
   >
   >    wait，notify和notifyAll只能在同步控制方法或者同步控制块里面使用
   >
   > sleep：
   >
   >    可以在任何地方睡

   sleep不出让系统资源；

   wait是进入线程等待池等待，出让系统资源，其他线程可以占用CPU。

   > 一般wait不会加时间限制，因为如果wait线程的运行资源不够，再出来也没用，要等待其他线程调用notify/notifyAll唤醒等待池中的所有线程，才会进入就绪队列等待OS分配系统资源。sleep(milliseconds)可以用时间指定使它自动唤醒过来，如果时间不到只能调用interrupt()强行打断。

   ==Thread.Sleep(0)的作用是“触发操作系统立刻重新进行一次CPU竞争”。==

   

4. 是否需要捕获异常

   > 都要捕获异常

## synchronized 和 lock 使用

### syschronized

> syschronized  使用 可以在方法或者代码块，代码块使用时 注意锁的对象

**这里需要注意创建多线程的方式，没有让资源类实现runnable接口或者继承thread类，就作为单纯的一个资源**

```java
package com.zjj.juc_study._831study;

public class syncDemo01 {
    public static void main(String[] args) {
        /**
         * 之前写多线程时会让 ticket 实现runnable接口 然后利用 new Thread(new ticket(),"a").start() 此方式 创建多线程
         * 此种方式不利于解耦合  ticket 作为一个资源类 不应该附加其他的操作，所以用如下方式进行创建
         */
        //创建资源类对象
        ticket ticket = new ticket();
        //创建三个线程 对资源类进行操作
        new Thread(()->{
           for (int i = 0; i < 30; i++) {
               ticket.sale();
           }
       },"A").start();
        new Thread(()->{
            for (int i = 0; i < 30; i++) {
                ticket.sale();
            }
        },"B").start();
        new Thread(()->{
            for (int i = 0; i < 30; i++) {
                ticket.sale();
            }
        },"C").start();
        // 加上 synchronized 解决多线程操作时的并发问题
    }

    /**
     * 内部类
     */
    static class ticket {
        private int tickets = 20;
        //卖票
      public /*synchronized*/  void  sale(){
          synchronized (this){ //锁的时候必须是同一个对象
              if (tickets>0){
                  System.out.println(Thread.currentThread().getName()+"正在买第"+tickets--+"张票，目前还剩余"+tickets+"张票");
              }
          }

      }
    }
}

```

### lock

```java
package com.zjj.juc_study._831study;

import java.util.concurrent.locks.ReentrantLock;

public class lockDemo02 {
    public static void main(String[] args) {

        //创建资源类对象
        ticket ticket = new ticket();
        //创建三个线程 对资源类进行操作
        new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                ticket.sale();
            }
        }, "A").start();
        new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                ticket.sale();
            }
        }, "B").start();
        new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                ticket.sale();
            }
        }, "C").start();
        // 加上 lock  解决多线程操作时的并发问题
    }

    /**
     * 内部类
     */
    static class ticket {
        private int tickets = 20;
        /**
         * 创建 ReentrantLock 对象是 可以指定是否使用公平锁/非公平锁
         *   公平锁：一个线程占有锁 其他线程等待 无论其他线程是否一秒完事 严格的先来后到
         *   非公平锁： 可以插队  （默认）
         */


        /**
         *   Lock 使用三步曲
         *   1、创建 ReentrantLock 对象
         *   2、reentrantLock.lock()
         *   3、finally => reentrantLock.unlock()
         */
        ReentrantLock reentrantLock = new ReentrantLock();

        //卖票
        public void sale() {
            //这里采用Lock 的方式解决并发
            reentrantLock.lock();
            try {
                if (tickets > 0) {
                    System.out.println(Thread.currentThread().getName() + "正在买第" + tickets-- + "张票，目前还剩余" + tickets + "张票");
                }
            } finally {
                reentrantLock.unlock();
            }

        }
    }
}

```



**创建reentrantLock对象时，可以指定是否使用公平锁，不指定的话默认是非公平锁**

**公平锁**：就是很公平，在并发环境中，每个线程在获取时会先查看此锁维护的队列，如果为空，或者当前线程是等待队列的第一个就占有锁，否则就会加入到队列中，以后会按照FIFO的规则从队列中取到自己。

**非公平锁**：非公平锁比较粗鲁，上来就直接尝试占有锁，如果尝试失败，就再采用类似公平锁那种方式。

```java
// 非公平锁
Lock lock = new ReentrantLock(); 

// 公平锁
Lock lock = new ReentrantLock(true);
```



### lock 和 syschronized 对比

- Synchronized 是java 内置的关键字， Lock是一个java类

- Synchronized 无法判断获取锁的状态，Lock可以判断是否获取到了锁

- Synchronized 会自动释放锁，Lock必须要手动释放锁，否则会造成**死锁**

- Synchronized 线程1（获得锁 阻塞）线程2（傻傻的等），Lock锁不一定会一直等下去
- Synchronized ==可重入锁== ，不可以中断，非公平。Lock  ，==可重入锁== 可以判断锁  可以自己设置是否公平
- Synchronized 适合锁少量的代码同步问题，Lock适合锁大量的同步代码



> 什么是可重入锁？

的是同一个线程外层函数获得锁之后，内层递归函数仍然能获取该锁的代码，在同一个线程在外层方法获取锁的时候，在进入内层方法会自动获取锁。

也就是说，线程可以进入任何一个他已经拥有锁的所有同步代码块

[具体参考此代码demo](https://zhuanlan.zhihu.com/p/285252463)

## 生产者消费者问题

### Synchronized 实现

> Synchronized 实现两个线程交替执行

```java
package com.zjj.juc_study._901study;

/**
 * 线程之间的通信  等待唤醒 ，通知唤醒
 * 生产者消费者模型  两个线程交替执行操作一个变量  线程A num++   线程B  num--
 * 等待 、业务、通知
 */
public class SyncDemo01 {
    public static void main(String[] args) {
        Data data = new Data();

        //两个线程 交替操作一个变量
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data.increment();
            }
        },"AAA").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data.decrement();
            }
        },"BBB").start();
    }


    static class Data {
        private int number = 0;

        //增加库存
        private synchronized void increment() {
            if (number != 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            number++;
            System.out.println("线程" + Thread.currentThread().getName() + "====正在执行新增操作，number的值为:"+number);
            this.notifyAll();
        }

        //删减库存
        private synchronized void decrement() {
            if (number != 1) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            number--;
            System.out.println("线程" + Thread.currentThread().getName() + "====正在执行刪減操作number的值为:"+number);
            this.notifyAll();
        }
    }
}

```

**问题：如果多个生产者，多个消费者同时执行上述代码会有什么问题？怎么解决**

> 会造成虚假唤醒，导致生产者消费者并不会正常生产和消费
>
>   需要把同步方法中的if换成while   

![image-20220901211034998](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220901211034998.png)

[详细可参考：什么是线程的虚假唤醒](https://blog.csdn.net/Saintmm/article/details/121092830)

替换以后代码为：

```java
package com.zjj.juc_study._901study;

public class SyncDemo02 {
    public static void main(String[] args) {
        Data data = new Data();

        //如果多个线程交替操作这个变量 也就是同时存在两个生产者两个消费者
        //    会造成虚假唤醒  这个时候把if改成while 就解决了
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data.increment();
            }
        },"AAA").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data.decrement();
            }
        },"BBB").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data.increment();
            }
        },"CCC").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data.decrement();
            }
        },"DDD").start();
    }


    static class Data {
        private volatile int  number = 0;

        //增加库存
        private synchronized void increment() {
            while (number != 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            number++;
            System.out.println("线程" + Thread.currentThread().getName() + "====正在执行新增操作，number的值为:"+number);
            this.notifyAll();
        }

        //删减库存
        private synchronized void decrement() {
            while (number != 1) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            number--;
            System.out.println("线程" + Thread.currentThread().getName() + "====正在执行刪減操作number的值为:"+number);
            this.notifyAll();
        }
    }
}

```

### Lock锁实现

Lock体系中有配套的对线程睡眠唤醒的一套机制

==注意：这里等待或者唤醒的时候，只创建了一个condition对象，依然可以实现生产者消费者关系==

**这种方式实现也会有虚假唤醒，所以也要用while 来处理**

```java
package com.zjj.juc_study._901study;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者消费者  用 Lock 锁进行实现 休眠->唤醒->休眠 ，需要注意的是 这里不用 while 的话同样会造成虚假唤醒
 */
public class LockDemo01 {
    public static void main(String[] args) {
        Data data = new Data();

        //两个线程 交替操作一个变量   用condition 同样会造成虚假唤醒
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data.increment();
            }
        },"AAA").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data.decrement();
            }
        },"BBB").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data.increment();
            }
        },"CCC").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                data.decrement();
            }
        },"DDD").start();
    }


    static class Data {
        private int number = 0;
        private ReentrantLock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();//注意这里只用了一个condition对象

        //增加库存
        private void increment() {

            // 1、加锁
            lock.lock();
            // 2、业务代码
            try {
                while (number != 0) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                number++;

                System.out.println("线程" + Thread.currentThread().getName() + "====正在执行新增操作，number的值为:" + number);
                condition.signal();
            } finally {
                // 3、 释放锁
                lock.unlock();
            }
        }

        //删减库存
        private void decrement() {
            lock.lock();
            try {
                while (number != 1) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                number--;
                System.out.println("线程" + Thread.currentThread().getName() + "====正在执行刪減操作number的值为:" + number);
                condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}



```

**问题：lock锁是不是只能实现Synchronized的等待唤醒，还有别的高级的作用吗**

> 答案：有的，可以实现线程间的精准通知

**实现多个线程之间精准通知，就要有多个condition 对象，重点是如下代码**

![](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220901212120876.png)



**如截图：2等待唤醒3，3等待唤醒4 ，以此实现线程之间的精准通知**

```java
package com.zjj.juc_study._901study;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者消费者  用 Lock 锁进行实现 休眠->唤醒->休眠 ，需要注意的是 这里不用 while 的话同样会造成虚假唤醒
 * 多个condition 实现线程精准执行
 */
public class LockDemo02 {
    public static void main(String[] args) {
        Data data = new Data();

        //两个线程 交替操作一个变量   用condition 同样会造成虚假唤醒
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data.meth1();
            }
        }, "AAA").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data.meth2();
            }
        }, "BBB").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data.meth3();
            }
        }, "CCC").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data.meth4();
            }
        }, "DDD").start();
    }


    static class Data {
        private int number = 0;
        private ReentrantLock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();//这里只用了多个condition对象
        private Condition condition2 = lock.newCondition();
        private Condition condition3 = lock.newCondition();
        private Condition condition4 = lock.newCondition();



        //增加库存
        private void meth1() {

            // 1、加锁
            lock.lock();
            // 2、业务代码
            try {
                while (number != 0) {
                    condition.await();
                }
                number++;
                System.out.println("线程"+Thread.currentThread().getName()+"====正在执行");
                condition2.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 3、 释放锁
                lock.unlock();
            }
        }

        //删减库存
        private void meth2() {
            // 1、加锁
            lock.lock();
            // 2、业务代码
            try {
                while (number != 1) {
                    condition2.await();
                }
                number--;
                System.out.println("线程"+Thread.currentThread().getName()+"====正在执行");
                condition3.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 3、 释放锁
                lock.unlock();
            }
        }

        //增加库存
        private void meth3() {

            // 1、加锁
            lock.lock();
            // 2、业务代码
            try {
                while (number != 0) {
                    condition3.await();
                }
                number++;
                System.out.println("线程"+Thread.currentThread().getName()+"====正在执行");
                condition4.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 3、 释放锁
                lock.unlock();
            }
        }

        //删减库存
        private void meth4() {
            // 1、加锁
            lock.lock();
            // 2、业务代码
            try {
                while (number != 1) {
                    condition4.await();
                }
                number--;
                System.out.println("线程"+Thread.currentThread().getName()+"====正在执行");
                condition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 3、 释放锁
                lock.unlock();
            }
        }
    }
}



```

## 八锁现象

### 1、一个对象两个同步方法

> 谁先抢到谁先执行

```java
package com.zjj.juc_study._904study;

import java.util.concurrent.TimeUnit;

/**
 * 8锁现象  就是关于锁的八个问题
 *  1、标准情况下 两个线程先执行 打电话 还是 发短信呢？ // 答案：发短信
 *  2、发短信延迟4秒 先执行 打电话 还是发短信 // 答案：发短信
 *  分析：
 *      1、Synchronized 方法 锁的是对象 ，是方法的调用者。两个方法是同一把锁
 *      2、在执行第一个方法的时候，锁被占用，没有释放，自然第二个方法就执行不了 （谁先拿到锁谁先执行）
 */
public class Lock_8_demo01 {
    public static void main(String[] args) {
        Phone phone = new Phone();

        new Thread(()->{
            phone.send();
        }).start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(()->{
            phone.call();
        }).start();
    }
}


class Phone{

    public synchronized void send(){
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("发短信");
    }
    public synchronized void call(){
        System.out.println("打电话");
    }
}
```

### 2、两个对象，两个同步方法

> 两个同步方法的锁对象没有关系，由于发短信有延迟，所以打电话先执行

```java
package com.zjj.juc_study._904study;

import java.util.concurrent.TimeUnit;

/**
 * 8锁现象  就是关于锁的八个问题
 *  1、增加一个普通方法后，  先执行 打电话 还是 发短信还是吃大餐呢？ // 答案：吃大餐
 *  2、两个对象，两个同步方法， 先执行哪个？ //答案：打电话
 *  分析：
 *    1、第一个先执行吃大餐，是因为不是同步方法，没有锁的限制，另外两个同步方法还是按谁先占有锁谁先执行
 *    2、第二个先执行打电话是因为此时锁的对象有两个，打电话的锁和发短信的锁没关系，由于发短信有延迟，所以打电话先执行
 *
 */
public class Lock_8_demo02 {
    public static void main(String[] args) {
        Phone2 phone = new Phone2();
        Phone2 phone2 = new Phone2();

         //调用发短信的方法
        new Thread(()->{
            phone.send();
        }).start();

        //调用打电话的方法
        new Thread(()->{
            phone2.call();
        }).start();

        //调用一个无锁的方法
        new Thread(()->{
            phone.eat();
        }).start();
    }
}


class Phone2{

    public synchronized void send(){
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("发短信");
    }
    public synchronized void call(){
        System.out.println("打电话");
    }

    public  void eat(){
        System.out.println("吃大餐");
    }
}
```

### 3、两个静态的同步方法，两个对象

> 静态同步方法锁的对象只有一个，那就是类的Class ，所以无论几个对象调用，锁对象其实只有一个

```java
package com.zjj.juc_study._904study;

import java.util.concurrent.TimeUnit;

/**
   1、增加两个静态的同步方法，怎么执行？ // 先执行发短信
   2、两个对象调用，两个静态同步方法，怎么执行？ //先执行发短信
 *
 *  分析：
 *    1、static 同步方法，锁对象是类的 Class ，这是类加载以后就全局唯一的
 *    2、所以第一个执行时，还是先执行发短信 ，因为发短信和打电话是同一把锁
 *    3、两个对象调用时依然如此，因为不管多少个对象，static修改方法锁的是类对象吗，就一个
 */
public class Lock_8_demo03 {
    public static void main(String[] args) {
        Phone3 phone = new Phone3();
        Phone3 phone2 = new Phone3();

         //调用发短信的方法
        new Thread(()->{
            phone.send();
        }).start();

        //调用打电话的方法
        new Thread(()->{
            phone2.call();
        }).start();

    }
}


class Phone3{

    public static synchronized void send(){
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("发短信");
    }
    public static synchronized void call(){
        System.out.println("打电话");
    }

}
```

### 4、两个对象，一个静态同步，一个普通同步方法

> 只要静态同步方法，都理解为锁的是类的Class对象，是同一个对象，普通的同步方法，锁的就是方法调用者就可以理解

```java
package com.zjj.juc_study._904study;

import java.util.concurrent.TimeUnit;

/**
   1、一个静态同步方法，一个普通同步方法  一个对象  先打印哪个？ //打电话
   2、一个静态同步方法，一个普通同步方法  二个对象  先打印哪个？ //打电话
      分析：
      1、第一种是因为哪怕只有一个对象，依然是两把锁，一个是锁的Class 对象  一个是 Phone4对象
      2、第二种依然是打电话理由同上，索然两个对象但是 一个是锁的Class 对象  一个是 Phone4对象
 */
public class Lock_8_demo04 {
    public static void main(String[] args) {
        Phone4 phone = new Phone4();
        Phone4 phone2 = new Phone4();

         //调用发短信的方法
        new Thread(()->{
            phone.send();
        }).start();

        //调用打电话的方法
        new Thread(()->{
            phone2.call();
        }).start();

    }
}


class Phone4{

    public static synchronized void send(){
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("发短信");
    }
    public  synchronized void call(){
        System.out.println("打电话");
    }

}
```

## 集合类不安全

ArrayList--->CopyOnWriteArrayList

```java
package com.zjj.juc_study._904study.listdemo;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *  多线程操作 ArrayList ，会有并发修改异常，怎么解决呢
 */
public class CopyOnWriteArrayListStudy {
    public static void main(String[] args) {
        List<String> list = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                list.add(UUID.randomUUID().toString().substring(3));
                System.out.println(list); //ConcurrentModificationException 并发修改异常
            }).start();
        }
        /**
         *   解决方案：
         *   1、使用 new Vector<>() ,线程安全      //Synchronized 实现
         *   2、使用 Collections.synchronizedList(new ArrayList<>());
         *   3、new CopyOnWriteArrayList<>();  //lock 锁实现
         *   
         */
    }
}

```

学习方法推荐：

- 先回用

- 货比三家，用多个解决方案进行对比。寻找最优解决方案

- 分析源码

  ### Set 不安全

  ```java
  package com.zjj.juc_study._904study.listdemo;
  
  import java.util.Collections;
  import java.util.HashSet;
  import java.util.Set;
  import java.util.UUID;
  import java.util.concurrent.CopyOnWriteArraySet;
  
  public class unsafeSetStudy {
      public static void main(String[] args) {
          Set<String> hashSet = new CopyOnWriteArraySet<String>();
  
          /**
           * 解决方案：
           *     1、Collections.synchronizedSet(new HashSet<>())
           *     2、 new CopyOnWriteArraySet<String>()
           */
          for (int i = 0; i < 10; i++) {
               new Thread(()->{
                   hashSet.add(UUID.randomUUID().toString().substring(5));
                   System.out.println(hashSet);  //ConcurrentModificationException
               }).start();
          }
      }
  }
  
  ```

  **补充：HashSet原理**

  > new HashSet()  

  ![image-20220904181922850](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220904181922850.png)

  > hashSet.add() 

  ![image-20220904182027227](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220904182027227.png)



   ==总结：hashset 底层其实就是hashMap 的key==

    ### Map 不安全

```java
package com.zjj.juc_study._904study.listdemo;

import java.util.HashMap;
import java.util.UUID;

public class unSafeMap {
    public static void main(String[] args) {
        HashMap<String,String> map = new HashMap<>();

        /**   这里没有CopyOnWrite... 类似的map的包，用的是 ConcurrentHashMap
         *    1、Collections.synchronizedMap(new HashMap<>())
         *    2、 new ConcurrentHashMap<>()
         *
         */
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(5));
                System.out.println(map); //ConcurrentModificationException
            }).start();
        }
    }
}

```

**new HashMap()**

![image-20220904184523368](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220904184523368.png)

重要的两个因素。

了解hashMap 的存储原理

## Callable 学习

> Callable 怎么和多线程的创建关联呢？

- 创建多线程 使用Runnable 接口方式
- FutureTask 作为Runnable 的实现类，可以作为创建线程时的入参  
- FutureTask 本身可以用 Callable 进行构造

参考如下类图：

![image-20220912132206972](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220912132206972.png)

**具体实现可参考代码**

```java
package com.zjj.juc_study._912study;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallbaleStudy {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
         //1、传统创建多线程的方式
         new Thread(()->{   //实现Runnable 接口  --》函数式接口  --》可以用lambda 表达式
             System.out.println(123);
         }).start();

        
        
        //2、演进 使用 FutureTask (Runnable 的实现类)
        String s ="111sss";

        //创建一个 FutureTask ，将在运行时执行给定的 Runnable ，并安排 get将在成功完成后返回给定的结果。也就是执行成功以后返回传入的变量
        FutureTask<String> futureTask = new FutureTask<String>(()->{
            System.out.println("Runnable 任务被执行了");
        },s);

        new Thread(futureTask).start();

        new Thread(futureTask).start(); //结果会被缓存 说白了就是futureTask 的call方法只会执行一次

        System.out.println(futureTask.get());// 执行get方法时 线程会被阻塞



       //3、Callable 方式创建多线程
        FutureTask<String> integerFutureTask = new FutureTask<String>(() -> {
            System.out.println("Callable 方式创建多线程");
            String sss = UUID.randomUUID().toString().substring(1,5);
            return sss;   //Callable 也是函数式接口 并且抽象方法有返回值
        });
        new Thread(integerFutureTask).start(); //

        System.out.println(integerFutureTask.get());// 执行get方法时 线程会被阻塞
    }
}

```

问题：1、为什么FutureTask 只执行一次？

​           2、为什么get()方法获取返回结果的时候会被阻塞？

[答案参考此文章](https://zhuanlan.zhihu.com/p/484924626)

## JUC中三大常用辅助类

### CountDownLatch

>  允许一个或多个线程等待直到在其他线程中执行的一组操作完成的同步辅助。
>
> A `CountDownLatch`用给定的*计数*初始化。 [`await`](../../../java/util/concurrent/CountDownLatch.html#await--)方法阻塞，直到由于[`countDown()`](../../../java/util/concurrent/CountDownLatch.html#countDown--)方法的[调用](../../../java/util/concurrent/CountDownLatch.html#countDown--)而导致当前计数达到零，之后所有等待线程被释放，并且任何后续的`await`  [调用立即](../../../java/util/concurrent/CountDownLatch.html#await--)返回。  这是一个一次性的现象 - 计数无法重置。 如果您需要重置计数的版本，请考虑使用[`CyclicBarrier`](../../../java/util/concurrent/CyclicBarrier.html)  。 

**示例Demo**

```java
package com.zjj.juc_study._912study;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchStudy {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            new Thread(()->{
                System.out.println("线程"+temp+"正在执行");
                countDownLatch.countDown(); // -1
            },String.valueOf(i)).start();
        }
        countDownLatch.await();  //等待计数器归零 然后向下执行  
        System.out.println("10个线程执行完了，我解放了");
    }
}

```



![image-20220912134902303](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220912134902303.png)

==countDownLatch.countDown()==: 执行 -1 操作

==countDownLatch.await()== ：等待计数器归0 然后向下执行

### CyclicBarrier

> - 允许一组线程全部等待彼此达到共同屏障点的同步辅助。  循环阻塞在涉及固定大小的线程方的程序中很有用，这些线程必须偶尔等待彼此。 屏障被称为*循环*  ，因为它可以在等待的线程被释放之后重新使用。

简单理解为`加法计数器`：到达某个数量后触发什么操作

```java
package com.zjj.juc_study._912study;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierStudy {
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()->{
            System.out.println("集齐7颗龙珠，召唤神龙");
        });
        for (int i = 0; i < 7; i++) {
            final int temp = i;
            new Thread(()->{
                System.out.println("线程"+temp+"正在执行");
                try {
                        cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            },String.valueOf(i)).start();
        }
    }
}
// 线程执行7次以后，执行Runnable接口的run()

```

### Semaphore

> - 一个计数信号量。在概念上，信号量维持一组许可证。如果有必要，每个[`acquire()`都会](../../../java/util/concurrent/Semaphore.html#acquire--)阻塞，直到许可证可用，然后才能使用它。每个[`release()`](../../../java/util/concurrent/Semaphore.html#release--)添加许可证，潜在地释放阻塞获取方。但是，没有使用实际的许可证对象;`Semaphore`只保留可用数量的计数，并相应地执行。
> - 信号量通常用于限制线程数，而不是访问某些（物理或逻辑）资源。 

```java
package com.zjj.juc_study._912study;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreStudy {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);
        //停车位方式  理解  其实主要用做限流使用
        for (int i = 0; i < 7; i++) {
            final int temp = i;
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("车主" + Thread.currentThread().getName() + "抢到了车位");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println("车主" + Thread.currentThread().getName() + "离开了车位");
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();
                }
            }, String.valueOf(i)).start();
        }
    }
}

```

==semaphore.acquire()==：获得，如果已经满了，等待 等待被释放为止

==semaphore.release()==：释放 ，会将当前的信号量释放+1，然后唤醒等待的线程

作用：多个共享资源互斥的使用，并发限流，控制最大的线程数

## 读写锁ReentrantReadWriteLock

> 用 Synchronized 或者Lock 都可以实现锁，解决并发问题，但是相比较这两个锁，ReentrantReadWriteLock 锁的粒度更细一些

[读写锁原理可参考此文章](https://blog.csdn.net/qq_37685457/article/details/89855519)

```java
package com.zjj.juc_study._912study;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 独占锁（写锁）：一次只能被一个线程占有
 * 共享锁（读锁）：多个线程可以同时占有
 *
 * 读-读 ：可以共存
 * 读-写 ：不能共存
 * 写-写 ：不能共存
 */
public class ReentrantReadWriteLockStudy {
    public static void main(String[] args) {
        MyCatch myCatch = new MyCatch();
        //5个线程存入数据 没有锁肯定有并发问题
        for (int i = 0; i < 5; i++) {
            final int temp = i;
            new Thread(()->{
                myCatch.putData(String.valueOf(temp),UUID.randomUUID().toString().substring(1,5));
            },String.valueOf(i)).start();
        }

        //5个线程取数据
        for (int i = 0; i < 5; i++) {
            final int temp = i;
            new Thread(()->{
                myCatch.getData(String.valueOf(temp));
            },String.valueOf(i)).start();
        }
    }
}

class MyCatch{
    private Map<String,Object> map = new HashMap<>();
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public /*synchronized*/ void  putData(String k,Object v){
        readWriteLock.writeLock().lock();
        System.out.println(Thread.currentThread().getName()+"正在存入数据");
        try {
            map.put(k,v);
            TimeUnit.SECONDS.sleep(1);
            System.out.println(Thread.currentThread().getName()+"存完了");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            readWriteLock.writeLock().unlock();
        }

    }
    public  void  getData(String k){
        readWriteLock.readLock().lock();
        try {

            System.out.println(Thread.currentThread().getName()+"正在拿数据");
            Object o = map.get(k);
            TimeUnit.SECONDS.sleep(1);
            System.out.println(Thread.currentThread().getName()+"拿完了");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            readWriteLock.readLock().unlock();
        }

    }
}
```

##  13、队列 BlockingQueue



**队列类图：与List 、Set 平级**

![请添加图片描述](https://img-blog.csdnimg.cn/35db4744711041eab5621bf2d91f076c.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6YOd5byA,size_20,color_FFFFFF,t_70,g_se,x_16)



**实现类**

![image-20220912165157866](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220912165157866.png)

`Dwque`：双端队列
`AbstractQueue`：非阻塞队列
`SynchronousQueue`：同步队列

> 使用场景

**多线程并发处理，线程池，需要用一个队列去维护它的大小。**

> BlockingQueue四组必会API

|   操作方式   |  抛出异常   | 有返回值，不抛出异常 |  阻塞等待  |         超时等待          |
| :----------: | :---------: | :------------------: | :--------: | :-----------------------: |
|     添加     | `add(E e)`  |     `offer(E e)`     | `put(E e)` | `offer(e, timeout, unit)` |
|     移除     | `remove()`  |       `poll()`       |  `take()`  |   `poll(timeout, unit)`   |
| 检测队首元素 | `element()` |       `peek()`       |     -      |             -             |

**示例Demo**

```java
package com.zjj.juc_study._912study;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class BlockQueueStudy {
    private BlockingQueue<String> blockingQueue;

    public static void main(String[] args) {
        test4();
    }

    /**
     * 抛出异常
     */
    public static void test1() {
        BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<String>(2);
        //添加
        blockingQueue.add("1");
        blockingQueue.add("2");
        // blockingQueue.add("3");// 满了以后在添加抛出异常 IllegalStateException: Deque full

        //移除
        blockingQueue.remove();
        blockingQueue.remove();
        // blockingQueue.remove();// NoSuchElementException

        //检测队首元素

        System.out.println(blockingQueue.element()); //队首没有元素时 报错 NoSuchElementException
    }

    /**
     * 有返回值，不抛出异常
     */
    public static void test2() {
        BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<String>(2);
        //添加
        blockingQueue.offer("1");
        System.out.println(blockingQueue.offer("2"));// 添加成功 返回true
        System.out.println(blockingQueue.offer("3"));//  添加失败的时候 返回false
        System.out.println("===============移除======================================");
        //移除
        blockingQueue.poll();
        System.out.println(blockingQueue.poll());// 返回移除的元素
       // System.out.println(blockingQueue.poll()); //沒有元素 返回null

        //检测队首元素
        System.out.println("===============检测队首元素=====================================");
         System.out.println(blockingQueue.peek()); //队首没有元素时 返回null
    }

    /**
     * 阻塞等待
     */
    public static void test3() {
        BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<String>(2);
        //添加
        try {
            blockingQueue.put("1");
            blockingQueue.put("2");//
            //blockingQueue.put("3");// 放不进去的时候会阻塞在这里
            System.out.println("===============移除======================================");
            //移除
           blockingQueue.take();
           System.out.println(blockingQueue.take());// 返回移除的元素
           System.out.println(blockingQueue.take()); //沒有元素可以移除  就会一直阻塞

            //没有获取队首元素 可以阻塞等待的方法

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 超时等待
     */
    public static void test4() {
        BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<String>(2);
        //添加
        try {
            blockingQueue.offer("1");
            System.out.println(blockingQueue.offer("2")); //true
            System.out.println(blockingQueue.offer("3", 2l, TimeUnit.SECONDS));// 超时没放进去 返回false
            System.out.println("===============移除======================================");
            //移除
             blockingQueue.poll(2,TimeUnit.SECONDS);
            System.out.println(blockingQueue.poll(2,TimeUnit.SECONDS));// 返回移除的元素
            System.out.println(blockingQueue.poll(2,TimeUnit.SECONDS)); //等待两秒还没放进来 返回null

            //没有获取队首元素 可以超时等待的方法

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

```

****

> **SynchronousQueue  同步队列** 

- 没有容量
- 进去一个元素 必须等取出来一个才能放入下一个
- put 、tack 操作存取 

```java
package com.zjj.juc_study._912study;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 *  同步队列
 *  和其他 blockQueue 不一样 SynchronousQueue 不存储元素
 *  put了一个元素 必须从里面take 取出来 否则不能在put进去
 */
public class SynchronousQueueDemo {
    public static void main(String[] args) {
        BlockingQueue<String> synchronousQueue = new SynchronousQueue();
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName()+"线程，正在存数据。。。。000");
                synchronousQueue.put("000");
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName()+"线程，正在存数据。。。。111");
                synchronousQueue.put("111");
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName()+"线程，正在存数据。。。。222");
                synchronousQueue.put("222");
            } catch (Exception e) {
                e.printStackTrace();
            }
        },"AAA").start();

        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName()+"线程，正在取数据。。。"+synchronousQueue.take());
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName()+"线程，正在取数据。。。"+synchronousQueue.take());
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName()+"线程，正在取数据。。。"+synchronousQueue.take());
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"BBB").start();
    }
}

```



# 线程池（重要）

> 池化技术：

程序的运行，本质：占用系统的资源！ 为了优化资源的使用  ==》 池化技术

线程池、连接池、内存池、对象池... //  频繁的创建、销毁   十分浪费资源

池化技术：事先准备好一些资源，有人要用 就来我这里拿，用完以后还给我。

> 线程池的好处

1、降低资源的消耗

2、提高响应的速度

3、方便管理

==线程复用，可以控制最大并发数、管理线程==

**3大方法、7大参数、4种拒绝策略**

## 线程池：3大方法（常用的）

  ps ： 其实有不止这几种

```java
package com.zjj.juc_study._0917study;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 利用 Executors 创建三类线程池
 *    1、Executors.newSingleThreadExecutor()
 *    2、Executors.newFixedThreadPool(5)
 *    3、Executors.newCachedThreadPool()
 */
public class _0917study_Demo01 {
    public static void main(String[] args) {
     //  testExecutors1();
     //  testExecutors2();
       testExecutors3();
    }

    static void testExecutors1() {
        // 创建单一线程的线程池
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        try {
            for (int i = 0; i < 10; i++) {
                singleThreadExecutor.submit(() -> {
                    System.out.println(Thread.currentThread().getName() + "正在执行"); //输出的只有这一个线程
                }, String.valueOf(i));
            }
        } catch (Exception e) {
           //异常处理
        } finally {
            singleThreadExecutor.shutdown();
        }
    }

    static void testExecutors2() {
        //创建固定长度的线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        try {
            for (int i = 0; i < 10; i++) {
                fixedThreadPool.submit(() -> {
                    System.out.println(Thread.currentThread().getName() + "正在执行"); //输出的是线程池内的5个线程
                }, String.valueOf(i));
            }
        } catch (Exception e) {
            //异常处理
        } finally {
            fixedThreadPool.shutdown();
        }
    }

    static void testExecutors3() {
        //创建固定长度的线程池
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        try {
            for (int i = 0; i < 1000; i++) {
                cachedThreadPool.submit(() -> {
                    System.out.println(Thread.currentThread().getName() + "正在执行"); //输出的不是固定的  最大可以接受21 亿 ，（内存一般没有这么大，会报OOM）
                }, String.valueOf(i));
            }
        } catch (Exception e) {
            //异常处理
        } finally {
            cachedThreadPool.shutdown();
        }
    }

}

```

**问题：为什么阿里规范上禁止有以上三种方式创建线程池呢？而是推荐 new ThreadPoolExecutor 方式**

查看源码可以看到原因

​     Executors.newSingleThreadExecutor()：

​         底层调用 new ThreadPoolExecutor 方式创建，最大线程和核心线程均为1  默认拒绝策略为 new AbortPolicy();

> 1. 这里我们注意到虽然maximumPoolSize的取值是1了
>
> 2. 但是阻塞队列使用了LinkedBlockingQueue，我们接着看LinkedBlockingQueue的源代码
>
> 3. ```java
>    // 从这里不难看出阻塞队列的长度过长，也容易造成oom
>    public LinkedBlockingQueue() {
>        this(Integer.MAX_VALUE);
>    } 
>    ```

![image-20220917144703704](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220917144703704.png)

 Executors.newFixedThreadPool(5)：

​         底层调用 new ThreadPoolExecutor 方式创建，最大线程和核心线程均为入参数。  默认拒绝策略为 new AbortPolicy();

> 这里和`newSingleThreadExecutor`的问题是一样的，阻塞队列长度过长，造成`oom`

![image-20220917150310417](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220917150310417.png)

 Executors.newCachedThreadPool()：

​         底层调用 new ThreadPoolExecutor 方式创建，最大线程为21亿。核心线程为0。  默认拒绝策略为 new AbortPolicy(); SynchronousQueue 同步 队列

> 1. *我们可以看到最大线程数maximumPoolSize的取值是 Integer.MAX_VALUE*
> 2. 这样容易创建大量线程造成OOM

![image-20220917150423546](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220917150423546.png)



综上：从上面的分析我们不难看出，使用`Executors`创建的线程池，可能会存在**阻塞队列的长度过长**或者**最大线程数过大**的问题，有造成**OOM**的隐患！

## 线程池：7大参数

![image-20220917151234435](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220917151234435.png)

参考下图理解：

![image-20220917152326246](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220917152326246.png)

1、**corePoolSize** 核心线程数

2、**BlockingQueue workQueue** 阻塞队列

   当前的线程数等于corePoolSize同时workQueue未满并且新任务没有空闲线程来执行的时候，则将任务入队列，而不添加新的线  程。对于队列，通常由两种：

​	  a、**SynchronousQueue**

​               此队列中不缓存任何一个任务。

​	  b、**LinkedBlockingQueue**

​              用链表实现的队列，可以是有界的，也可以是无界的，但在Executors中默认使用无界的。

​	 c、**ArrayBlockingQueue**

​            FIFO原则的队列

3、**maximumPoolSize** 最大线程数

​            当前的线程数等于corePoolSize同时workQueue满了并且线程池中的线程数小于最大线程数，则额外创建线程来执行任务

4、**keepAliveTime** 空闲线程的存活时间

​           当线程数大于核心线程数的时候，额外线程等待任务的最大时间，如果超过这个时间，额外线程就被销毁

5、**TimeUnit unit** 表示keepAliveTime的单位

6、**ThreadFactory threadFactory** 线程创建的工厂

​            为了方便的管理我们的线程，我们可以自定义线程创建工厂，来对我们的线程进行个性化的设置，比如说线程名、优先级等等

​           一般情况下使用默认的，即Executors类的静态方法defaultThreadFactory()；

7、**RejectedExecutionHandler handler** 拒绝策略

​     当我们核心线程数、阻塞队列、最大线程数都到达上限，这个时候新的任务就会走拒绝策略，那么拒绝策略有哪些呢？通常由以下四种：

- 【ThreadPoolExecutor.AbortPolicy() ====丢弃任务，并抛出RejectedExecutionException异常】

- 【ThreadPoolExecutor.CallerRunsPolicy() ====将被拒绝的任务添加到`“线程池正在运行的线程”`中去执行】

- 【ThreadPoolExecutor.DiscardOldestPolicy() ====抛弃最旧的任务《最先提交而没有得到执行的任务》】

- 【ThreadPoolExecutor.DiscardPolicy() ====抛弃当前的任务,没有异常】

> 大概流程;

1、首先多线程的任务会根据其队列类型workQueue，看是否是有容量的，还是直接提交的，

​           提交就会让直接创建线程执行，

​          有容量的，就创建吸纳从到corepoolSize数量时，任务就会暂时保存在队列中；
2、当corepoolSize数量已满，而且当队列中任务已满，

​            如果用于执行任务的线程数量corepoolSize中已有的数量小于 maximumPoolSize，则尝试创建新的进程

3、如果达到maximumPoolSize设置的最大值，则根据你设置的handler执行拒绝策略。

> 队列种类

**1、直接提交队列**：

​       设置为**SynchronousQueue**队列，SynchronousQueue是一个特殊的BlockingQueue，它没有容量

​      使用SynchronousQueue队列，提交的任务不会被保存，总是会马上提交执行。如果用于执行任务的线程数量小于maximumPoolSize，则尝试创建新的进程，如果达到maximumPoolSize设置的最大值，则根据你设置的handler执行拒绝策略。因此这种方式你提交的任务不会被缓存起来，而是会被马上执行

**2、有界的任务队列**

​       有界的任务队列可以使用**ArrayBlockingQueue**实现 （创建队列时可以指定大小，你也可以做成无界）

 	 使用ArrayBlockingQueue有界任务队列，若有新的任务需要执行时，线程池会创建新的线程，直到创建的线程数量达到corePoolSize时，则会将新的任务加入到等待队列中。

​      若等待队列已满，即超过ArrayBlockingQueue初始化的容量，则继续创建线程，直到线程数量达到maximumPoolSize设置的最大线程数量，若大于maximumPoolSize，则执行拒绝策略。

**3、无界的任务队列**     

​         无界的任务队列可以使用**LinkedBlockingQueue**实现（创建队列时可以指定大小，你也可以做成有界）

        ```java
        // LinkedBlockingQueue 的默认实现 
        public LinkedBlockingQueue() {
                this(Integer.MAX_VALUE);  //就因为这 容量不满
            }
        ```

​        用无界任 务队列，线程池的任务队列可以无限制的添加新的任务，而线程池创建的最大线程数量就是你corePoolSize设置的数量，也就是说在这种情况下 maximumPoolSize 这个参数是无效的，哪怕你的任务队列中缓存了很多未执行的任务，当线程池的线程数达到corePoolSize后，就不会再增加了；

​      若后续有新的任务加入，则直接进入队列等待，当使用这种任务队列模式时，一定要注意你任务提交与处理之间的协调与控制，不然会出现队列中的任务由于无法及时处理导致一直增长，直到最后资源耗尽的问题。

4、 **优先任务队列**：优先任务队列通过**PriorityBlockingQueue**实现

> 总结下来其实两类 一个是   SynchronousQueue  还有一个是有界的

## 线程池： 4种拒绝策略

- 【ThreadPoolExecutor.AbortPolicy() ====丢弃任务，并抛出RejectedExecutionException异常】

- 【ThreadPoolExecutor.CallerRunsPolicy() ====将被拒绝的任务添加到`“线程池正在运行的线程”`中去执行】

- 【ThreadPoolExecutor.DiscardOldestPolicy() ====抛弃最旧的任务《最先提交而没有得到执行的任务》】

- 【ThreadPoolExecutor.DiscardPolicy() ====抛弃当前的任务,没有异常】

> 总结一点：
>
> 线程池的最大承载数 = deque + max 
>
>   超过最大承载，执行拒绝策略

## 自定义线程池  验证以上内容

```java
package com.zjj.juc_study._0917study;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class diyExecutorsStudy {
    public static void main(String[] args) {
        ThreadPoolExecutor diyThreadPool = new ThreadPoolExecutor(2,
                5,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(30), // 此队列太大的话一直使用的就是核心线程
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

              try {
                  for (int i = 0; i < 10; i++) {
                      diyThreadPool.submit(()->{
                          System.out.println(Thread.currentThread().getName()+"===>正在执行");
                      },String.valueOf(i));
                  }
              }finally {
                  diyThreadPool.shutdown();
              }

    }
}

```

**==线程池一定要关闭哦==**

**自定义线程池的时候，最大线程数怎么设置？**

> IO 密集型 ：
>
> ​    电脑几核的就设置几，可以保持CPU的效率最高

```java
public class getMaxThread {
    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors()); //获取核数
    }
}
```

> CUP密集型
>
> ​     根据实际业务来确定：
>
> ​      比如说有15个任务需要跑，耗时比较久，那就衡量一下定义15个以上  根据实际业务情况

# 函数式接口

`新时代的程序员：lambda 表达式 、链式编程、函数式接口、Stream 流式计算`

> 函数式接口：就是一个有且仅有一个抽象方法，但是可以有多个非抽象方法的接口

以Runnable 接口为例

![image-20220917200035271](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220917200035271.png)

  **`@FunctionalInterface `注解可以检测接口是否为函数式接口，如果不是函数式接口，则编译失败**



> Function 函数型接口 ：
>
> ​        一个T 类的入参，一个R类型的出参

![image-20220917213237636](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220917213237636.png)

```java
package com.zjj.juc_study._0917study.function_interface;
import java.util.function.Function;
/**
 * Function 函数型接口 有一个输入参数 有一个输出参数
 * 只要是函数型接口，都可以用 Lambda  表达式
 */
public class functionInterface {

    public static void main(String[] args) {
        
        //1、匿名内部类方式 实现
        Function<String,Integer> function0 =  new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return Integer.valueOf(s)-1;
            }
        };
        //2、Lambda 表达式实现
        Function<String,Integer> function = k->{
            return Integer.valueOf(k)-1;
        };

       /* Lambda 是匿名内部类的简化写法，对于此Function 接口，在定义Function 类的泛型的时候，方法的出
        *     入参就已经定了
        */
        //输出 Function 函数的返回值
        System.out.println(function.apply("123"));

    }
}

```





> **Predicate** 断言型接口 :
>
> ​           一个入参 ，返回值为布尔值

![image-20220917215210475](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220917215210475.png)

```java
package com.zjj.juc_study._0917study.function_interface;

import java.util.function.Predicate;

public class Demo02 {
    public static void main(String[] args) {
        // 1、匿名内部类
        Predicate<String> p1 = new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return false;
            }
        };
        System.out.println(p1.test("123"));

        //2、简化为Lambda 表达式
        Predicate<String> p2 = s -> {
            return true;
        };
        System.out.println(p2.test("123"));
    }
}

```



> **Consumer** 消费型接口：
>
> ​      一个入参，没有返回值

![image-20220917220154107](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220917220154107.png)



```java
package com.zjj.juc_study._0917study.function_interface;

import java.util.function.Consumer;

public class Demo03 {
    public static void main(String[] args) {
        Consumer<String> c1 = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("匿名内部类的我执行了");
            }
        };
        c1.accept("2");

        Consumer<String> c2 = (s) -> {
            System.out.println("简化升级的我Lambda 表达式执行了");
        };

        c2.accept("1");

    }
}

```



> **Supplier** 供给型接口：
>
> ​               没有入参，一个返回值 

![image-20220917220908454](C:\Users\17310\AppData\Roaming\Typora\typora-user-images\image-20220917220908454.png)



```java
package com.zjj.juc_study._0917study.function_interface;

import java.util.function.Supplier;

public class Demo04 {
    public static void main(String[] args) {
        //1、 内部类方式
        Supplier<String> p1 = new Supplier<String>() {
            @Override
            public String get() {
                return "123";
            }
        };
        System.out.println(p1.get());

        //2、Lambda 表达式
        Supplier<Integer> p2 = () -> {
            return 123;
        };

        System.out.println(p2.get());

    }
}
```

**以上函数型接口更多的是使用在拓展性方法里，通过对接口的不同实现来实现不同的业务**

例如：Consumer 的使用

```java
public class consumer {
    public static void main(String[] args) {
        System.out.println("1、-----------------------------------");
        //想要打印的参数
        printNum("aaa",s -> System.out.println(s));

        System.out.println("2、-----------------------------------");
        //想要打印该参数字符的个数
        printNum("aaa",s -> System.out.println(s.length()));

        System.out.println("3、-----------------------------------");
        //将参数和其他字符串拼接
        printNum("aaa",s -> System.out.println("999"+s));

        System.out.println("4、-----------------------------------");
        Consumer<String> s1 = s -> System.out.println(s);
        Consumer<String> s2 = s -> System.out.println(s.length());
        //将s1 和 s2 结合到一起
        s1.andThen(s2).accept("aaa");
    }
    //方法的功能：打印参数
    // 打印好处：
    //          如果方法要使用一个参数，具体参数的使用方式不确定，或者有很多种使用方式
    //          可以将该参数交给消费型接口去完成该参数的使用
    //          后续使用该方法时，不仅要传递实际参数，还要传递消费型接口的实现类对象
    //          消费型接口的对象如何定义： 根据使用的具体需求来定义
    public static void printNum(String str, Consumer<String > con){
        con.accept(str);
    }
}
```



# Stream 流

> 定义：Stream流的介绍

1. `stream`不存储数据，而是按照特定的规则对数据进行计算，一般会输出结果；
2. `stream`不会改变数据源，通常情况下会产生一个新的集合；
3. `stream`具有延迟执行特性，只有调用终端操作时，中间操作才会执行。
4. 对`stream`操作分为终端操作和中间操作，那么这两者分别代表什么呢？

   - 终端操作：会消费流，这种操作会产生一个结果的，如果一个流被消费过了，那它就不能被重用的。

   - 中间操作：中间操作会产生另一个流。因此中间操作可以用来创建执行一系列动作的管道。

     ​                    **一个特别需要注意的点是:中间操作不是立即发生的。相反，当在中间操作创建的新流上执行完终 端操作后，中间操作指定的操作才会发生。所以中间操作是延迟发生的，中间操作的延迟行为主要是让流API能够更加高效地执行。**

5. `stream`不可复用，对一个已经进行过终端操作的流再次调用，会抛出异常。

> demo

```java
package com.zjj.juc_study._0917study.function_interface;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamDemo {
    public static void main(String[] args) {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 65, 7, 9);
        integers.stream().filter(s->{     //过滤掉是偶数的
           return s%2==0;
        }).collect(Collectors.toMap(s->{   //单列转化为双列集合
           return String.valueOf(s+1);
        },t->{
           return  "aaa" ;
        })).forEach((k,v)->{
            System.out.println(k);
            System.out.println(v);
            System.out.println("=================");
        });
    }
}

```



# ForkJoin

> ForkJoin

[参考此链接](https://blog.csdn.net/weixin_42039228/article/details/123206215)

**ForkJoinPool与ThreadPool思考**

- 共同点
      它们都是线程池中有多个线程，能够将任务进行执行
-  区别
      ForkJoinPool是将一个任务分配为多个子任务来进行并发执行。ThreadPool是将不同任务放入队列进行分配执行



# CompletableFuture

[参考此链接](https://blog.csdn.net/wumingdu1234/article/details/123947273)

# JMM

> 什么是JMM

   JMM就是Java内存模型(java memory model)。因为在不同的硬件生产商和不同的操作系统下，内存的访问有一定的差异，所以会造成相同的代码运行在不同的系统上会出现各种问题。所以**java内存模型(JMM)屏蔽掉各种硬件和操作系统的内存访问差异，以实现让java程序在各种平台下都能达到一致的并发效果。**

​      Java内存模型规定**所有的变量都存储在主内存**中，包括实例变量，静态变量，但是不包括局部变量和方法参数。每个线程都有自己的工作内存，**线程的工作内存保存了该线程用到的变量和主内存的副本拷贝，线程对变量的操作都在工作内存中进行**。**线程不能直接读写主内存中的变量**。

![img](http://p6.itc.cn/images01/20200923/ef3cc31b27eb4c6e9ad6e63c7d55c132.png)



> JMM定义了什么

- **`原子性`**

  ​     原子性指的是一个操作是不可分割，不可中断的，一个线程在执行时不会被其他线程干扰

- **`可见性`**

             1. 可见性指当一个线程修改共享变量的值，其他线程能够立即知道被修改了
             1. Java是利用volatile关键字来提供可见性的。 当变量被volatile修饰时，这个变量被修改后会立刻刷新到主内存，当其它线程需要读取该变量时，会去主内存中读取新值。而普通变量则不能保证这一点。
             1. 除了volatile关键字之外，final和synchronized也能实现可见性。

- **`有序性`**

     在Java中，可以使用synchronized或者volatile保证多线程之间操作的有序性。实现原理有些区别：

       1. volatile关键字是使用内存屏障达到禁止指令重排序，以保证有序性。
       1. synchronized的原理是，一个线程lock之后，必须unlock后，其他线程才可以重新lock，使得被synchronized包住的代码块在多线程之间是串行执行的。

> **八种内存交互**

![img](http://p1.itc.cn/images01/20200923/46abbcc96025479bb217452288178e66.png)

- lock(锁定)，作用于**主内存**中的变量，把变量标识为线程独占的状态。
- read(读取)，作用于**主内存**的变量，把变量的值从主内存传输到线程的工作内存中，以便下一步的load操作使用。
- load(加载)，作用于**工作内存**的变量，把read操作主存的变量放入到工作内存的变量副本中。
- use(使用)，作用于**工作内存**的变量，把工作内存中的变量传输到执行引擎，每当虚拟机遇到一个需要使用到变量的值的字节码指令时将会执行这个操作。
- assign(赋值)，作用于**工作内存**的变量，它把一个从执行引擎中接受到的值赋值给工作内存的变量副本中，每当虚拟机遇到一个给变量赋值的字节码指令时将会执行这个操作。
- store(存储)，作用于**工作内存**的变量，它把一个从工作内存中一个变量的值传送到主内存中，以便后续的write使用。
- write(写入)：作用于**主内存**中的变量，它把store操作从工作内存中得到的变量的值放入主内存的变量中。
- unlock(解锁)：作用于**主内存**的变量，它把一个处于锁定状态的变量释放出来，释放后的变量才可以被其他线程锁定。

> JMM对8种内存交互操作制定的规则

- 不允许read、load、store、write操作之一单独出现，也就是read操作后必须load，store操作后必须write。
- 不允许线程丢弃他最近的assign操作，即工作内存中的变量数据改变了之后，必须告知主存。
- 不允许线程将没有assign的数据从工作内存同步到主内存。
- 一个新的变量必须在主内存中诞生，不允许工作内存直接使用一个未被初始化的变量。就是对变量实施use、store操作之前，必须经过load和assign操作。
- 一个变量同一时间只能有一个线程对其进行lock操作。多次lock之后，必须执行相同次数unlock才可以解锁。
- 如果对一个变量进行lock操作，会清空所有工作内存中此变量的值。在执行引擎使用这个变量前，必须重新load或assign操作初始化变量的值。
- 如果一个变量没有被lock，就不能对其进行unlock操作。也不能unlock一个被其他线程锁住的变量。
- 一个线程对一个变量进行unlock操作之前，必须先把此变量同步回主内存。

> **Volatile**

很多并发编程都使用了volatile关键字，主要的作用包括两点：

1. **保证线程间变量的可见性。**
2. **禁止CPU进行指令重排序**

==**注意：volatile 不能保证原子性**==

> Volatile之可见性

volatile保证可见性的流程大概就是这个一个过程：

![img](http://p5.itc.cn/images01/20200923/def09491f53f4d56af3858a8d4de71cd.png)

**验证可见性Demo如下：**

```java
package com.zjj.juc_study._0917study.JMMabout;

import java.util.concurrent.TimeUnit;
/**
*    i 如果不用 Volatile 修饰 ，就会一直循环下去，因为while 的值一直不变
*/
public class VolatileTest {
    private volatile static int i ;  
    public static void main(String[] args) throws InterruptedException {
       //线程1
        new Thread(()->{
            while (i==0){
                //System.out.println("我一直在循环");  这里 println 方法会带锁  无法重先
            }
        }).start();

        TimeUnit.SECONDS.sleep(2);
        //主线程 修改 i的值
         i =1;

        System.out.println("执行完了");
    }
}

```

**验证非原子性：**

```java
package com.zjj.juc_study._0917study.JMMabout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class JmmTest {
   // private static volatile int i ;   加了输出结果也不是两万
    private volatile static  AtomicInteger  i = new AtomicInteger();
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(20);
        for (int i1 = 1; i1 <= 20; i1++) {
             new Thread(()->{
                 for (int i2 = 0; i2 < 1000; i2++) {
                     add();
                 }
                 countDownLatch.countDown();
             }).start();
        }

         countDownLatch.await(); //等待20个线程执行完
        /*while (Thread.activeCount()>2){

        }*/
        System.out.println(i.get());
    }

   public static void add(){
        // i++  不是一个原子类操作
       i.getAndIncrement(); // 底层用的CAS 效率比 lock 和 synchronized 高
   }
}

```

**CAS和原子类
     CAS即Compare and Swap，是一种乐观锁的思想。为了保证该变量的可见性，需要使用volatile修饰，`结合CAS和volatile可以实现无锁并发`，适用于竞争不激烈、多核CPU的场景。
原子操作类：AtomicInteger、AtomicBoolean，底层采用CAS+volatile实现。**



> Volatile之 禁止指令重排序

​     为了使指令更加符合CPU的执行特性，最大限度的发挥机器的性能，提高程序的执行效率，只要程序的最终结果与它顺序化情况的结果相等，那么指令的执行顺序可以与代码逻辑顺序不一致，这个过程就叫做**指令的重排序**。

**源代码－－》编译器优化重排－－》指令并行也可能会重排－－》内存系统也会重排－－》执行**

![img](https://pic3.zhimg.com/80/v2-b98bd0ede8658fd5f3c53d8d9cb51ad6_1440w.jpg)





如下相关总结

![img](http://p0.itc.cn/images01/20200923/a8cb38c5ba6c4bbbb60aad3d16e7fcbc.png)

# 单例模式

> 饿汉式(线程安全，调用效率高，但是不能延时加载)：

```java
/**
 *  饿汉式
 */
public class hungerTest {
    private static hungerTest ht = new hungerTest();

    public static hungerTest getInstance(){
        return ht;
    }
}
```

> 懒汉式(线程安全，调用效率不高，但是能延时加载)：

​      Double CheckLock实现单例：

​      DCL也就是双重锁判断机制（由于JVM底层模型原因，偶尔会出问题，不建议使用）

​       [具体参考为什么会出现问题](https://blog.csdn.net/m0_46378271/article/details/125899790)

```java
package com.zjj.juc_study._0917study.singleModel;

/**
 * 懒汉式
 */
public class LazyTest {
    private  volatile static  LazyTest t;
    public static LazyTest getInstance() {
        if (t == null) {
            synchronized (LazyTest.class) {
                if (t == null) {
                    t = new LazyTest();
                }
            }
        }
        return t;
    }

}

```

> 静态内部类   线程安全，调用效率高，可以延时加载

```java
package com.zjj.juc_study._0917study.singleModel;

/**
 *  静态内部类
 */
public class InnerTest {

    public InnerTest getInstance(){
        return innerCls.t;
    }

    static class innerCls {
        private static final InnerTest t = new InnerTest();
    }
}

```

> 枚举类（线程安全，调用效率高，不能延时加载，可以天然的防止反射和反序列化调用）

```java
package com.zjj.juc_study._0917study.singleModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum EnumTest {
    instance ;

    public LazyTest GetInstance(){
        return new LazyTest();
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        /*Constructor<EnumTest> constructor = EnumTest.class.getConstructor();
        EnumTest enumTest = constructor.newInstance(); *///这里会报异常  NoSuchMethodException 因为字节码生成的不是无参构造方法


        Constructor<EnumTest> constructor = EnumTest.class.getDeclaredConstructor(String.class,int.class);//字节码中可以看到这个类有两个入参的构造方法 <(Ljava/lang/String;I)V>
        EnumTest enumTest = constructor.newInstance("abc", 1); //Cannot reflectively create enum objects
        System.out.println(enumTest);

    }
}

```




















































































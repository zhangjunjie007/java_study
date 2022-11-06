## ArrayList 类内部属性
```java
  private static final int DEFAULT_CAPACITY = 10;       //默认容量              	     

    /**
     * Shared empty array instance used for empty instances.
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};   // 默认空对象    

    /**
     * Shared empty array instance used for default sized empty instances. We
     * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
     * first element is added.
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};  //默认空对象

    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     */
    transient Object[] elementData;   //   底层数组

    /**
     * The size of the ArrayList (the number of elements it contains).
     *
     * @serial
     */
    private int size;                // 数组内元素的个数  不是数组的长度


    /**
     * The maximum size of array to allocate.
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8; //数组可以存储的最大个数
```
> **为什么ArrayList容量上限是`Integer.MAX_VALUE `- 8**



![image.png](https://cdn.nlark.com/yuque/0/2022/png/32794191/1663762491417-ba996056-07f1-4506-8536-84fec8e41eb9.png?x-oss-process=image%2Fresize%2Cw_1125%2Climit_0)

## 构造方法

```java
//1、 指定容量构造  
public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    };

//2、空参构造 默认空数组
  public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

//3、传入集合 
    public ArrayList(Collection<? extends E> c) {
        Object[] a = c.toArray();
        if ((size = a.length) != 0) {
            if (c.getClass() == ArrayList.class) {
                elementData = a;
            } else {
                elementData = Arrays.copyOf(a, size, Object[].class);
            }
        } else {
            // replace with empty array.
            elementData = EMPTY_ELEMENTDATA;
        }
    }
    
```

## add(E e)

```java

    /**
     *   1. 先判断是否需要扩容
         2。 把新元素顺序放在数组中
     */
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }
```

**具体扩容步骤分析**

```java
//根据所需容量 确定是否需要扩容
private void ensureCapacityInternal(int minCapacity) {
    ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
}

  //判断此时底层的 elementData 是否还为空，是的话返回所需容量大小
    private static int calculateCapacity(Object[] elementData, int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            return Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        return minCapacity;
    }

   // 如果此时所需的容量大于数组现有容量  触发扩容 grow()
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;  //迭代器中修改数据报异常就是根据这个来的

        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }


```

**扩容方法**

```java
   
//jdk1.8的 扩容
private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;               //数组现有容量 
        int newCapacity = oldCapacity + (oldCapacity >> 1);//现有容量+现有容量的1/2（也就是1.5倍）
        if (newCapacity - minCapacity < 0) //1.5倍扩容以后 仍然小于所需最小容量
            newCapacity = minCapacity;     //把此时所需的容量作为数组长度
        if (newCapacity - MAX_ARRAY_SIZE > 0)  //新容量大于Integer.MAX_VALUE - 8
            newCapacity = hugeCapacity(minCapacity); //取Integer.MAX_VALUE作为最大值
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);//复制数组到新数组 底层调用 System.arraycopy
    }


  //判断最大值
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }



```

## remove方法

> E remove(int index)

```java
   
public E remove(int index) {
        rangeCheck(index);  //a 判断是否越界 是的话异常处理

        modCount++;
        E oldValue = elementData(index); // 获取该索引下的元素

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved); //从移除元素的位置开始复制元素到这个数组
        elementData[--size] = null; // clear to let GC do its work 也就是方便垃圾回收

        return oldValue;
    }

   //a
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
```

**了解  System.arraycopy(int[] arr, int star,int[] arr2, int start2, length)方法**

5个参数：

1. 第一个参数是要被复制的数组
2. 第二个参数是被复制的数字开始复制的下标
3. 第三个参数是目标数组，也就是要把数据放进来的数组
4. 第四个参数是从目标数据第几个下标开始放入数据
5. 第五个参数表示从被复制的数组中拿几个数值放到目标数组中



> boolean remove(Object o)

```java
   
public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }

//具体逻辑  一目了然
  private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        elementData[--size] = null; // clear to let GC do its work
    }

```

## clear()

```java
public void clear() {
    modCount++;

    // clear to let GC do its work
    for (int i = 0; i < size; i++)
        elementData[i] = null;

    size = 0;
}
```


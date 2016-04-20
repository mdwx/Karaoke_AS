package com.fonsview.localktv.dataSources;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {  
	
	private ExecutorService Pool;
	
    /* 私有构造方法，防止被实例化 */  
    private ThreadPool() {    	
    	Pool = Executors.newCachedThreadPool();
    }  
  
    /* 此处使用一个内部类来维护单例 */  
    private static class SingletonFactory {  
        private static ThreadPool instance =  new ThreadPool();  
    }  
  
    /* 获取实例 */  
    public static ThreadPool getInstance() {  
        return SingletonFactory.instance;  
    }  
  
    /* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
    public Object readResolve() {  
    	System.out.println("That is Singleton!");
        return getInstance();  
    }

	public ExecutorService getPool() {
		return Pool;
	}

	public void setPool(ExecutorService pool) {
		Pool = pool;
	}  

} 
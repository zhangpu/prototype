package org.acooly.memcached;

/**
 * Memcached 常用功能接口定义
 * 无过期时间和flags支持，无append,prepend,replace,incr,decr等操作
 * @author zhangpu
 *
 */
public interface MemcachedClient {

	String get(String key);
	
	CacheItem gets(String key);
	
	boolean add(String key,String value);
	
	boolean set(String key,String value);
	
	boolean cas(String key,String value,long unique);
	
	boolean flushAll();
	
}

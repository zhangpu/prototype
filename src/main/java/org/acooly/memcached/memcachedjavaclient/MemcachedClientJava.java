package org.acooly.memcached.memcachedjavaclient;

import org.acooly.memcached.CacheItem;
import org.acooly.memcached.MemcachedClient;

import com.danga.MemCached.MemCachedClient;
import com.schooner.MemCached.MemcachedItem;

public class MemcachedClientJava implements MemcachedClient {

	MemCachedClient mmc = MemcachedClientFactory.getInstance();
	public boolean add(String key, String value) {
		return mmc.add(key, value);
	}

	public boolean cas(String key, String value, long unique) {
		return mmc.cas(key, value, unique);
	}

	public String get(String key) {
		return (String) mmc.get(key);
	}

	public CacheItem gets(String key) {
		MemcachedItem item = mmc.gets(key);
		return new CacheItem(key, (String) item.getValue(), item.getCasUnique());
	}

	public boolean set(String key, String value) {
		return mmc.set(key, value);
	}

	public boolean flushAll() {
		return mmc.flushAll();
	}

}

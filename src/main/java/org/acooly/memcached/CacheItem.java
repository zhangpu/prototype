package org.acooly.memcached;

public class CacheItem {

	private String key;
	private String value;
	private long unique;
	
	public CacheItem() {
		super();
	}
	
	public CacheItem(String key, String value, long unique) {
		super();
		this.key = key;
		this.value = value;
		this.unique = unique;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getUnique() {
		return unique;
	}
	public void setUnique(long unique) {
		this.unique = unique;
	}
	
	
}

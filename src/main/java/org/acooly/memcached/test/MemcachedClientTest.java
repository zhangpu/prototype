package org.acooly.memcached.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.acooly.memcached.CacheItem;
import org.acooly.memcached.MemcachedClient;
import org.acooly.memcached.memcachedjavaclient.MemcachedClientJava;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemcachedClientTest {
	/** 线程数 */
	static int THREAD_COUNT = 10;
	/** 每线程执行次数 */
	static int PER_THREAD_COUNT = 10000;
	
	
	static String KEY = "key";
	static String VALUE = "value";
	static String NEW_VALUE = "newValue";
	
	/** 成功数 */
	int addSuccess;
	int setSuccess;
	int getSuccess;
	int getsSuccess;
	int casSuccess;
	
	/** 执行时间 */
	int addTotal;
	int setTotal;
	int getTotal;
	int getsTotal;
	int casTotal;
	
	int total;
	
	public static final Logger logger = LoggerFactory.getLogger(MemcachedClientTest.class);
	
	public static void main(String[] args) throws Exception{
		MemcachedClient memcachedClient = new MemcachedClientJava();
		MemcachedClientTest test = new MemcachedClientTest(memcachedClient);
		test.startTest();
	}
	
	
	
	
	private MemcachedClient memcachedClient;
	private MemcachedClientTest(MemcachedClient memcachedClient) {
		super();
		this.memcachedClient = memcachedClient;
	}
	
	public void startTest() throws Exception{
		
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
		ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
		
		for (int i = 1; i <= THREAD_COUNT; i++) {
			executor.execute(new Task("Thread-"+i,latch));
		}
		latch.await();
		executor.shutdown();
		
		//total = addTotal+setTotal+getTotal+getsTotal+casTotal;
		total -= THREAD_COUNT * PER_THREAD_COUNT; //减去线程等待
		logger.info("Test thread: " + THREAD_COUNT + "; total times: " + total + "s" + ";count/thread: "+PER_THREAD_COUNT * 5);
		
		logger.info("add success : " + addSuccess + "; success rate: " + (addSuccess * 1.0f / (THREAD_COUNT * PER_THREAD_COUNT)) * 100 + "%");
		logger.info("set success : " + setSuccess + "; success rate: " + (setSuccess * 1.0f / (THREAD_COUNT * PER_THREAD_COUNT)) * 100 + "%");
		logger.info("get success : " + getSuccess + "; success rate: " + (getSuccess * 1.0f / THREAD_COUNT) * 100 + "%");
		logger.info("gets success : " + getsSuccess + "; success rate: " + (getsSuccess * 1.0f / (THREAD_COUNT * PER_THREAD_COUNT)) * 100 + "%");
		logger.info("cas success : " + casSuccess + "; success rate: " + (casSuccess * 1.0f / (THREAD_COUNT * PER_THREAD_COUNT)) * 100 + "%");
		logger.info("Average time: " + (total * 1.0f / (THREAD_COUNT * PER_THREAD_COUNT * 5)));
		
		logger.info("add TPS: " + (THREAD_COUNT * PER_THREAD_COUNT * 1.0) / (addTotal * 1.0 / 1000) + "; time: " + addTotal + "ms");
		logger.info("set TPS: " + (THREAD_COUNT * PER_THREAD_COUNT * 1.0) / (setTotal * 1.0 / 1000) + "; time: " + setTotal + "ms");
		logger.info("get TPS: " + (THREAD_COUNT * PER_THREAD_COUNT * 1.0) / (getTotal * 1.0 / 1000) + "; time: " + getTotal + "ms");
		logger.info("gets TPS: " + (THREAD_COUNT * PER_THREAD_COUNT * 1.0) / (getsTotal * 1.0 / 1000) + "; time: " + getsTotal + "ms");
		logger.info("cas TPS: " + (THREAD_COUNT * PER_THREAD_COUNT * 1.0) / (casTotal * 1.0 / 1000) + "; time: " + casTotal + "ms");
		logger.info("Average TPS: " + (THREAD_COUNT * PER_THREAD_COUNT * 5 * 1.0) / (total * 1.0 / 1000));
		
		memcachedClient.flushAll();
		
	}
	
	class Task implements Runnable{
		
		private String name;
		CountDownLatch latch;
		public Task(String name,CountDownLatch latch) {
			super();
			this.name = name;
			this.latch = latch;
		}

		public void run() {
			long start = System.currentTimeMillis();
			
			for (int i = 0; i < PER_THREAD_COUNT; i++) {
				String key = name+KEY+i;
				long singleStart = System.currentTimeMillis();
				if(memcachedClient.add(key, VALUE)){
					addSuccess++;
				}
				addTotal += System.currentTimeMillis() - singleStart;
				
				singleStart = System.currentTimeMillis();
				if(memcachedClient.set(key, NEW_VALUE)){
					setSuccess++;
				}
				setTotal += System.currentTimeMillis() - singleStart;
				
				singleStart = System.currentTimeMillis();
				if(memcachedClient.get(key) != null){
					getSuccess++;
				}
				getTotal += System.currentTimeMillis() - singleStart;
				
				singleStart = System.currentTimeMillis();
				CacheItem item = memcachedClient.gets(key);
				if(item.getValue() != null){
					getsSuccess++;
				}
				getsTotal += System.currentTimeMillis() - singleStart;
				
				singleStart = System.currentTimeMillis();
				if(memcachedClient.cas(key, NEW_VALUE, item.getUnique())){
					casSuccess++;
				}	
				casTotal += System.currentTimeMillis() - singleStart;
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			long time = System.currentTimeMillis() - start;
			total += time;
			logger.debug(name + " - time:" + time);
			latch.countDown();
		}
		
		
	}
	
	
	
	
}

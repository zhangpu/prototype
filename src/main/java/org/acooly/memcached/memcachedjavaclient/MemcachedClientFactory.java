package org.acooly.memcached.memcachedjavaclient;

import org.acooly.utils.ConfigurableConstants;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/**
 * MemcachedClient 单例(JDK1.5以上)
 * @author zhangpu
 *
 */
public class MemcachedClientFactory extends ConfigurableConstants{

	private static volatile MemCachedClient mmc;

	static {
		init("memcached-client.properties");
		
		//{ "localhost:11211", "localhost:11212", "localhost:11213" };
		String[] servers = getProperty("memcached-servers","").split(",");

		Integer[] weights = null;
		String weightsCfg = getProperty("memcached-weights","");
		if(weightsCfg != null){
			String[] wcfg = weightsCfg.split(",");
			weights = new Integer[wcfg.length];
			for (int i = 0; i < weights.length; i++) {
				weights[i] = Integer.valueOf(wcfg[i]);
			}
		}else{
			weights = new Integer[servers.length];
			for (int i = 0; i < weights.length; i++) {
				weights[i] = 1;
			}
		}
		
		SockIOPool pool = SockIOPool.getInstance();

		pool.setServers(servers);
		pool.setWeights(weights);
		pool.setHashingAlg(SockIOPool.CONSISTENT_HASH);

		pool.setInitConn(getProperty("memcached-initConn",5));
		pool.setMinConn(getProperty("memcached-minConn",5));
		pool.setMaxConn(getProperty("memcached-maxConn",250));
		pool.setMaxIdle(1000 * 60 * 60 * 6);

		pool.setMaintSleep(30);

		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);

		pool.initialize();
	}

	private MemcachedClientFactory() {

	}

	public static MemCachedClient getInstance() {
		if (mmc == null) {
			synchronized (MemCachedClient.class) {
				if (mmc == null) {
					mmc = new MemCachedClient();
				}
			}
		}
		return mmc;
	}

}
package org.acooly.note;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.lang.math.RandomUtils;

public class ConcurrentCalculate {

	
	static final int COUNT = 10000000;
	List<Long> data = new ArrayList<Long>(COUNT);
	
	private int threadNum = 5;
	private long total = 0;
	
	public static void main(String[] args) throws Exception{
		ConcurrentCalculate cc = new ConcurrentCalculate();
		cc.generalTestData();
		cc.concurrent();
		cc.direct();
		System.exit(0);
	}
	
	
	/**
	 * ֱ�Ӽ���
	 */
	public void direct(){
		total = 0;
		long start = System.currentTimeMillis();
		for(long l : data){
			add(algorithm_default(l));
		}
		System.out.println("directSum - total:" + total + "; time:" + (System.currentTimeMillis()-start) + "ms");
	}
	
	
	/**
	 * ��������
	 * @throws Exception
	 */
	public void concurrent() throws Exception{
		long start = System.currentTimeMillis();
		CountDownLatch countDownLatch = new CountDownLatch(threadNum);
		Executor executor = Executors.newFixedThreadPool(threadNum);
		for (int i = 0; i < threadNum; i++) {
			int subCount = COUNT/5;
			List<Long> subData = new ArrayList<Long>(subCount);
			for (int j = 0; j < subCount; j++) {
				subData.add(data.get(i*subCount+j));
			}
			executor.execute(new CalculateSum(subData,countDownLatch));
		}
		countDownLatch.await();

		//do sum
		System.out.println("concurrentSum(threadNum:"+threadNum+") - total:" + total + "; time:" + (System.currentTimeMillis()-start) + "ms");
		
		countDownLatch = null;
		executor = null;
		//System.exit(0);
	}
	
	/**
	 * ͬ�������̼߳�����
	 * @param subtotal
	 */
	public synchronized void add(long subtotal){
		total += subtotal;
	}
	
	
	/**
	 * �������ʼ������
	 */
	public void generalTestData(){
		for (int i = 0; i < COUNT; i++) {
			data.add((long)RandomUtils.nextInt(100));
		}
	}
	
	/**
	 * �㷨ģ��ҵ���߼���Ĭ���㷨���޼���
	 * @param l
	 * @return
	 */
	public long algorithm_default(long l){
		return l;
	}
	
	/**
	 * �㷨ģ��ҵ���߼���Ĭ���㷨������ѧ���㡣
	 * @param l
	 * @return
	 */
	public long algorithm_math(long l){
		return ((l / 10) + 2 + 1 * l / 100 - 3) % 2;
	}	

	
	
	/**
	 * ��������
	 * @author zhangpu
	 *
	 */
	class CalculateSum implements Runnable{

		private List<Long> subData = null;
		private CountDownLatch countDownLatch = null;
		private CalculateSum(List<Long> subData,CountDownLatch countDownLatch) {
			super();
			this.subData = subData;
			this.countDownLatch = countDownLatch;
		}
		
		public void run() {
			int subtotal = 0;
			for (long l : subData) {
				subtotal += algorithm_default(l);
			}
			//System.out.println("subtotal: " + subtotal);
			add(subtotal);
			this.countDownLatch.countDown();
			
		}
		
	}
	
	
}

package org.acooly.prototype.directoryservices;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConcurrentTest {
	static final int TEST_COUNT = 1000;
	DirectoryService ds = new DirectoryService(); //我是线程安全的
	
	int threadNum = 4; //4核CPU
	int threadWaiting = 0;
	
	int createTimeTotal = 0;
	int queryTimeTotal = 0;
	int deleteTimeTotal = 0;
	int renameTimeTotal = 0;
	int moveTimeTotal = 0;
	int listTimeTotal = 0;
	
	
	public static void main(String[] args) throws Exception{
		ConcurrentTest test = new ConcurrentTest();
		test.runTest();
	}
	
	public void runTest() throws Exception{
		CountDownLatch countDownLatch = new CountDownLatch(threadNum);
		Executor executor = Executors.newFixedThreadPool(threadNum);
		for (int i = 0; i < threadNum; i++) {
			Runnable run = new TestTask(countDownLatch,"thread-"+i);
			executor.execute(run);
		}
		countDownLatch.await();
		//所有线程执行完成，执行统计和计算平均性能
		System.out.println("Test thread: " + threadNum + "; threadWaiting: " + threadWaiting + "ms; testCount/thread:" + TEST_COUNT);
		System.out.println("Test 'create',createTimeTotal: " + createTimeTotal + "; Average time:" + createTimeTotal / (threadNum * TEST_COUNT) + "ms");
		System.out.println("Test 'query',Average time:" + queryTimeTotal / (threadNum * TEST_COUNT) + "ms");
		System.out.println("Test 'delete',Average time:" + deleteTimeTotal / (threadNum * TEST_COUNT) + "ms");
		System.out.println("Test 'rename',Average time:" + renameTimeTotal / (threadNum * TEST_COUNT) + "ms");
		System.out.println("Test 'move',Average time:" + moveTimeTotal / (threadNum * TEST_COUNT) + "ms");
		System.out.println("Test 'list',Average time:" + listTimeTotal / (threadNum * TEST_COUNT) + "ms");
		System.exit(0);
	}
	
	
	
	class TestTask implements Runnable{

		CountDownLatch countDownLatch;
		String name;
		
		private TestTask(CountDownLatch countDownLatch,String name) {
			super();
			this.countDownLatch = countDownLatch;
			this.name = name;
		}

		public void run() { 
			System.out.println("Thread :" + name + ",start...");
			long start = System.currentTimeMillis();
			int createTimeThread = 0;
			int queryTimeThread = 0;
			int deleteTimeThread = 0;
			int renameTimeThread = 0;
			int moveTimeThread = 0;
			int listTimeThread = 0;
			for (int i = 0; i < TEST_COUNT; i++) {
				try {
					createTimeThread += ds.createDirTest();
					queryTimeThread += ds.queryPathTest();
					deleteTimeThread += ds.deleteDirTest();
					renameTimeThread += ds.renameDirTest();
					moveTimeThread += ds.moveDirTest();
					listTimeThread += ds.listDirTest();
					Thread.sleep(threadWaiting);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("Thread :" + name + ",execute count: " + i);
			}
			//线程小计
			createTimeTotal += createTimeThread;
			queryTimeTotal += queryTimeThread;
			deleteTimeTotal += deleteTimeThread;
			renameTimeTotal += renameTimeThread;
			moveTimeTotal += moveTimeThread;
			listTimeTotal += listTimeThread;			
			countDownLatch.countDown();
			//System.out.println("Thread: " + name + " createTimeTotal:"+createTimeTotal);
			System.out.println("Thread: " + name + " complete. time: " + (System.currentTimeMillis() - start) + "ms");
		}
		
	}
	
}

package tb.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactorySecuenceNaming implements ThreadFactory {
	private AtomicInteger count;
	private String threadNamePrefix;

	public ThreadFactorySecuenceNaming(String threadNamePrefix) {
		count = new AtomicInteger(0);
		this.threadNamePrefix = threadNamePrefix;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		int current = count.incrementAndGet();
		thread.setName(threadNamePrefix + current);
		return thread;
	}

}
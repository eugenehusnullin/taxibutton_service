package tb2014.service.order;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tb2014.domain.order.OrderCancel;

@Service
public class CancelOrderProcessing {
	private static final Logger log = LoggerFactory.getLogger(CancelOrderProcessing.class);

	class ProcessingRunnable implements Runnable {
		@Override
		public void run() {
			while (processing) {
				try {
					OrderCancel cancelOrder = null;
					synchronized (queue) {
						if (queue.isEmpty()) {
							try {
								queue.wait();
							} catch (InterruptedException ex) {
								return;
							}
						}
						cancelOrder = queue.poll();
					}

					if (cancelOrder != null) {
						orderProcessing.cancelPreparedOrder(cancelOrder.getOrder(), cancelOrder.getReason());
					}
				} catch (Exception ex) {
					log.error(ex.toString());
				}
			}
		}
	}

	@Autowired
	private OrderProcessing orderProcessing;
	private Queue<OrderCancel> queue;
	private Thread mainThread;
	private volatile boolean processing;

	public CancelOrderProcessing() {
		processing = true;
		queue = new LinkedList<>();
	}

	@PostConstruct
	public void startProcessing() {
		Runnable processRunnable = new ProcessingRunnable();
		mainThread = new Thread(processRunnable);
		mainThread.start();
	}

	@PreDestroy
	public void stopProcessing() {
		processing = false;
		mainThread.interrupt();
		try {
			mainThread.join();
		} catch (InterruptedException e) {
		}
	}

	public void addOrderCancel(OrderCancel orderCancel) {
		synchronized (queue) {
			queue.offer(orderCancel);
			queue.notify();
		}
	}
}

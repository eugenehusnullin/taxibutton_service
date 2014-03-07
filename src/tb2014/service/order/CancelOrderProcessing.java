package tb2014.service.order;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tb2014.domain.order.OrderCancel;
import tb2014.utils.ThreadFactorySecuenceNaming;

@Service
public class CancelOrderProcessing {
	private static final Logger log = LoggerFactory.getLogger(CancelOrderProcessing.class);

	class ReceiverOrderCancelRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class CancelOrderRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
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
	private ExecutorService executor;
	private volatile boolean processing;

	public CancelOrderProcessing() {
		processing = true;
		queue = new LinkedList<>();
		executor = Executors.newFixedThreadPool(5, new ThreadFactorySecuenceNaming("OfferOrderProcessing EXECUTOR #"));
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

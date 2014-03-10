package tb2014.service.order;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tb2014.domain.order.OrderCancel;
import tb2014.utils.ThreadFactorySecuenceNaming;

@Service
public class CancelOrderProcessing {
	private static final Logger log = LoggerFactory.getLogger(CancelOrderProcessing.class);

	class ReceiverOrderCancelRunnable implements Runnable {
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
								break;
							}
						}
						cancelOrder = queue.poll();
					}

					if (cancelOrder != null) {
						CancelOrderRunnable offerOrderRunnable = new CancelOrderRunnable(cancelOrder);
						executor.execute(offerOrderRunnable);
					}
				} catch (Exception ex) {
					log.error("CancelOrderProcessing exception in RecieverOrderRunnable.", ex);
				}
			}
		}
	}

	class CancelOrderRunnable implements Runnable {
		private OrderCancel orderCancel;

		public CancelOrderRunnable(OrderCancel orderCancel) {
			this.orderCancel = orderCancel;
		}

		@Override
		public void run() {
			orderProcessing.cancelOfferedOrder(orderCancel.getOrder(), orderCancel.getReason());
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
		Runnable processRunnable = new ReceiverOrderCancelRunnable();
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

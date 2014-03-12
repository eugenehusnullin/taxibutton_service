package tb2014.service.order;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tb2014.domain.order.Order;
import tb2014.utils.ThreadFactorySecuenceNaming;

@Service()
public class OfferOrderProcessing {
	private static final Logger log = LoggerFactory.getLogger(OfferOrderProcessing.class);

	@Value("#{mainSettings['offerorder.threads.count']}")
	private Integer threadsCount;

	@Value("#{mainSettings['offerorder.repeat.pause']}")
	private Integer repeatPause;

	class RecieverOrderRunnable implements Runnable {
		@Override
		public void run() {
			while (processing) {
				try {
					Order order = null;
					synchronized (queue) {
						if (queue.isEmpty()) {
							try {
								queue.wait();
							} catch (InterruptedException e) {
								break;
							}
						}
						order = queue.poll();
					}

					if (order != null) {
						OfferOrderRunnable offerOrderRunnable = new OfferOrderRunnable(order);
						executor.execute(offerOrderRunnable);
					}
				} catch (Exception e) {
					log.error("OfferOrderProcessing exception in RecieverOrderRunnable.", e);
				}
			}
		}
	}

	class OfferOrderRunnable implements Runnable {
		private Order order;

		public OfferOrderRunnable(Order order) {
			this.order = order;
		}

		@Override
		public void run() {
			orderProcessing.offerOrderProcessing(order, repeatPause);
		}
	}

	private Queue<Order> queue;
	private Thread mainThread;
	private volatile boolean processing = true;
	private ExecutorService executor;
	private OrderProcessing orderProcessing;

	@Autowired
	public OfferOrderProcessing(OrderProcessing orderProcessing) {
		this.orderProcessing = orderProcessing;

		queue = new ArrayDeque<Order>();
	}

	@PostConstruct
	public void startProcessing() {
		executor = Executors.newFixedThreadPool(threadsCount, new ThreadFactorySecuenceNaming(
				"OfferOrderProcessing EXECUTOR #"));

		Runnable processRunnable = new RecieverOrderRunnable();
		mainThread = new Thread(processRunnable);
		mainThread.setName("OfferOrderProcessing MAIN THREAD");
		mainThread.start();
	}

	@PreDestroy
	public void stopProcessing() {
		processing = false;
		mainThread.interrupt();
		executor.shutdown();
		try {
			mainThread.join();
		} catch (InterruptedException e) {
		}
	}

	public void addOrder(Order order) {
		synchronized (queue) {
			queue.add(order);
			queue.notifyAll();
		}
	}
}

package tb2014.service.order;

import java.util.ArrayDeque;
import java.util.Date;
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
import tb2014.service.OrderService;
import tb2014.utils.ThreadFactorySecuenceNaming;

@Service()
public class OfferOrderProcessing {
	private static final Logger log = LoggerFactory.getLogger(OfferOrderProcessing.class);

	@Value("#{mainSettings['offerorder.threads.count']}")
	private Integer threadsCount;

	@Value("#{mainSettings['offerorder.repeat.pause']}")
	private Integer repeatPause;

	@Value("#{mainSettings['choosewinner.cancelorder.timeout']}")
	private Integer cancelOrderTimeout;

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
			// do pause before offer, maybe client canceled order
			Date currentDatetime = new Date();
			if (order.getStartOffer().after(currentDatetime)) {
				long diff = order.getStartOffer().getTime() - currentDatetime.getTime();
				try {
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					return;
				}
			}

			Boolean offered = orderService.offerOrderProcessing(order);

			if (offered != null) {
				if (offered) {
					chooseWinnerProcessing.addOrder(order);
				} else {
					CancelOrderProcessing.OrderCancelHolder orderCancelHolder = orderService.checkExpired(order,
							cancelOrderTimeout, new Date());
					if (orderCancelHolder != null) {
						cancelOrderProcessing.addOrderCancel(orderCancelHolder);
					} else {
						try {
							Thread.sleep(repeatPause);
							addOrder(order);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}
	}

	private Queue<Order> queue;
	private Thread mainThread;
	private volatile boolean processing = true;
	private ExecutorService executor;
	@Autowired
	private OrderService orderService;
	@Autowired
	private ChooseWinnerProcessing chooseWinnerProcessing;
	@Autowired
	private CancelOrderProcessing cancelOrderProcessing;

	public OfferOrderProcessing() {
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

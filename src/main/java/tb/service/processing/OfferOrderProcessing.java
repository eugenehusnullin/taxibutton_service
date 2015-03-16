package tb.service.processing;

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

import tb.service.OfferingOrder;
import tb.service.OrderExecHolder;
import tb.service.OrderService;
import tb.utils.ThreadFactorySecuenceNaming;

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
					OrderExecHolder orderExecHolder = null;
					synchronized (queue) {
						if (queue.isEmpty()) {
							try {
								queue.wait();
							} catch (InterruptedException e) {
								break;
							}
						}
						orderExecHolder = queue.poll();
					}

					if (orderExecHolder != null) {
						OfferOrderRunnable offerOrderRunnable = new OfferOrderRunnable(orderExecHolder);
						executor.execute(offerOrderRunnable);
					}
				} catch (Exception e) {
					log.error("OfferOrderProcessing exception in RecieverOrderRunnable.", e);
				}
			}
		}
	}

	class OfferOrderRunnable implements Runnable {
		private OrderExecHolder orderExecHolder;

		public OfferOrderRunnable(OrderExecHolder orderExecHolder) {
			this.orderExecHolder = orderExecHolder;
		}

		@Override
		public void run() {
			try {
				// do pause before offer, maybe client canceled order
				Date currentDatetime = new Date();
				if (orderExecHolder.getOrder().getStartOffer().after(currentDatetime)) {
					long diff = orderExecHolder.getOrder().getStartOffer().getTime() - currentDatetime.getTime();
					try {
						Thread.sleep(diff);
					} catch (InterruptedException e) {
						return;
					}
				}

				Boolean offered = offeringOrder.offer(orderExecHolder.getOrder().getId(), orderExecHolder.getAttemptCount());

				if (offered != null) {
					if (offered) {
						chooseWinnerProcessing.addOrder(orderExecHolder);
					} else {
						CancelOrderProcessing.OrderCancelHolder orderCancelHolder =
								orderService.checkExpired(orderExecHolder.getOrder(), cancelOrderTimeout, new Date());
						if (orderCancelHolder != null) {
							cancelOrderProcessing.addOrderCancel(orderCancelHolder);
						} else {
							try {
								Thread.sleep(repeatPause);
								addOrder(orderExecHolder);
							} catch (InterruptedException e) {
							}
						}
					}
				}
			} catch (Exception ex) {
				try {
					Thread.sleep(repeatPause);
					addOrder(orderExecHolder);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private Queue<OrderExecHolder> queue;
	private Thread mainThread;
	private volatile boolean processing = true;
	private ExecutorService executor;
	@Autowired
	private OrderService orderService;
	@Autowired
	private ChooseWinnerProcessing chooseWinnerProcessing;
	@Autowired
	private CancelOrderProcessing cancelOrderProcessing;
	@Autowired
	private OfferingOrder offeringOrder;

	public OfferOrderProcessing() {
		queue = new ArrayDeque<OrderExecHolder>();
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

	public void addOrder(OrderExecHolder orderExecHolder) {
		synchronized (queue) {
			queue.add(orderExecHolder);
			queue.notifyAll();
		}
	}
}

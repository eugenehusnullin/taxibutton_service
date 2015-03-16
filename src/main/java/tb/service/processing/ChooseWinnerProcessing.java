package tb.service.processing;

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

import tb.domain.order.Order;
import tb.service.OrderExecHolder;
import tb.service.OrderService;
import tb.utils.ThreadFactorySecuenceNaming;

@Service
public class ChooseWinnerProcessing {
	private static final Logger log = LoggerFactory.getLogger(ChooseWinnerProcessing.class);

	@Value("#{mainSettings['choosewinner.threads.count']}")
	private Integer threadsCount;

	@Value("#{mainSettings['choosewinner.repeat.pause']}")
	private Integer repeatPause;

	@Value("#{mainSettings['choosewinner.cancelorder.timeout']}")
	private Integer cancelorderTimeout;

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
						ChooseWinnerRunnable chooseWinnerRunnable = new ChooseWinnerRunnable(orderExecHolder);
						executor.execute(chooseWinnerRunnable);
					}
				} catch (Exception e) {
					log.error("ChooseWinnerProcessing exception in RecieverOrderRunnable.", e);
				}
			}
		}
	}

	class ChooseWinnerRunnable implements Runnable {
		private OrderExecHolder orderExecHolder;

		public ChooseWinnerRunnable(OrderExecHolder orderExecHolder) {
			this.orderExecHolder = orderExecHolder;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(repeatPause);
			} catch (InterruptedException e) {
				return;
			}

			Object object = orderService.chooseWinnerProcessing(orderExecHolder.getOrder(), cancelorderTimeout);
			if (object != null) {
				if (object.getClass().equals(Order.class)) {
					if (orderExecHolder.getAttemptCount() >= 4) {
						addOrder(orderExecHolder);
					} else {
						orderExecHolder.incrementAttempt();
						offerOrderProcessing.addOrder(orderExecHolder);
					}
				} else {
					cancelOrderProcessing.addOrderCancel((CancelOrderProcessing.OrderCancelHolder) object);
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
	private CancelOrderProcessing cancelOrderProcessing;
	@Autowired
	private OfferOrderProcessing offerOrderProcessing;

	public ChooseWinnerProcessing() {
		queue = new ArrayDeque<OrderExecHolder>();
	}

	@PostConstruct
	public void startProcessing() {
		executor = Executors.newFixedThreadPool(threadsCount, new ThreadFactorySecuenceNaming(
				"ChooseWinnerProcessing EXECUTOR #"));

		Runnable processRunnable = new RecieverOrderRunnable();
		mainThread = new Thread(processRunnable);
		mainThread.setName("ChooseWinnerProcessing MAIN THREAD");
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

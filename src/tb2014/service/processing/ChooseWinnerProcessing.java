package tb2014.service.processing;

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
import tb2014.service.OrderService;
import tb2014.utils.ThreadFactorySecuenceNaming;

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
						ChooseWinnerRunnable chooseWinnerRunnable = new ChooseWinnerRunnable(order);
						executor.execute(chooseWinnerRunnable);
					}
				} catch (Exception e) {
					log.error("ChooseWinnerProcessing exception in RecieverOrderRunnable.", e);
				}
			}
		}
	}

	class ChooseWinnerRunnable implements Runnable {
		private Order order;

		public ChooseWinnerRunnable(Order order) {
			this.order = order;
		}

		@Override
		public void run() {
			Object object = orderService.chooseWinnerProcessing(order, cancelorderTimeout);

			if (object != null) {
				if (object.getClass().equals(Order.class)) {
					try {
						Thread.sleep(repeatPause);
						addOrder((Order) object);
					} catch (InterruptedException e) {
					}
				} else {
					cancelOrderProcessing.addOrderCancel((CancelOrderProcessing.OrderCancelHolder) object);
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
	private CancelOrderProcessing cancelOrderProcessing;

	public ChooseWinnerProcessing() {
		queue = new ArrayDeque<Order>();
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

	public void addOrder(Order order) {
		synchronized (queue) {
			queue.add(order);
			queue.notifyAll();
		}
	}
}

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
import org.springframework.stereotype.Service;

import tb2014.business.IOrderAcceptAlacrityBusiness;
import tb2014.business.impl.OrderStatusBusiness;
import tb2014.domain.Broker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.OrderStatusType;
import tb2014.utils.ThreadFactorySecuenceNaming;

@Service
public class ChooseWinnerProcessing {
	private static final Logger log = LoggerFactory.getLogger(ChooseWinnerProcessing.class);

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
			// check right status
			OrderStatus orderStatus = orderStatusBusiness.getLast(order);
			if (orderStatus.getStatus() == OrderStatusType.Cancelled
					|| orderStatus.getStatus() == OrderStatusType.Failed) {
				return;
			}

			
			Broker winner = alacrityBuiness.getWinner(order);
			boolean success = false;
			if (winner != null) {
				success = orderProcessing.giveOrder(order.getId(), winner.getId());
			}

			if (!success) {
				try {
					Thread.sleep(5000);
					synchronized (queue) {
						queue.add(order);
						queue.notifyAll();
					}
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private Queue<Order> queue;
	private Thread mainThread;
	private boolean processing = true;
	private ExecutorService executor;
	private IOrderAcceptAlacrityBusiness alacrityBuiness;
	private OrderProcessing orderProcessing;
	private OrderStatusBusiness orderStatusBusiness;

	@Autowired
	public ChooseWinnerProcessing(IOrderAcceptAlacrityBusiness alacrityBuiness, OrderProcessing orderProcessing,
			OrderStatusBusiness orderStatusBusiness) {
		this.alacrityBuiness = alacrityBuiness;
		this.orderProcessing = orderProcessing;
		this.orderStatusBusiness = orderStatusBusiness;

		queue = new ArrayDeque<Order>();
		executor = Executors.newFixedThreadPool(5, new ThreadFactorySecuenceNaming("ChooseWinnerProcessing EXECUTOR #"));
	}

	@PostConstruct
	public void startProcessing() {
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

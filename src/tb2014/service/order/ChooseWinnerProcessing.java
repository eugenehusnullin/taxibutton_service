package tb2014.service.order;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tb2014.business.IOrderAcceptAlacrityBusiness;
import tb2014.domain.Broker;
import tb2014.domain.order.Order;

@Service
public class ChooseWinnerProcessing {

	class RecieverOrderRunnable implements Runnable {
		@Override
		public void run() {
			while (processing) {
				Order order = null;
				synchronized (queue) {
					order = queue.poll();
					if (order == null) {
						try {
							queue.wait();
						} catch (InterruptedException e) {
							break;
						}
					}
				}

				if (order != null) {
					ChooseWinnerRunnable chooseWinnerRunnable = new ChooseWinnerRunnable(order);
					executor.execute(chooseWinnerRunnable);
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
			boolean success = false;
			Broker winner = alacrityBuiness.getWinner(order);

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

	@Autowired
	public ChooseWinnerProcessing(IOrderAcceptAlacrityBusiness alacrityBuiness, OrderProcessing orderProcessing) {
		this.alacrityBuiness = alacrityBuiness;
		this.orderProcessing = orderProcessing;
		queue = new ArrayDeque<Order>();
		executor = Executors.newFixedThreadPool(5);
	}

	@PostConstruct
	public void startProcessing() {
		Runnable processRunnable = new RecieverOrderRunnable();
		mainThread = new Thread(processRunnable);
		mainThread.start();
	}

	public void stopProcessing() {
		processing = false;
		mainThread.interrupt();
		executor.shutdown();
	}

	public void addOrder(Order order) {
		synchronized (queue) {
			queue.add(order);
			queue.notifyAll();
		}
	}
}

package tb2014.service.order;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tb2014.business.IOrderAcceptAlacrityBusiness;
import tb2014.business.IOrderStatusBusiness;
import tb2014.domain.Broker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.OrderStatusType;

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
				success = orderProcessing.giveOrder(order.getId(), winner);
			}

			if (success) {
				OrderStatus orderStatus = new OrderStatus();
				orderStatus.setDate(new Date());
				orderStatus.setOrder(order);
				orderStatus.setStatus(OrderStatusType.Taked);
				orderStatusBusiness.save(orderStatus);
			} else {
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
	private IOrderStatusBusiness orderStatusBusiness;

	public ChooseWinnerProcessing(IOrderAcceptAlacrityBusiness alacrityBuiness, OrderProcessing orderProcessing,
			IOrderStatusBusiness orderStatusBusiness) {
		this.alacrityBuiness = alacrityBuiness;
		this.orderProcessing = orderProcessing;
		this.orderStatusBusiness = orderStatusBusiness;
		queue = new ArrayDeque<Order>();
		executor = Executors.newFixedThreadPool(5);
	}

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

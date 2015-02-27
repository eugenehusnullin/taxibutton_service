package tb.service.processing;

import java.util.LinkedList;
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
import tb.domain.order.OrderCancelType;
import tb.service.OrderService;
import tb.utils.ThreadFactorySecuenceNaming;

@Service
public class CancelOrderProcessing {
	private static final Logger log = LoggerFactory.getLogger(CancelOrderProcessing.class);

	@Value("#{mainSettings['cancelorder.threads.count']}")
	private Integer threadsCount;

	public static class OrderCancelHolder {
		private Order order;
		private OrderCancelType orderCancelType;

		public Order getOrder() {
			return order;
		}

		public void setOrder(Order order) {
			this.order = order;
		}

		public OrderCancelType getOrderCancelType() {
			return orderCancelType;
		}

		public void setOrderCancelType(OrderCancelType orderCancelType) {
			this.orderCancelType = orderCancelType;
		}
	}

	class ReceiverOrderCancelRunnable implements Runnable {
		@Override
		public void run() {
			while (processing) {
				try {
					OrderCancelHolder orderCancelHolder = null;
					synchronized (queue) {
						if (queue.isEmpty()) {
							try {
								queue.wait();
							} catch (InterruptedException ex) {
								break;
							}
						}
						orderCancelHolder = queue.poll();
					}

					if (orderCancelHolder != null) {
						CancelOrderRunnable offerOrderRunnable = new CancelOrderRunnable(orderCancelHolder);
						executor.execute(offerOrderRunnable);
					}
				} catch (Exception ex) {
					log.error("CancelOrderProcessing exception in RecieverOrderRunnable.", ex);
				}
			}
		}
	}

	class CancelOrderRunnable implements Runnable {
		private OrderCancelHolder orderCancelHolder;

		public CancelOrderRunnable(OrderCancelHolder orderCancelHolder) {
			this.orderCancelHolder = orderCancelHolder;
		}

		@Override
		public void run() {
			orderService.cancelOfferedOrder(orderCancelHolder);
		}
	}

	@Autowired
	private OrderService orderService;
	private Queue<OrderCancelHolder> queue;
	private Thread mainThread;
	private ExecutorService executor;
	private volatile boolean processing;

	public CancelOrderProcessing() {
		processing = true;
		queue = new LinkedList<>();
	}

	@PostConstruct
	public void startProcessing() {
		executor = Executors.newFixedThreadPool(threadsCount, new ThreadFactorySecuenceNaming(
				"CancelOrderProcessing EXECUTOR #"));

		Runnable processRunnable = new ReceiverOrderCancelRunnable();
		mainThread = new Thread(processRunnable);
		mainThread.setName("CancelOrderProcessing MAIN THREAD");
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

	public void addOrderCancel(OrderCancelHolder orderCancelHolder) {
		synchronized (queue) {
			queue.offer(orderCancelHolder);
			queue.notify();
		}
	}
}

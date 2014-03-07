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
import org.springframework.stereotype.Service;

import tb2014.business.IOrderStatusBusiness;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.OrderStatusType;
import tb2014.utils.ThreadFactorySecuenceNaming;

@Service()
public class OfferOrderProcessing {
	private static final Logger log = LoggerFactory.getLogger(OfferOrderProcessing.class);

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
				long diff = currentDatetime.getTime() - order.getStartOffer().getTime();
				try {
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					return;
				}
			}

			// check right status
			OrderStatus orderStatus = orderStatusBusiness.getLast(order);
			if (orderStatus.getStatus() != OrderStatusType.Created) {
				return;
			}

			// do offer
			boolean offered = orderProcessing.offerOrder(order);

			if (offered) {
				chooseWinnerProcessing.addOrder(order);
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
	private OrderProcessing orderProcessing;
	private ChooseWinnerProcessing chooseWinnerProcessing;
	private IOrderStatusBusiness orderStatusBusiness;

	@Autowired
	public OfferOrderProcessing(OrderProcessing orderProcessing, ChooseWinnerProcessing chooseWinnerProcessing,
			IOrderStatusBusiness orderStatusBusiness) {
		this.orderProcessing = orderProcessing;
		this.chooseWinnerProcessing = chooseWinnerProcessing;
		this.orderStatusBusiness = orderStatusBusiness;

		queue = new ArrayDeque<Order>();
		executor = Executors.newFixedThreadPool(5, new ThreadFactorySecuenceNaming("OfferOrderProcessing EXECUTOR #"));
	}

	@PostConstruct
	public void startProcessing() {
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

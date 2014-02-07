package tb2014.service.order;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tb2014.domain.order.Order;

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
					ProcessOrderRunnable processOrderRunnable = new ProcessOrderRunnable(order);
					executor.execute(processOrderRunnable);
				}
			}
		}
	}
	
	class ProcessOrderRunnable implements Runnable {
		private Order order;
		
		public ProcessOrderRunnable(Order order) {
			this.order = order;
		}
		
		@Override
		public void run() {
			// TODO: ������� ����������, ��������� ��� ��������� �� ����
			// ���� ���������� ���, ������� order � ������� ��������������, ����� 5 ���. �����
		}
	}

	private Queue<Order> queue;
	private Thread mainThread;
	private boolean processing = true;
	private ExecutorService executor;
	

	public ChooseWinnerProcessing() {
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
	}
}

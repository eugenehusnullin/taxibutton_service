package tb.service.processing;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tb.domain.order.GeoData;
import tb.service.GeodataService;

@Service
public class GeoDataProcessing {
	private static final Logger log = LoggerFactory.getLogger(GeoDataProcessing.class);

	class ProcessingRunnable implements Runnable {
		@Override
		public void run() {
			while (processing) {
				try {
					GeoData newGeoData = null;
					synchronized (queue) {
						if (queue.isEmpty()) {
							try {
								queue.wait();
							} catch (InterruptedException e) {
								break;
							}
						}
						newGeoData = queue.poll();
					}

					if (newGeoData != null) {
						boolean needupdate = false;
						synchronized (map) {
							GeoData storedGeoData = map.get(newGeoData.getOrder().getId());
							if (storedGeoData == null) {
								needupdate = true;
							} else {
								Double lat = storedGeoData.getLat();
								Double lon = storedGeoData.getLon();

								if (!lat.equals(newGeoData.getLat()) || !lon.equals(newGeoData.getLon())) {
									needupdate = true;
								}
							}

							if (needupdate) {
								map.put(newGeoData.getOrder().getId(), newGeoData);
								geodataService.save(newGeoData);
							}
						}
					}
				} catch (Exception e) {
					log.error("GeoDataProcessing exception in ProcessingRunnable.", e);
				}
			}
		}
	}

	@Autowired
	private GeodataService geodataService;
	private Queue<GeoData> queue;
	private Map<Long, GeoData> map;
	private Thread mainThread;
	private volatile boolean processing;

	public GeoDataProcessing() {
		processing = true;
		queue = new ArrayDeque<>();
		map = new HashMap<>();
	}

	@PostConstruct
	public void startProcessing() {
		Runnable processRunnable = new ProcessingRunnable();
		mainThread = new Thread(processRunnable);
		mainThread.setName("GeoDataProcessing THREAD");
		mainThread.start();
	}

	@PreDestroy
	public void stopProcessing() {
		processing = false;
		mainThread.interrupt();
		try {
			mainThread.join();
		} catch (InterruptedException e) {
		}
	}

	public void addGeoData(GeoData geoData) {
		synchronized (queue) {
			queue.add(geoData);
			queue.notifyAll();
		}
	}

	// Call this on orderComplete, orderCancel, orderFail
	public void removeActual(Long orderId) {
		synchronized (map) {
			map.remove(orderId);
		}
	}

}

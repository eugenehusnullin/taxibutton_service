package tb.maparea;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb.dao.IBrokerDao;
import tb.dao.IMapAreaDao;
import tb.domain.Broker;
import tb.domain.maparea.MapArea;
import tb.utils.HttpUtils;

@Service
@EnableScheduling
public class MapareaSynch {
	private static final Logger log = LoggerFactory.getLogger(MapareaSynch.class);
	@Autowired
	private IBrokerDao brokerDao;
	@Autowired
	private MapareaBuilder mapareaBuilder;
	@Autowired
	private IMapAreaDao mapAreaDao;

	@Scheduled(cron = "0 10 * * * *")
	@Transactional
	public void synch() {
		List<Broker> brokers = brokerDao.getBrokersNeedMapareaSynch();

		for (Broker broker : brokers) {
			if (broker.getMapareaUrl() == null || broker.getMapareaUrl().isEmpty()) {
				continue;
			}

			try {
				InputStream inputStream = HttpUtils.makeGetRequest(broker.getMapareaUrl(), "application/xml");
				if (inputStream != null) {
					Date loadDate = new Date();
					List<MapArea> mapareas = mapareaBuilder.create(inputStream, broker, loadDate);
					updateMapareas(mapareas, broker, loadDate);
				}
			} catch (IOException e) {
				log.error("Maparea synch error: ", e);
			}
		}
	}

	private void updateMapareas(List<MapArea> mapareas, Broker broker, Date loadDate) {
		mapAreaDao.delete(broker);
		for (MapArea mapArea : mapareas) {
			mapAreaDao.add(mapArea);
		}
	}
}

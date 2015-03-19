package tb.service;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import tb.car.CarSynch;
import tb.tariff.TariffSynch;

@Service
public class Starter {
	@Autowired
	private CarSynch carSynch;
	@Autowired
	private TariffSynch tariffSynch;
	private ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

	@PostConstruct
	public void start() {
		taskScheduler.initialize();
		Date d = new Date(new Date().getTime() + (2 * 60 * 1000));
		taskScheduler.schedule(carSynch::synch, d);
		taskScheduler.schedule(tariffSynch::synch, d);
	}
}

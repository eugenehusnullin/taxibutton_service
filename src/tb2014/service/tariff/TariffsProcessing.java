package tb2014.service.tariff;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.Run;
import tb2014.business.IBrokerBusiness;
import tb2014.business.ISimpleTariffBusiness;
import tb2014.domain.Broker;
import tb2014.domain.tariff.SimpleTariff;
import tb2014.utils.NetStreamUtils;

@Service("tariffsProcessing")
public class TariffsProcessing {

	private static final Logger log = LoggerFactory.getLogger(Run.class);

	private IBrokerBusiness brokerBusiness;
	private ISimpleTariffBusiness simpleTariffBusiness;

	@Autowired
	public TariffsProcessing(IBrokerBusiness brokerBusiness, ISimpleTariffBusiness simpleTariffBusiness) {

		this.brokerBusiness = brokerBusiness;
		this.simpleTariffBusiness = simpleTariffBusiness;
	}

	@Transactional
	public void pullBrokersTariffs() {

		List<Broker> brokers = brokerBusiness.getAll();

		for (Broker currentBroker : brokers) {

			try {

				String currentStringResponce = GetTariffsHTTP(currentBroker);

				UpdateBrokerTariffs(currentBroker, currentStringResponce);
			} catch (Exception ex) {
				log.info("Get XML tariffs error: " + ex.toString());
			}

		}
	}

	private String GetTariffsHTTP(Broker broker) {

		String result = null;
		URL url = null;
		HttpURLConnection connection = null;

		try {

			url = new URL(broker.getApiurl() + "/tariff");
			connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/xml");

			result = NetStreamUtils.getStringFromInputStream(connection.getInputStream());

		} catch (Exception ex) {
			log.info("Get XML tariffs HTTP error: " + ex.toString());
		}

		return result;
	}

	private void UpdateBrokerTariffs(Broker broker, String tariff) {

		SimpleTariff simpleTariff = simpleTariffBusiness.get(broker);

		if (simpleTariff == null) {

			simpleTariff = new SimpleTariff();
			simpleTariff.setBroker(broker);
		}

		simpleTariff.setTariffs(tariff);

		simpleTariffBusiness.saveOrUpdate(simpleTariff);
	}
}

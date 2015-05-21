package tb.apiyandex;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import tb.car.CarStateStatusBuilder;
import tb.car.dao.CarDao;
import tb.car.domain.CarState;
import tb.car.domain.CarStateEnum;
import tb.dao.IBrokerDao;
import tb.domain.Broker;
import utils.XmlUtils;

@Controller("apiyandexCarStatusController")
@RequestMapping("/carstatus")
public class CarStatusController {

	private static final Logger logger = LoggerFactory.getLogger(CarStatusController.class);

	@Autowired
	private IBrokerDao brokerDao;
	@Autowired
	private CarDao carDao;
	@Autowired
	private CarStateStatusBuilder carStateBuilder;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public void index(HttpServletRequest request, HttpServletResponse response, @RequestParam("clid") String clid,
			@RequestParam("apikey") String apikey, @RequestParam("uuid") String uuid,
			@RequestParam("status") String status) {

		logger.info("Single status update: broker=" + clid + " car=" + uuid + " status=" + status);

		Broker broker = brokerDao.getByApiId(clid);
		if (broker == null || !broker.getApiKey().equals(apikey)) {
			response.setStatus(403);
		} else {
			CarStateEnum carStateEnum = CarStateStatusBuilder.defineCarState(status);
			if (carStateEnum == CarStateEnum.Undefined) {
				response.setStatus(400);
			} else {
				if (carDao.updateCarStateStatus(broker, uuid, carStateEnum)) {
					response.setStatus(200);
				} else {
					response.setStatus(404);
				}
			}
		}
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public void index(HttpServletRequest request, HttpServletResponse response, @RequestParam("clid") String clid,
			@RequestParam("apikey") String apikey) {
		Broker broker = brokerDao.getByApiId(clid);
		if (broker == null || !broker.getApiKey().equals(apikey)) {
			response.setStatus(403);
		} else {
			try {
				Document document = XmlUtils.buildDomDocument(request.getInputStream());
				if (logger.isInfoEnabled()) {
					try {
						String forLog = XmlUtils.nodeToString(document.getFirstChild());
						logger.info("Multi status update: " + forLog);
					} catch (Exception e) {
						logger.error("Cannot log.");
					}
				}
				List<CarState> carStates = carStateBuilder.createCarStateStatuses(document);
				carDao.updateCarStateStatuses(broker, carStates);
				response.setStatus(200);

			} catch (ParserConfigurationException | SAXException | IOException e) {
				response.setStatus(400);
			}
		}
	}
}

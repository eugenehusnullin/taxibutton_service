package tb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import tb.car.dao.CarDao;
import tb.car.domain.Car4Request;
import tb.car.domain.CarState;
import tb.dao.IBrokerDao;
import tb.dao.IOfferedOrderBrokerDao;
import tb.dao.IOrderDao;
import tb.dao.IOrderStatusDao;
import tb.dao.ITariffDao;
import tb.domain.Broker;
import tb.domain.Tariff;
import tb.domain.order.OfferedOrderBroker;
import tb.domain.order.Order;
import tb.domain.order.OrderStatus;
import tb.domain.order.OrderStatusType;
import tb.service.serialize.YandexOrderSerializer;
import tb.utils.DatetimeUtils;
import tb.utils.HttpUtils;

@Service
public class OfferingOrder {
	private static final Logger log = LoggerFactory.getLogger(OfferingOrder.class);

	@Autowired
	private IOrderDao orderDao;
	@Autowired
	private IOrderStatusDao orderStatusDao;
	@Autowired
	private CarDao carDao;
	@Autowired
	private IBrokerDao brokerDao;
	@Autowired
	private ITariffDao tariffDao;
	@Autowired
	private IOfferedOrderBrokerDao offeredOrderBrokerDao;

	private final static int NOTLATER_MINUTES = 1;
	private final static double COORDINATES_COEF = 0.03;
	private final static int SPEED = 40;
	private final static int MINUTE_IN_HOUR = 60;

	@Transactional
	public Boolean offer(Long orderId, int cntAttempt) {
		Order order = orderDao.get(orderId);

		// common check (order state and e.t.c.)
		if (!checkOrderValid4Offer(order)) {
			return null;
		}

		Collection<Broker> limitBrokers = order.getOfferBrokerList();
		List<Long> limitBrokerIds = null;
		if (limitBrokers != null && limitBrokers.size() > 0) {
			limitBrokerIds = limitBrokers.stream().map(p -> p.getId()).collect(Collectors.toList());
		}

		// define is notlater order
		List<CarState> carStates = carDao.getNearCarStates(
				order.getSource().getLat(),
				order.getSource().getLon(),
				COORDINATES_COEF * (order.getNotlater() ? cntAttempt : 10),
				limitBrokerIds);

		// DEMO
		//List<Long> brokerIdsList = carStates.stream().map(p -> p.getBrokerId()).distinct().collect(Collectors.toList());
		List<Long> brokerIdsList = brokerDao.getActive().stream().map(p -> p.getId()).collect(Collectors.toList());

		Map<Long, Document> messages4Send = null;
		if (order.getNotlater()) {
			carStates = carDao.getCarStatesByRequirements(carStates, order.getRequirements());
			messages4Send = createNotlaterOffer(order, brokerIdsList, carStates);
		} else {
			messages4Send = createExactOffer(order, brokerIdsList);
		}

		return makeOffer(messages4Send, order);
	}

	private boolean makeOffer(Map<Long, Document> messages4Send, Order order) {
		boolean result = false;
		for (Map.Entry<Long, Document> entry : messages4Send.entrySet()) {
			Long brokerId = entry.getKey();
			Document doc = entry.getValue();

			Broker broker = brokerDao.get(brokerId);
			String url = broker.getApiurl() + "/1.x/requestcar";
			try {
				boolean posted = HttpUtils.postDocumentOverHttp(doc, url);
				result |= posted;
				if (posted) {
					OfferedOrderBroker offeredOrderBroker = new OfferedOrderBroker();
					offeredOrderBroker.setOrder(order);
					offeredOrderBroker.setBroker(broker);
					offeredOrderBroker.setTimestamp(new Date());
					offeredOrderBrokerDao.save(offeredOrderBroker);
				}
			} catch (IOException | TransformerException | TransformerFactoryConfigurationError e) {
				log.error("MAKE OFFER.", e);
			}
		}
		return result;
	}

	private Map<Long, Document> createExactOffer(Order order, List<Long> brokerIdsList) {
		Map<Long, Document> messagesMap = new HashMap<Long, Document>();
		for (Long brokerId : brokerIdsList) {
			Broker broker = brokerDao.get(brokerId);
			List<Tariff> tariffs = tariffDao.get(broker);
			List<String> tariffIds = tariffs.stream().map(p -> p.getTariffId()).collect(Collectors.toList());
			Document doc = YandexOrderSerializer.orderToRequestXml(order, tariffIds, null);

			messagesMap.put(brokerId, doc);
		}
		return messagesMap;
	}

	private Map<Long, Document> createNotlaterOffer(Order order, List<Long> brokerIdsList, List<CarState> carStates) {
		double lat = order.getSource().getLat();
		double lon = order.getSource().getLon();
		Map<Long, Document> messagesMap = new HashMap<Long, Document>();
		for (Long brokerId : brokerIdsList) {
			List<CarState> filteredCarStates = carStates.stream()
					.filter(p -> p.getBrokerId() == brokerId)
					.collect(Collectors.toList());

			List<Car4Request> car4RequestList = new ArrayList<Car4Request>();
			for (CarState carState : filteredCarStates) {
				Car4Request car4Request = new Car4Request();
				car4Request.setUuid(carState.getUuid());
				int dist = (int) calcDistance(lat, lon, carState.getLatitude(), carState.getLongitude());
				car4Request.setDist(dist);
				car4Request.setTime((dist * MINUTE_IN_HOUR) / SPEED);
				car4Request.setTariff(carDao.getFirstTariff(carState.getBrokerId(), carState.getUuid()));

				car4RequestList.add(car4Request);
			}

			List<String> tariffsList = car4RequestList.stream()
					.map(p -> p.getTariff())
					.distinct()
					.collect(Collectors.toList());

			Document doc = YandexOrderSerializer.orderToRequestXml(order, tariffsList, car4RequestList);
			messagesMap.put(brokerId, doc);
		}

		return messagesMap;
	}

	private long calcDistance(double lat1, double lon1, double lat2, double lon2) {
		return Math.round(
				Math.sqrt(
						Math.pow(lat1 - lat2, 2) + Math.pow(lon1 - lon2, 2)
						) * 111
				);
		// 111 - km in one degree
	}

	private boolean checkOrderValid4Offer(Order order) {
		OrderStatus orderStatus = orderStatusDao.getLast(order);
		return OrderStatusType.IsValidForOffer(orderStatus.getStatus());
	}

	public static boolean defineNotlater(Date bookingDateUtc) {
		Calendar bookingCalendar = DatetimeUtils.utcToLocal(bookingDateUtc);
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.add(Calendar.MINUTE, NOTLATER_MINUTES);

		return localCalendar.getTime().after(bookingCalendar.getTime());
	}
}

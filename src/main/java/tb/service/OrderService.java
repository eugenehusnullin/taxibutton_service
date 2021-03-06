package tb.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import tb.admin.model.AlacrityModel;
import tb.admin.model.OrderModel;
import tb.admin.model.OrderStatusModel;
import tb.car.dao.CarDao;
import tb.car.domain.Car;
import tb.car.domain.Car4Request;
import tb.car.domain.GeoData;
import tb.dao.IBrokerDao;
import tb.dao.IDeviceDao;
import tb.dao.IOfferedOrderBrokerDao;
import tb.dao.IOrderAcceptAlacrityDao;
import tb.dao.IOrderCancelDao;
import tb.dao.IOrderDao;
import tb.dao.IOrderStatusDao;
import tb.domain.Broker;
import tb.domain.Device;
import tb.domain.order.Feedback;
import tb.domain.order.OfferedOrderBroker;
import tb.domain.order.Order;
import tb.domain.order.OrderAcceptAlacrity;
import tb.domain.order.OrderCancel;
import tb.domain.order.OrderCancelType;
import tb.domain.order.OrderStatus;
import tb.domain.order.OrderStatusType;
import tb.service.exceptions.BrokerNotFoundException;
import tb.service.exceptions.DeviceNotFoundException;
import tb.service.exceptions.NotValidOrderStatusException;
import tb.service.exceptions.OrderNotFoundException;
import tb.service.exceptions.ParseOrderException;
import tb.service.exceptions.WrongData;
import tb.service.processing.CancelOrderProcessing;
import tb.service.processing.OfferOrderProcessing;
import tb.service.serialize.OrderJsonParser;
import tb.service.serialize.YandexOrderSerializer;
import tb.tariffdefinition.TariffDefinitionHelper;
import tb.utils.DatetimeUtils;
import tb.utils.HttpUtils;

@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private IOrderDao orderDao;
	@Autowired
	private IOrderStatusDao orderStatusDao;
	@Autowired
	private IDeviceDao deviceDao;
	@Autowired
	private IBrokerDao brokerDao;
	@Autowired
	private OfferOrderProcessing offerOrderProcessing;
	@Autowired
	private IOfferedOrderBrokerDao offeredOrderBrokerDao;
	@Autowired
	private IOrderCancelDao orderCancelDao;
	@Autowired
	private CancelOrderProcessing cancelorderProcessing;
	@Autowired
	private IOrderAcceptAlacrityDao alacrityDao;
	@Autowired
	private IOrderAcceptAlacrityDao orderAlacrityDao;
	// @Autowired
	// private ITariffDao tariffDao;
	@Autowired
	private CarDao carDao;

	@Value("#{mainSettings['createorder.limit']}")
	private Integer createOrderLimit = 60000;

	@Value("#{mainSettings['offerorder.notlaterminutes']}")
	private int notlaterMinutes;

	// @Autowired
	// private ChooseWinnerProcessing chooseWinnerProcessing;

	@Transactional
	public void saveFeedback(JSONObject feedbackJson) throws OrderNotFoundException, WrongData {
		String apiId = feedbackJson.optString("apiId");
		String orderUuid = feedbackJson.optString("orderId");
		Order order = orderDao.get(orderUuid);
		if (order == null || !order.getDevice().getApiId().equals(apiId)) {
			throw new OrderNotFoundException(orderUuid);
		}

		int rating = feedbackJson.optInt("rating", -1);
		String text = feedbackJson.optString("feedback");
		if (rating == -1 && text == null) {
			throw new WrongData();
		}

		Feedback feedback = new Feedback();
		feedback.setDate(new Date());
		feedback.setOrder(order);
		feedback.setRating(rating);
		feedback.setText(text);

		orderDao.saveFeedback(feedback);
		return;
	}

	@Transactional
	public void informEvent(JSONObject informJson) throws OrderNotFoundException, WrongData {
		String apiId = informJson.optString("apiId");
		String orderUuid = informJson.optString("orderId");
		Order order = orderDao.get(orderUuid);
		if (order == null || !order.getDevice().getApiId().equals(apiId)) {
			throw new OrderNotFoundException(orderUuid);
		}

		OrderStatusType lastStatusType = order.getLastStatus().getStatus();
		if (lastStatusType != OrderStatusType.Driving && lastStatusType != OrderStatusType.Waiting) {
			throw new WrongData();
		}

		Broker broker = order.getBroker();
		String url = broker.getApiurl() + "/1.x/inform";
		try {
			HttpUtils.postRawData(informJson.toString(), url, "UTF-8", "application/json");
		} catch (Exception e) {
			logger.error("INFORM - " + order.getUuid() + ".", e);
		}

		return;
	}

	@Transactional
	public void getOrders(String apiId, String apiKey) throws BrokerNotFoundException {
		Broker broker = brokerDao.getByApiId(apiId, apiKey);
		if (broker == null) {
			throw new BrokerNotFoundException(apiId);
		}

	}

	@Transactional
	public Order initOrder(JSONObject createOrderObject) throws DeviceNotFoundException, ParseOrderException {
		String deviceApiid = createOrderObject.optString("apiId");
		Device device = deviceDao.get(deviceApiid);
		if (device == null) {
			throw new DeviceNotFoundException(deviceApiid);
		}

		Order order = OrderJsonParser.Json2Order(createOrderObject.getJSONObject("order"), device.getPhone(), brokerDao,
				notlaterMinutes);
		order.setDevice(device);
		return order;
	}

	@Transactional
	public void create(Order order) throws ParseOrderException {
		// check booking date
		if (DatetimeUtils.checkTimeout(order.getBookingDate(), createOrderLimit, new Date())) {
			throw new ParseOrderException("bookingdate is out");
		}

		order.setUuid(UUID.randomUUID().toString().replace("-", ""));
		orderDao.save(order);

		// create new order status (Created)
		OrderStatus orderStatus = new OrderStatus();
		orderStatus.setDate(new Date());
		orderStatus.setOrder(order);
		orderStatus.setStatus(OrderStatusType.Created);
		orderStatusDao.save(orderStatus);

		if (order.getNotlater()) {
			OrderExecHolder orderExecHolder = new OrderExecHolder(order);
			offerOrderProcessing.addOrder(orderExecHolder);
		}
	}

	@Transactional
	public void cancel(JSONObject cancelOrderJson)
			throws DeviceNotFoundException, OrderNotFoundException, NotValidOrderStatusException {

		String apiId = cancelOrderJson.getString("apiId");
		String orderUuid = cancelOrderJson.getString("orderId");
		String reason = cancelOrderJson.getString("reason");

		Device device = deviceDao.get(apiId);
		if (device == null) {
			throw new DeviceNotFoundException(apiId);
		}

		Order order = orderDao.get(orderUuid);
		if (order == null) {
			throw new OrderNotFoundException(orderUuid);
		}

		if (!order.getDevice().getApiId().equals(apiId)) {
			throw new OrderNotFoundException(orderUuid);
		}

		cancelAction(order, reason, device);
	}

	private void cancelAction(Order order, String reason, Device device) throws NotValidOrderStatusException {

		OrderCancel orderCancel = new OrderCancel();
		orderCancel.setOrder(order);
		orderCancel.setReason(reason);
		orderCancelDao.save(orderCancel);

		OrderStatus newStatus = new OrderStatus();
		newStatus.setOrder(order);
		newStatus.setStatus(OrderStatusType.Cancelled);
		newStatus.setDate(new Date());
		orderStatusDao.save(newStatus);

		CancelOrderProcessing.OrderCancelHolder orderCancelHolder = new CancelOrderProcessing.OrderCancelHolder();
		orderCancelHolder.setOrder(order);
		orderCancelHolder.setOrderCancelType(OrderCancelType.User);
		cancelorderProcessing.addOrderCancel(orderCancelHolder);
	}

	@Transactional
	public JSONObject getStatus(JSONObject getStatusObject) throws DeviceNotFoundException, OrderNotFoundException {
		String apiId = getStatusObject.getString("apiId");
		Device device = deviceDao.get(apiId);
		if (device == null) {
			throw new DeviceNotFoundException(apiId);
		}

		String orderUuid = getStatusObject.getString("orderId");
		Order order = orderDao.get(orderUuid);
		if (order == null) {
			throw new OrderNotFoundException(orderUuid);
		}

		if (!order.getDevice().getApiId().equals(apiId)) {
			throw new OrderNotFoundException(orderUuid);
		}

		OrderStatus status = orderStatusDao.getLast(order);
		JSONObject statusJson = new JSONObject();
		statusJson.put("orderId", orderUuid);
		statusJson.put("status", status.getStatus().toString());
		statusJson.put("date", status.getDate());
		statusJson.put("description", status.getStatusDescription());

		if (order.getBroker() != null) {
			statusJson.put("executor", order.getBroker().getName());
			Car car = carDao.getCar(order.getBroker().getId(), order.getCarUuid());
			statusJson.put("driver_name", car.getDriverDisplayName());
			statusJson.put("driver_phone", car.getDriverPhone());
			statusJson.put("car_color", car.getCarColor());
			statusJson.put("car_mark", car.getCarModel());
			statusJson.put("car_model", car.getCarModel());
			statusJson.put("car_number", car.getCarNumber());
		}
		return statusJson;
	}

	@Transactional
	public JSONObject getGeodata(JSONObject getGeodataJsonObject)
			throws DeviceNotFoundException, OrderNotFoundException, ParseException {
		String apiId = getGeodataJsonObject.optString("apiId");
		Device device = deviceDao.get(apiId);
		if (device == null) {
			throw new DeviceNotFoundException(apiId);
		}

		String orderUuid = getGeodataJsonObject.optString("orderId");
		Order order = orderDao.get(orderUuid);
		if (order == null) {
			throw new OrderNotFoundException(orderUuid);
		}

		if (!order.getDevice().getApiId().equals(apiId)) {
			throw new OrderNotFoundException(orderUuid);
		}

		List<GeoData> geoDataList = new ArrayList<GeoData>();
		if (OrderStatusType.IsValidForUserGeodata(order.getLastStatus().getStatus())) {
			String lastDateString = getGeodataJsonObject.optString("lastDate");
			Date date = null;
			if (!lastDateString.isEmpty()) {
				date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(lastDateString);
			}
			OrderStatus firstUserGeodataStatus = order.getStatuses().stream()
					.filter(p -> OrderStatusType.IsValidForUserGeodata(p.getStatus()))
					.sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate())).findFirst().get();
			geoDataList = carDao.getGeoData(order.getBroker().getId(), order.getCarUuid(),
					date == null ? firstUserGeodataStatus.getDate() : date);
		}

		JSONObject geoDataJson = new JSONObject();
		geoDataJson.put("orderId", order.getUuid());

		JSONArray geoPointsArrayJson = new JSONArray();
		for (GeoData currentPoint : geoDataList) {

			JSONObject currentPointJson = new JSONObject();
			currentPointJson.put("lat", currentPoint.getLat());
			currentPointJson.put("lon", currentPoint.getLon());
			currentPointJson.put("direction", 1);
			currentPointJson.put("speed", 45);
			currentPointJson.put("category", "n");
			currentPointJson.put("date", currentPoint.getDate());
			geoPointsArrayJson.put(currentPointJson);
		}
		geoDataJson.put("points", geoPointsArrayJson);

		return geoDataJson;
	}

	@Transactional
	public void alacrity(String brokerApiId, String brokerApiKey, String orderUuid, String uuid)
			throws BrokerNotFoundException, OrderNotFoundException {

		Broker broker = brokerDao.getByApiId(brokerApiId);
		if (broker == null) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		if (!broker.getApiKey().equals(brokerApiKey)) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		Order order = orderDao.get(orderUuid);
		if (order == null) {
			throw new OrderNotFoundException(orderUuid);
		}

		if (alacrityDao.get(order, broker, uuid) != null) {
			return;
		}

		OrderAcceptAlacrity alacrity = new OrderAcceptAlacrity();
		alacrity.setBroker(broker);
		alacrity.setOrder(order);
		alacrity.setUuid(uuid);
		alacrity.setDate(new Date());
		alacrityDao.save(alacrity);

		OrderExecHolder orderExecHolder = new OrderExecHolder(order);
		orderExecHolder.setStartChooseWinner(new Date());
		// if (!chooseWinnerProcessing.exists(orderExecHolder)) {
		// chooseWinnerProcessing.addOrder(orderExecHolder);
		// }
	}

	@Transactional
	public void setNewcar(String brokerApiId, String brokerApiKey, String orderUuid, String newcar)
			throws BrokerNotFoundException, OrderNotFoundException {
		Broker broker = brokerDao.getByApiId(brokerApiId);
		if (broker == null) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		if (!broker.getApiKey().equals(brokerApiKey)) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		Order order = orderDao.get(orderUuid);
		if (order == null) {
			throw new OrderNotFoundException(orderUuid);
		}

		if (!order.getBroker().getId().equals(broker.getId())) {
			throw new OrderNotFoundException(orderUuid);
		}

		order.setCarUuid(newcar);
		orderDao.saveOrUpdate(order);
	}

	@Transactional
	public void setStatus(String brokerApiId, String brokerApiKey, String orderUuid, String newStatus,
			String statusParams) throws BrokerNotFoundException, OrderNotFoundException {
		Broker broker = brokerDao.getByApiId(brokerApiId);
		if (broker == null) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		if (!broker.getApiKey().equals(brokerApiKey)) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		Order order = orderDao.get(orderUuid);
		if (order == null) {
			throw new OrderNotFoundException(orderUuid);
		}

		if (order.getBroker() == null || !order.getBroker().getId().equals(broker.getId())) {
			throw new OrderNotFoundException(orderUuid);
		}

		OrderStatus status = new OrderStatus();
		status.setOrder(order);
		status.setDate(new Date());
		status.setStatus(defineOrderStatusType(newStatus));
		status.setStatusDescription(statusParams);
		orderStatusDao.save(status);
	}

	private OrderStatusType defineOrderStatusType(String status) {
		OrderStatusType orderStatusType;
		switch (status) {
		case "driving":
			orderStatusType = OrderStatusType.Driving;
			break;

		case "waiting":
			orderStatusType = OrderStatusType.Waiting;
			break;

		case "transporting":
			orderStatusType = OrderStatusType.Transporting;
			break;

		case "complete":
			orderStatusType = OrderStatusType.Completed;
			break;

		case "cancelled":
			orderStatusType = OrderStatusType.Cancelled;
			break;

		case "failed":
			orderStatusType = OrderStatusType.Failed;
			break;

		default:
			orderStatusType = null;
			break;
		}

		return orderStatusType;
	}

	@Transactional
	public List<OrderModel> listByPage(String orderField, String orderDirection, int start, int count) {
		List<Order> orders = orderDao.getPagination(orderField, orderDirection, start, count);

		List<OrderModel> models = new ArrayList<>();
		for (Order order : orders) {
			OrderModel model = new OrderModel();
			models.add(model);
			model.setId(order.getId());
			model.setBookingDate(order.getBookingDate());
			model.setLastStatus(order.getLastStatus().getStatus().toString());
			model.setPhone(order.getPhone());
			model.setSourceShortAddress(order.getSource().getShortAddress());
			model.setUrgent(order.getNotlater());
			model.setUuid(order.getUuid());
			if (order.getBroker() != null) {
				model.setBrokerName(order.getBroker().getName());
			}
		}

		return models;
	}

	@Transactional
	public Long getAllCount() {
		return orderDao.getAllOrdersCount();
	}

	@Transactional
	public List<AlacrityModel> getAlacrities(Long orderId) {
		Order order = orderDao.get(orderId);
		List<OrderAcceptAlacrity> listAlacrity = orderAlacrityDao.getAll(order);

		List<AlacrityModel> models = new ArrayList<>();
		for (OrderAcceptAlacrity orderAcceptAlacrity : listAlacrity) {
			AlacrityModel model = new AlacrityModel();
			models.add(model);

			model.setId(orderAcceptAlacrity.getOrder().getId());
			model.setDate(orderAcceptAlacrity.getDate());
		}
		return models;
	}

	@Transactional
	public OrderModel getOrder(Long orderId) {
		Order order = orderDao.get(orderId);
		OrderModel model = new OrderModel();
		model.setId(order.getId());
		model.setBookingDate(order.getBookingDate());
		model.setLastStatus(order.getLastStatus().getStatus().toString());
		model.setPhone(order.getPhone());
		model.setSourceShortAddress(order.getSource().getShortAddress());
		model.setUrgent(order.getNotlater());
		model.setUuid(order.getUuid());
		if (order.getBroker() != null) {
			model.setBrokerName(order.getBroker().getName());
		}
		return model;
	}

	@Transactional
	public Order getTrueOrder(Long orderId) {
		return orderDao.get(orderId);
	}

	@Transactional
	public List<OrderStatusModel> getStatuses(Long orderId) {
		Order order = orderDao.get(orderId);
		List<OrderStatus> statusList = orderStatusDao.get(order);

		List<OrderStatusModel> models = new ArrayList<>();
		for (OrderStatus orderStatus : statusList) {
			OrderStatusModel model = new OrderStatusModel();
			models.add(model);

			model.setId(orderStatus.getId());
			model.setDate(orderStatus.getDate());
			model.setStatus(orderStatus.getStatus().toString());
		}

		return models;
	}

	@Autowired
	private TariffDefinitionHelper tariffDefinitionHelper;

	// assign order executer
	@Transactional
	public int giveOrder(Long orderId, OrderAcceptAlacrity winnerAlacrity) {
		Order order = orderDao.get(orderId);

		String tariffIdName = tariffDefinitionHelper.getTariffIdName(order.getSource().getLat(),
				order.getSource().getLon(), order.getOrderVehicleClass());

		Car car = carDao.getCar(winnerAlacrity.getBroker().getId(), winnerAlacrity.getUuid());
		Car4Request car4Request = new Car4Request();
		car4Request.setUuid(car.getUuid());
		car4Request.setTariff(tariffIdName);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(order.getBookingDate());
		calendar.add(Calendar.HOUR_OF_DAY, winnerAlacrity.getBroker().getTimezoneOffset());

		Document doc = YandexOrderSerializer.orderToSetcarXml(order, calendar.getTime(), car4Request, order.getPhone());
		String url = winnerAlacrity.getBroker().getApiurl() + "/1.x/setcar";

		int responseCode = 0;
		try {
			// TODO: DELETE AFTER DEVICE TESTING COMPLETED
			if (order.getDevice().getPhone().startsWith("+++")) {
				responseCode = 200;
			} else {
				responseCode = HttpUtils.postDocumentOverHttp(doc, url, logger).getResponseCode();
			}

			if (responseCode != 200) {
				logger.info("Error giving order to broker: " + winnerAlacrity.getBroker().getId().toString());
			} else {
				order.setBroker(winnerAlacrity.getBroker());
				order.setCarUuid(winnerAlacrity.getUuid());
				orderDao.saveOrUpdate(order);

				OrderStatus orderStatus = new OrderStatus();
				orderStatus.setDate(new Date());
				orderStatus.setOrder(order);
				orderStatus.setStatus(OrderStatusType.Taked);
				orderStatusDao.save(orderStatus);
			}
		} catch (IOException | TransformerException | TransformerFactoryConfigurationError e) {
			logger.error(e.toString());
		}
		return responseCode;
	}

	// cancel order to prepared broker
	@Transactional
	public void cancelOfferedOrder(CancelOrderProcessing.OrderCancelHolder orderCancelHolder) {
		orderCancelHolder.setOrder(orderDao.get(orderCancelHolder.getOrder().getId()));
		List<OfferedOrderBroker> offeredBrokerList = offeredOrderBrokerDao.get(orderCancelHolder.getOrder());
		String reason = orderCancelHolder.getOrderCancelType().toString();
		String params = "orderid=" + orderCancelHolder.getOrder().getUuid() + "&reason=" + reason;

		for (OfferedOrderBroker currentOffer : offeredBrokerList) {
			if (orderCancelHolder.getOrderCancelType() == OrderCancelType.Assigned
					&& orderCancelHolder.getOrder().getBroker().getId().equals(currentOffer.getBroker().getId())) {
				continue;
			}
			String url = currentOffer.getBroker().getApiurl() + "/1.x/cancelrequest";
			HttpUtils.sendHttpGet(url, params);
		}
	}

	@Transactional
	public Object chooseWinnerProcessing(Order order, int cancelOrderTimeout) {
		// check right status
		OrderStatus orderStatus = orderStatusDao.getLast(order);
		if (OrderStatusType.EndProcessingStatus(orderStatus.getStatus())) {
			logger.info("chooseWinner for order id=" + order.getId() + ", detect that is in EndProcessingStatus.");
			return null;
		}

		OrderAcceptAlacrity winnerAlacrity = alacrityDao.getWinner(order);
		int responseCode = 0;
		if (winnerAlacrity != null) {
			logger.info("chooseWinner for order id=" + order.getId() + ", found winner id="
					+ winnerAlacrity.getBroker().getId());
			responseCode = giveOrder(order.getId(), winnerAlacrity);
		}

		if (responseCode == 200) {
			logger.info("chooseWinner for order id=" + order.getId() + ", success gived to winner.");
			CancelOrderProcessing.OrderCancelHolder orderCancelHolder = new CancelOrderProcessing.OrderCancelHolder();
			orderCancelHolder.setOrder(order);
			orderCancelHolder.setOrderCancelType(OrderCancelType.Assigned);
			return orderCancelHolder;

		} else {
			if (responseCode != 0) {
				logger.info("chooseWinner for order id=" + order.getId() + ", fail give to winner.");
				winnerAlacrity.setFail(true);
				winnerAlacrity.setFailHttpCode(responseCode);
				alacrityDao.save(winnerAlacrity);
			}

			logger.info("chooseWinner for order id=" + order.getId() + ", check expired.");
			// check date supply for obsolete order
			CancelOrderProcessing.OrderCancelHolder orderCancelHolder = checkExpired(order, cancelOrderTimeout,
					new Date());
			if (orderCancelHolder != null) {
				logger.info("chooseWinner for order id=" + order.getId() + ", order has expired.");
				return orderCancelHolder;
			} else {
				logger.info("chooseWinner for order id=" + order.getId() + ", paused for next iteration.");
				return order;
			}
		}
	}

	@Transactional
	public CancelOrderProcessing.OrderCancelHolder checkExpired(Order order, int cancelOrderTimeout, Date checkTime) {
		if (DatetimeUtils.checkTimeout(order.getBookingDate(), cancelOrderTimeout, new Date())) {
			OrderStatus failedStatus = new OrderStatus();
			failedStatus.setDate(new Date());
			failedStatus.setOrder(order);
			failedStatus.setStatus(OrderStatusType.Failed);
			orderStatusDao.save(failedStatus);

			CancelOrderProcessing.OrderCancelHolder orderCancelHolder = new CancelOrderProcessing.OrderCancelHolder();
			orderCancelHolder.setOrder(order);
			orderCancelHolder.setOrderCancelType(OrderCancelType.Timeout);
			return orderCancelHolder;
		} else {
			return null;
		}
	}
}

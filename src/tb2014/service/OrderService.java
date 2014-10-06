package tb2014.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import tb2014.admin.model.AlacrityModel;
import tb2014.admin.model.OrderModel;
import tb2014.admin.model.OrderStatusModel;
import tb2014.dao.IBrokerDao;
import tb2014.dao.IDeviceDao;
import tb2014.dao.IGeoDataDao;
import tb2014.dao.IOfferedOrderBrokerDao;
import tb2014.dao.IOrderAcceptAlacrityDao;
import tb2014.dao.IOrderCancelDao;
import tb2014.dao.IOrderDao;
import tb2014.dao.IOrderStatusDao;
import tb2014.domain.Broker;
import tb2014.domain.Device;
import tb2014.domain.order.Car;
import tb2014.domain.order.Driver;
import tb2014.domain.order.Feedback;
import tb2014.domain.order.GeoData;
import tb2014.domain.order.OfferedOrderBroker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderAcceptAlacrity;
import tb2014.domain.order.OrderCancel;
import tb2014.domain.order.OrderCancelType;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.OrderStatusType;
import tb2014.service.exceptions.BrokerNotFoundException;
import tb2014.service.exceptions.DeviceNotFoundException;
import tb2014.service.exceptions.WrongData;
import tb2014.service.exceptions.NotValidOrderStatusException;
import tb2014.service.exceptions.OrderNotFoundException;
import tb2014.service.exceptions.ParseOrderException;
import tb2014.service.processing.CancelOrderProcessing;
import tb2014.service.processing.GeoDataProcessing;
import tb2014.service.processing.OfferOrderProcessing;
import tb2014.service.serialize.OrderJsonParser;
import tb2014.service.serialize.OrderSerializer;
import tb2014.utils.DatetimeUtil;

@Service
public class OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

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
	private IGeoDataDao geoDataDao;
	@Autowired
	private IOrderAcceptAlacrityDao alacrityDao;
	@Autowired
	private GeoDataProcessing geoDataProcessing;
	@Autowired
	private IOrderAcceptAlacrityDao orderAlacrityDao;

	@Value("#{mainSettings['offerorder.wait.pause']}")
	private Integer waitPause;

	@Value("#{mainSettings['createorder.limit']}")
	private Integer createOrderLimit = 60000;

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
	public void getOrders(String apiId, String apiKey) throws BrokerNotFoundException {
		Broker broker = brokerDao.getByApiId(apiId, apiKey);
		if (broker == null) {
			throw new BrokerNotFoundException(apiId);
		}

	}

	@Transactional
	public String createFromBroker(JSONObject createOrderObject) throws BrokerNotFoundException, ParseOrderException {
		String apiId = createOrderObject.optString("apiId");
		String apiKey = createOrderObject.optString("apiKey");
		if (apiId == null || apiKey == null) {
			throw new BrokerNotFoundException(apiId);
		}

		Broker broker = brokerDao.getByApiId(apiId, apiKey);
		if (broker == null) {
			throw new BrokerNotFoundException(apiId);
		}

		Order order = OrderJsonParser.Json2Order(createOrderObject.getJSONObject("order"), null, brokerDao);
		order.setBrokerCreator(broker);
		create(order);

		return order.getUuid();
	}

	@Transactional
	public String createFromDevice(JSONObject createOrderObject) throws DeviceNotFoundException, ParseOrderException {
		String deviceApiid = createOrderObject.optString("apiId");
		Device device = deviceDao.get(deviceApiid);
		if (device == null) {
			throw new DeviceNotFoundException(deviceApiid);
		}

		Order order = OrderJsonParser.Json2Order(createOrderObject.getJSONObject("order"), device.getPhone(), brokerDao);
		if (order == null) {
			throw new ParseOrderException();
		}
		order.setDevice(device);
		create(order);

		return order.getUuid();
	}

	private void create(Order order) throws ParseOrderException {

		if (order == null) {
			throw new ParseOrderException();
		}

		// check booking date
		if (DatetimeUtil.isTimeoutExpired(order, createOrderLimit, new Date())) {
			throw new ParseOrderException();
		}

		order.setUuid(UUID.randomUUID().toString());
		orderDao.save(order);

		// create new order status (Created)
		OrderStatus orderStatus = new OrderStatus();
		orderStatus.setDate(new Date());
		orderStatus.setOrder(order);
		orderStatus.setStatus(OrderStatusType.Created);
		orderStatusDao.save(orderStatus);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, waitPause);
		order.setStartOffer(cal.getTime());
		offerOrderProcessing.addOrder(order);
	}

	@Transactional
	public void cancel(JSONObject cancelOrderJson) throws DeviceNotFoundException, OrderNotFoundException,
			NotValidOrderStatusException {

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
		OrderStatus status = orderStatusDao.getLast(order);

		if (!OrderStatusType.IsValidForUserCancel(status.getStatus())) {
			throw new NotValidOrderStatusException(status);
		}

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

			OrderAcceptAlacrity oaa = alacrityDao.get(order, order.getBroker());
			Driver driver = oaa.getDriver();
			statusJson.put("driver_name", driver.getName());
			statusJson.put("driver_phone", driver.getPhone());
			Car car = oaa.getCar();
			statusJson.put("car_color", car.getColor());
			statusJson.put("car_mark", car.getMark());
			statusJson.put("car_model", car.getModel());
			statusJson.put("car_number", car.getNumber());
		}
		return statusJson;
	}

	@Transactional
	public JSONObject getGeodata(JSONObject getGeodataJsonObject) throws DeviceNotFoundException,
			OrderNotFoundException, ParseException {
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

		String lastDate = getGeodataJsonObject.optString("lastDate");
		List<GeoData> geoDataList = null;

		if (lastDate.isEmpty()) {
			geoDataList = geoDataDao.getAll(order);
		} else {
			Date date = null;
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(lastDate);
			geoDataList = geoDataDao.getAll(order, date);
		}

		JSONObject geoDataJson = new JSONObject();
		geoDataJson.put("orderId", order.getUuid());

		JSONArray geoPointsArrayJson = new JSONArray();
		for (GeoData currentPoint : geoDataList) {

			JSONObject currentPointJson = new JSONObject();
			currentPointJson.put("lat", currentPoint.getLat());
			currentPointJson.put("lon", currentPoint.getLon());
			currentPointJson.put("direction", currentPoint.getDirection());
			currentPointJson.put("speed", currentPoint.getSpeed());
			currentPointJson.put("category", currentPoint.getCategory());
			currentPointJson.put("date", currentPoint.getDate());
			geoPointsArrayJson.put(currentPointJson);
		}
		geoDataJson.put("points", geoPointsArrayJson);

		return geoDataJson;
	}

	@Transactional
	public void alacrity(String brokerApiId, String brokerApiKey, String orderUuid, Driver driver, Car car)
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

		// проверка того, был-ли данный запрос на готовность выполнить от
		// данного брокера по данному заказу
		if (alacrityDao.get(order, broker) != null) {
			return;
		}

		OrderAcceptAlacrity alacrity = new OrderAcceptAlacrity();
		alacrity.setBroker(broker);
		alacrity.setOrder(order);
		alacrity.setDriver(driver);
		alacrity.setCar(car);
		alacrity.setDate(new Date());
		alacrityDao.save(alacrity);
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

		if (!order.getBroker().getId().equals(broker.getId())) {
			throw new OrderNotFoundException(orderUuid);
		}

		OrderStatus status = new OrderStatus();
		status.setOrder(order);
		status.setDate(new Date());
		status.setStatus(OrderStatusType.valueOf(newStatus));
		status.setStatusDescription(statusParams);
		orderStatusDao.save(status);

		if (OrderStatusType.EndProcessingStatus(status.getStatus())) {
			geoDataProcessing.removeActual(status.getOrder().getId());
		}
	}

	@Transactional
	public void setGeoData(String brokerApiId, String brokerApiKey, String orderUuid, String category,
			String direction, String lat, String lon, String speed) throws BrokerNotFoundException,
			OrderNotFoundException {
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

		GeoData geoData = new GeoData();
		geoData.setOrder(order);
		geoData.setDate(new Date());
		geoData.setLat(Double.parseDouble(lat.replace(',', '.')));
		geoData.setLon(Double.parseDouble(lon.replace(',', '.')));

		if (category != null && !category.isEmpty()) {
			geoData.setCategory(category);
		}

		if (direction != null && !direction.isEmpty()) {
			geoData.setDirection(Integer.parseInt(direction));
		}

		if (speed != null && !speed.isEmpty()) {
			geoData.setSpeed(Double.parseDouble(speed.replace(',', '.')));
		}

		geoDataProcessing.addGeoData(geoData);
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
			model.setUrgent(order.getUrgent());
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
		model.setUrgent(order.getUrgent());
		model.setUuid(order.getUuid());
		if (order.getBroker() != null) {
			model.setBrokerName(order.getBroker().getName());
		}
		return model;
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

	// offer order to all connected brokers (need to apply any rules to share
	// order between bounded set of brokers)
	@Transactional
	public boolean offerOrder(Order order) {

		Collection<Broker> brokers;
		if (order.getOfferBrokerList() == null || order.getOfferBrokerList().size() == 0) {
			brokers = brokerDao.getAll();
		} else {
			brokers = order.getOfferBrokerList();
		}
		Document orderXml = OrderSerializer.OrderToXml(order);
		boolean offered = false;

		for (Broker currentBroker : brokers) {

			try {

				offered = offerOrderHTTP(currentBroker, orderXml);

				if (offered) {
					OfferedOrderBroker offeredOrderBroker = new OfferedOrderBroker();
					offeredOrderBroker.setOrder(order);
					offeredOrderBroker.setBroker(currentBroker);
					offeredOrderBroker.setTimestamp(new Date());
					offeredOrderBrokerDao.save(offeredOrderBroker);
				}
			} catch (Exception ex) {
				log.error("Offer order to broker " + currentBroker.getId() + " error: " + ex.toString());
			}
		}

		return offered;
	}

	// offer order via HTTP protocol
	private boolean offerOrderHTTP(Broker broker, Document document) throws IOException,
			TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {

		String url = broker.getApiurl() + "/offer";
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setReadTimeout(0);

		connection.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

		Source source = new DOMSource(document);
		Result result = new StreamResult(wr);

		TransformerFactory.newInstance().newTransformer().transform(source, result);
		wr.flush();
		wr.close();

		int responceCode = connection.getResponseCode();

		if (responceCode != 200) {
			log.info("Error offering order to broker (code: " + responceCode + "): " + broker.getId().toString());
			return false;
		} else {
			return true;
		}
	}

	// assign order executer
	@Transactional
	public boolean giveOrder(Long orderId, Long brokerId) {

		boolean result = true;
		Broker broker = brokerDao.get(brokerId);
		try {
			Order order = orderDao.get(orderId);

			String url = broker.getApiurl() + "/give";
			url += "?orderId=" + order.getUuid();
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("GET");

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				result = false;
				log.info("Error giving order to broker (code: " + responceCode + "): " + broker.getId().toString());
			} else {
				order.setBroker(broker);
				orderDao.saveOrUpdate(order);

				OrderStatus orderStatus = new OrderStatus();
				orderStatus.setDate(new Date());
				orderStatus.setOrder(order);
				orderStatus.setStatus(OrderStatusType.Taked);
				orderStatusDao.save(orderStatus);
			}
		} catch (Exception ex) {
			result = false;
			log.error("Giving order for broker " + broker.getId() + " error: " + ex.toString());
		}

		return result;
	}

	// cancel order to prepared broker
	@Transactional
	public Boolean cancelOfferedOrder(CancelOrderProcessing.OrderCancelHolder orderCancelHolder) {
		orderCancelHolder.setOrder(orderDao.get(orderCancelHolder.getOrder().getId()));

		Boolean result = true;
		List<OfferedOrderBroker> offeredBrokerList = offeredOrderBrokerDao.get(orderCancelHolder.getOrder());
		String reason = orderCancelHolder.getOrderCancelType().toString();

		String params = "orderId=" + orderCancelHolder.getOrder().getUuid() + "&reason=" + reason;

		for (OfferedOrderBroker currentOffer : offeredBrokerList) {
			if (orderCancelHolder.getOrderCancelType() == OrderCancelType.Assigned
					&& orderCancelHolder.getOrder().getBroker().getId().equals(currentOffer.getBroker().getId())) {
				continue;
			}
			String url = currentOffer.getBroker().getApiurl() + "/cancel";
			int resultCode = sendHttpGet(url, params);

			if (resultCode != 200) {
				result = false;
			}
		}

		return result;
	}

	// sending HTTP GET request
	private int sendHttpGet(String url, String params) {

		String protocol = url.split(":")[0];
		String[] fullAddress = url.split("//")[1].split("/", 2);
		String address = fullAddress[0];
		String path = "/" + fullAddress[1];

		int responseCode = 0;

		try {
			URI uriObject = new URI(protocol, address, path, params, null);

			URL obj = uriObject.toURL();
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("GET");

			responseCode = connection.getResponseCode();
		} catch (Exception ex) {

			System.out.println("Sending HTTP GET to: " + url + " FAILED, error: " + ex.toString());
			responseCode = -1;
		}

		return responseCode;
	}

	@Transactional
	public void deleteOrder(Long orderId) {
		Order order = orderDao.get(orderId);
		orderDao.delete(order);
	}

	@Transactional
	public Object chooseWinnerProcessing(Order order, int cancelOrderTimeout) {
		// check right status
		OrderStatus orderStatus = orderStatusDao.getLast(order);
		if (OrderStatusType.EndProcessingStatus(orderStatus.getStatus())) {
			return null;
		}

		Broker winner = alacrityDao.getWinner(order);
		boolean success = false;
		if (winner != null) {
			success = giveOrder(order.getId(), winner.getId());
		}

		if (!success) {
			// check date supply for obsolete order
			CancelOrderProcessing.OrderCancelHolder orderCancelHolder = checkExpired(order, cancelOrderTimeout,
					new Date());
			if (orderCancelHolder != null) {
				return orderCancelHolder;
			} else {
				return order;
			}
		} else {
			CancelOrderProcessing.OrderCancelHolder orderCancelHolder = new CancelOrderProcessing.OrderCancelHolder();
			orderCancelHolder.setOrder(order);
			orderCancelHolder.setOrderCancelType(OrderCancelType.Assigned);
			return orderCancelHolder;
		}
	}

	@Transactional
	public CancelOrderProcessing.OrderCancelHolder checkExpired(Order order, int cancelOrderTimeout, Date checkTime) {
		if (DatetimeUtil.isTimeoutExpired(order, cancelOrderTimeout, new Date())) {
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

	@Transactional
	public Boolean offerOrderProcessing(Long orderId) {
		Order order = orderDao.get(orderId);

		// check right status
		OrderStatus orderStatus = orderStatusDao.getLast(order);
		if (!OrderStatusType.IsValidForOffer(orderStatus.getStatus())) {
			return null;
		}

		// do offer
		return offerOrder(order);
	}
}

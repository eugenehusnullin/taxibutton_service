package tb2014.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.admin.model.AlacrityModel;
import tb2014.admin.model.OrderModel;
import tb2014.admin.model.OrderStatusModel;
import tb2014.business.IBrokerBusiness;
import tb2014.business.IDeviceBusiness;
import tb2014.business.IGeoDataBusiness;
import tb2014.business.IOfferedOrderBrokerBusiness;
import tb2014.business.IOrderAcceptAlacrityBusiness;
import tb2014.business.IOrderBusiness;
import tb2014.business.IOrderCancelBusiness;
import tb2014.business.IOrderStatusBusiness;
import tb2014.business.impl.OrderAcceptAlacrityBusiness;
import tb2014.domain.Broker;
import tb2014.domain.Device;
import tb2014.domain.order.Car;
import tb2014.domain.order.Driver;
import tb2014.domain.order.GeoData;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderAcceptAlacrity;
import tb2014.domain.order.OrderCancel;
import tb2014.domain.order.OrderCancelType;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.OrderStatusType;
import tb2014.service.geo.GeoDataProcessing;
import tb2014.service.order.CancelOrderProcessing;
import tb2014.service.order.OfferOrderProcessing;
import tb2014.service.serialize.OrderJsonParser;

@Service
public class OrderService {

	@Autowired
	private IOrderBusiness orderBusiness;
	@Autowired
	private IOrderStatusBusiness orderStatusBusiness;
	@Autowired
	private IDeviceBusiness deviceBusiness;
	@Autowired
	private IBrokerBusiness brokerBusiness;
	@Autowired
	private OfferOrderProcessing offerOrderProcessing;
	@Autowired
	private IOfferedOrderBrokerBusiness offeredOrderBrokerBusiness;
	@Autowired
	private IOrderCancelBusiness orderCancelBusiness;
	@Autowired
	private CancelOrderProcessing cancelorderProcessing;
	@Autowired
	private IGeoDataBusiness geoDataBusiness;
	@Autowired
	private IOrderAcceptAlacrityBusiness alacrityBuiness;
	@Autowired
	private GeoDataProcessing geoDataProcessing;
	@Autowired
	private OrderAcceptAlacrityBusiness orderAlacrityBusiness;

	@Value("#{mainSettings['offerorder.wait.pause']}")
	private Integer waitPause;

	@Transactional
	public String create(JSONObject createOrderObject) throws DeviceNotFoundException, ParseOrderException {

		String deviceApiid = createOrderObject.optString("apiId");
		Device device = deviceBusiness.get(deviceApiid);
		if (device == null) {
			throw new DeviceNotFoundException(deviceApiid);
		}

		JSONObject orderObject = createOrderObject.getJSONObject("order");
		Order order = OrderJsonParser.Json2Order(orderObject, brokerBusiness);

		if (order == null) {
			throw new ParseOrderException();
		}

		order.setDevice(device);
		order.setUuid(UUID.randomUUID().toString());
		orderBusiness.saveNewOrder(order);

		// create new order status (Created)
		OrderStatus orderStatus = new OrderStatus();
		orderStatus.setDate(new Date());
		orderStatus.setOrder(order);
		orderStatus.setStatus(OrderStatusType.Created);
		orderStatusBusiness.save(orderStatus);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, waitPause);
		order.setStartOffer(cal.getTime());
		offerOrderProcessing.addOrder(order);

		return order.getUuid();
	}

	@Transactional
	public void cancel(JSONObject cancelOrderJson) throws DeviceNotFoundException, OrderNotFoundException,
			NotValidOrderStatusException {

		String apiId = cancelOrderJson.getString("apiId");
		String orderUuid = cancelOrderJson.getString("orderId");
		String reason = cancelOrderJson.getString("reason");

		Device device = deviceBusiness.get(apiId);
		if (device == null) {
			throw new DeviceNotFoundException(apiId);
		}

		Order order = orderBusiness.getByUuid(orderUuid);
		if (order == null) {
			throw new OrderNotFoundException(orderUuid);
		}

		if (!order.getDevice().getApiId().equals(apiId)) {
			throw new OrderNotFoundException(orderUuid);
		}

		cancelAction(order, reason, device);
	}

	private void cancelAction(Order order, String reason, Device device) throws NotValidOrderStatusException {
		OrderStatus status = orderStatusBusiness.getLast(order);

		if (!OrderStatusType.IsValidForUserCancel(status.getStatus())) {
			throw new NotValidOrderStatusException(status);
		}

		OrderCancel orderCancel = new OrderCancel();
		orderCancel.setOrder(order);
		orderCancel.setReason(reason);
		orderCancelBusiness.save(orderCancel);

		OrderStatus newStatus = new OrderStatus();
		newStatus.setOrder(order);
		newStatus.setStatus(OrderStatusType.Cancelled);
		newStatus.setDate(new Date());
		orderStatusBusiness.save(newStatus);

		CancelOrderProcessing.OrderCancelHolder orderCancelHolder = new CancelOrderProcessing.OrderCancelHolder();
		orderCancelHolder.setOrder(order);
		orderCancelHolder.setOrderCancelType(OrderCancelType.User);
		cancelorderProcessing.addOrderCancel(orderCancelHolder);
	}

	@Transactional
	public JSONObject getStatus(JSONObject getStatusObject) throws DeviceNotFoundException, OrderNotFoundException {

		String apiId = getStatusObject.getString("apiId");
		Device device = deviceBusiness.get(apiId);
		if (device == null) {
			throw new DeviceNotFoundException(apiId);
		}

		String orderUuid = getStatusObject.getString("orderId");
		Order order = orderBusiness.getByUuid(orderUuid);
		if (order == null) {
			throw new OrderNotFoundException(orderUuid);
		}

		if (!order.getDevice().getApiId().equals(apiId)) {
			throw new OrderNotFoundException(orderUuid);
		}

		OrderStatus status = orderStatusBusiness.getLast(order);

		JSONObject statusJson = new JSONObject();
		statusJson.put("orderId", orderUuid);
		statusJson.put("status", status.getStatus().toString());
		statusJson.put("date", status.getDate());
		if (order.getBroker() == null) {
			statusJson.put("executor", order.getBroker().getName());
		}

		return statusJson;
	}

	@Transactional
	public JSONObject getGeodata(JSONObject getGeodataJsonObject) throws DeviceNotFoundException,
			OrderNotFoundException, ParseException {

		String apiId = getGeodataJsonObject.optString("apiId");
		Device device = deviceBusiness.get(apiId);
		if (device == null) {
			throw new DeviceNotFoundException(apiId);
		}

		String orderUuid = getGeodataJsonObject.optString("orderId");
		Order order = orderBusiness.getByUuid(orderUuid);
		if (order == null) {
			throw new OrderNotFoundException(orderUuid);
		}

		if (!order.getDevice().getApiId().equals(apiId)) {
			throw new OrderNotFoundException(orderUuid);
		}

		String lastDate = getGeodataJsonObject.optString("lastDate");
		List<GeoData> geoDataList = null;

		if (lastDate.isEmpty()) {
			geoDataList = geoDataBusiness.getAll(order);
		} else {
			Date date = null;
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(lastDate);
			geoDataList = geoDataBusiness.getAll(order, date);
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

		Broker broker = brokerBusiness.getByApiId(brokerApiId);
		if (broker == null) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		if (!broker.getApiKey().equals(brokerApiKey)) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		Order order = orderBusiness.getByUuid(orderUuid);
		if (order == null) {
			throw new OrderNotFoundException(orderUuid);
		}

		// проверка того, был-ли данный запрос на готовность выполнить от
		// данного брокера по данному заказу
		if (alacrityBuiness.get(order, broker) != null) {
			return;
		}

		OrderAcceptAlacrity alacrity = new OrderAcceptAlacrity();
		alacrity.setBroker(broker);
		alacrity.setOrder(order);
		alacrity.setDriver(driver);
		alacrity.setCar(car);
		alacrity.setDate(new Date());
		alacrityBuiness.save(alacrity);
	}

	@Transactional
	public void setStatus(String brokerApiId, String brokerApiKey, String orderUuid, String newStatus)
			throws BrokerNotFoundException, OrderNotFoundException {
		Broker broker = brokerBusiness.getByApiId(brokerApiId);
		if (broker == null) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		if (!broker.getApiKey().equals(brokerApiKey)) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		Order order = orderBusiness.getByUuid(orderUuid);
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
		orderStatusBusiness.save(status);

		if (OrderStatusType.EndProcessingStatus(status.getStatus())) {
			geoDataProcessing.removeActual(status.getOrder().getId());
		}
	}

	@Transactional
	public void setGeoData(String brokerApiId, String brokerApiKey, String orderUuid, String category,
			String direction, String lat, String lon, String speed) throws BrokerNotFoundException,
			OrderNotFoundException {
		Broker broker = brokerBusiness.getByApiId(brokerApiId);
		if (broker == null) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		if (!broker.getApiKey().equals(brokerApiKey)) {
			throw new BrokerNotFoundException(brokerApiId);
		}

		Order order = orderBusiness.getByUuid(orderUuid);
		if (order == null) {
			throw new OrderNotFoundException(orderUuid);
		}

		if (!order.getBroker().getId().equals(broker.getId())) {
			throw new OrderNotFoundException(orderUuid);
		}

		GeoData geoData = new GeoData();
		geoData.setOrder(order);
		geoData.setDate(new Date());
		geoData.setLat(Double.parseDouble(lat));
		geoData.setLon(Double.parseDouble(lon));

		if (category != null && !category.isEmpty()) {
			geoData.setCategory(category);
		}

		if (direction != null && !direction.isEmpty()) {
			geoData.setDirection(Integer.parseInt(direction));
		}

		if (speed != null && !speed.isEmpty()) {
			geoData.setSpeed(Double.parseDouble(speed));
		}

		geoDataProcessing.addGeoData(geoData);
	}

	@Transactional
	public List<OrderModel> listByPage(String orderField, String orderDirection, int start, int count) {
		List<Order> orders = orderBusiness.getPagination(orderField, orderDirection, start, count);

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
		return orderBusiness.getAllOrdersCount();
	}

	@Transactional
	public List<AlacrityModel> getAlacrities(Long orderId) {
		Order order = orderBusiness.get(orderId);
		List<OrderAcceptAlacrity> listAlacrity = orderAlacrityBusiness.getAll(order);

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
		Order order = orderBusiness.get(orderId);
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
		Order order = orderBusiness.get(orderId);
		List<OrderStatus> statusList = orderStatusBusiness.get(order);

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
}

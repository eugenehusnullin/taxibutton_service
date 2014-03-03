package tb2014.apibroker;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb2014.business.IBrokerBusiness;
import tb2014.business.IGeoDataBusiness;
import tb2014.business.IOrderAcceptAlacrityBusiness;
import tb2014.business.IOrderBusiness;
import tb2014.business.IOrderStatusBusiness;
import tb2014.domain.Broker;
import tb2014.domain.order.Car;
import tb2014.domain.order.Driver;
import tb2014.domain.order.GeoData;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderAcceptAlacrity;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.OrderStatusType;

@RequestMapping("/order")
@Controller("apiBrokerOrderController")
public class OrderController {
	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

	private IBrokerBusiness brokerBusiness;
	private IOrderBusiness orderBusiness;
	private IOrderAcceptAlacrityBusiness alacrityBuiness;
	private IOrderStatusBusiness orderStatusBusiness;
	private IGeoDataBusiness geoDataBusiness;

	@Autowired
	public OrderController(IOrderBusiness orderBusiness, IBrokerBusiness brokerBusiness,
			IOrderAcceptAlacrityBusiness alacrityBuiness, IOrderStatusBusiness orderStatusBusiness,
			IGeoDataBusiness geoDataBusiness) {
		this.orderBusiness = orderBusiness;
		this.brokerBusiness = brokerBusiness;
		this.alacrityBuiness = alacrityBuiness;
		this.orderStatusBusiness = orderStatusBusiness;
		this.geoDataBusiness = geoDataBusiness;
	}

	@RequestMapping(value = "/alacrity", method = RequestMethod.POST)
	public void alacrity(HttpServletRequest request, HttpServletResponse response) {

		Broker broker = brokerBusiness.getByApiId(request.getParameter("apiId"));
		if (broker == null) {
			response.setStatus(403);
			return;
		}

		if (broker.getApiKey().trim().equals(request.getParameter("apiKey").trim()) == false) {
			response.setStatus(403);
			return;
		}

		Order order = orderBusiness.get(request.getParameter("orderId"));
		if (order == null) {
			response.setStatus(404);
			return;
		}

		// проверка того, был-ли данный запрос на готовность выполнить от
		// данного брокера по данному заказу
		if (alacrityBuiness.get(order, broker) != null) {
			response.setStatus(200);
			return;
		}

		Driver driver = new Driver();
		driver.setName(request.getParameter("driverName"));
		driver.setSecondName(request.getParameter("driverSecondName"));
		driver.setThirdName(request.getParameter("driverThirdName"));
		driver.setPhone(request.getParameter("driverPhone"));

		Car car = new Car();
		car.setNumber(request.getParameter("carNumber"));
		car.setColor(request.getParameter("carColor"));
		car.setMark(request.getParameter("carMark"));
		car.setModel(request.getParameter("carModel"));

		OrderAcceptAlacrity alacrity = new OrderAcceptAlacrity();
		alacrity.setBroker(broker);
		alacrity.setOrder(order);
		alacrity.setDriver(driver);
		alacrity.setCar(car);
		alacrity.setDate(new Date());
		alacrityBuiness.save(alacrity);

		response.setStatus(200);
	}

	@RequestMapping(value = "/setStatus", method = RequestMethod.POST)
	public void setStatus(HttpServletRequest request, HttpServletResponse response) {

		Broker broker = getBroker(request.getParameter("apiId"), request.getParameter("apiKey"));
		if (broker == null) {
			response.setStatus(403);
			return;
		}

		Order order = orderBusiness.get(request.getParameter("orderId"));
		if (this.checkOrder(request.getParameter("apiId"), order, broker) == false) {
			response.setStatus(403);
			return;
		}

		if (OrderStatusType.valueOf(request.getParameter("status")) == null) {
			response.setStatus(404);
			return;
		}

		OrderStatus status = new OrderStatus();
		status.setOrder(order);
		status.setDate(new Date());
		status.setStatus(OrderStatusType.valueOf(request.getParameter("status")));

		orderStatusBusiness.save(status);

		response.setStatus(200);
	}

	@RequestMapping(value = "/setGeoData", method = RequestMethod.POST)
	public void setGeoData(HttpServletRequest request, HttpServletResponse response) {

		Broker broker = getBroker(request.getParameter("apiId"), request.getParameter("apiKey"));
		if (broker == null) {
			response.setStatus(403);
			return;
		}

		Order order = orderBusiness.get(Long.parseLong(request.getParameter("orderId")));
		if (this.checkOrder(request.getParameter("apiId"), order, broker) == false) {
			response.setStatus(403);
			return;
		}

		try {
			GeoData geoData = new GeoData();
			geoData.setOrder(order);
			geoData.setDate(new Date());

			if (request.getParameter("category") != null && !request.getParameter("category").isEmpty()) {
				geoData.setCategory(request.getParameter("category"));
			}

			if (request.getParameter("direction") != null && !request.getParameter("direction").isEmpty()) {
				geoData.setDirection(Integer.parseInt(request.getParameter("direction")));
			}

			geoData.setLat(Double.parseDouble(request.getParameter("lat")));
			geoData.setLon(Double.parseDouble(request.getParameter("lon")));

			if (request.getParameter("speed") != null && !request.getParameter("speed").isEmpty()) {
				geoData.setSpeed(Double.parseDouble(request.getParameter("speed")));
			}

			geoDataBusiness.save(geoData);
		} catch (Exception ex) {
			log.warn("Parsing geo data error.", ex);
			response.setStatus(403);
			return;
		}

		response.setStatus(200);
	}

	private Broker getBroker(String apiId, String apiKey) {
		Broker broker = brokerBusiness.getByApiId(apiId);
		if (broker != null) {
			if (broker.getApiKey().trim().equals(apiKey.trim()) == false) {
				broker = null;
			}
		}
		return broker;
	}

	private Boolean checkOrder(String apiId, Order order, Broker broker) {
		if (order == null) {
			return false;
		}

		if (order.getBroker() == null || order.getBroker().getId() != broker.getId()) {
			return false;
		}

		return true;
	}
}

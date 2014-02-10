package tb2014.apibroker;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

@RequestMapping("/apibroker/order")
@Controller("apiBrokerOrderController")
public class OrderController {

	private IBrokerBusiness brokerBusiness;
	private IOrderBusiness orderBusiness;
	private IOrderAcceptAlacrityBusiness alacrityBuiness;
	private IOrderStatusBusiness orderStatusBusiness;
	private IGeoDataBusiness geoDataBusiness;

	@Autowired
	public OrderController(IOrderBusiness orderBusiness,
			IBrokerBusiness brokerBusiness,
			IOrderAcceptAlacrityBusiness alacrityBuiness,
			IOrderStatusBusiness orderStatusBusiness,
			IGeoDataBusiness geoDataBusiness) {
		this.orderBusiness = orderBusiness;
		this.brokerBusiness = brokerBusiness;
		this.alacrityBuiness = alacrityBuiness;
		this.orderStatusBusiness = orderStatusBusiness;
		this.geoDataBusiness = geoDataBusiness;
	}

	@RequestMapping(value = "/alacrity", method = RequestMethod.POST)
	public void alacrity(HttpServletRequest request,
			HttpServletResponse response) {

		Broker broker = brokerBusiness
				.getByApiId(request.getParameter("apiId"));
		Order order = orderBusiness.get(Long.parseLong(request
				.getParameter("orderId")));

		if (broker == null) {
			response.setStatus(403);
			return;
		}

		if (broker.getApiKey().trim()
				.equals(request.getParameter("apiKey").trim()) == false) {
			response.setStatus(403);
			return;
		}

		if (order == null) {
			response.setStatus(404);
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
	public void setStatus(HttpServletRequest request,
			HttpServletResponse response) {

		if (this.checkBroker(request.getParameter("apiId"),
				request.getParameter("apiKey")) == false) {

			response.setStatus(403);
			return;
		}

		Order order = orderBusiness.get(Long.parseLong(request
				.getParameter("orderId")));

		if (this.checkOrder(request.getParameter("apiId"), order) == false) {

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
	public void setGeoData(HttpServletRequest request,
			HttpServletResponse response) {

		if (this.checkBroker(request.getParameter("apiId"),
				request.getParameter("apiKey")) == false) {

			response.setStatus(403);
			return;
		}

		Order order = orderBusiness.get(Long.parseLong(request
				.getParameter("orderId")));

		if (this.checkOrder(request.getParameter("apiId"), order) == false) {

			response.setStatus(403);
			return;
		}

		try {
			GeoData geoData = new GeoData();

			geoData.setOrder(order);
			geoData.setDate(new Date());
			geoData.setCategory(request.getParameter("category"));
			geoData.setDirection(Integer.parseInt(request
					.getParameter("direction")));
			geoData.setLat(Double.parseDouble(request.getParameter("lat")));
			geoData.setLon(Double.parseDouble(request.getParameter("lon")));
			geoData.setSpeed(Double.parseDouble(request.getParameter("speed")));

			geoDataBusiness.save(geoData);
		} catch (Exception ex) {

			System.out.println("Parsing geo data error: " + ex.toString());
			response.setStatus(403);

			return;
		}

		response.setStatus(200);
	}

	private Boolean checkBroker(String apiId, String apiKey) {

		Broker broker = brokerBusiness.getByApiId(apiId);

		if (broker == null) {
			return false;
		}

		if (broker.getApiKey().trim().equals(apiKey.trim()) == false) {
			return false;
		}

		return true;
	}

	private Boolean checkOrder(String apiId, Order order) {

		Broker broker = brokerBusiness.getByApiId(apiId);

		if (order == null) {
			return false;
		}

		if (order.getBroker() == null
				|| order.getBroker().getId() != broker.getId()) {
			return false;
		}

		return true;
	}
}
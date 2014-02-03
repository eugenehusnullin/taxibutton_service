package tb2014.mvc.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb2014.business.IBrokerBusiness;
import tb2014.business.IOrderAcceptAlacrityBusiness;
import tb2014.business.IOrderBusiness;
import tb2014.business.IOrderStatusBusiness;
import tb2014.domain.Broker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderAcceptAlacrity;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.OrderStatusType;

@RequestMapping("/apiOrder")
@Controller
public class OrderControllerApi {

	private IBrokerBusiness brokerBusiness;
	private IOrderBusiness orderBusiness;
	private IOrderAcceptAlacrityBusiness alacrityBuiness;
	private IOrderStatusBusiness orderStatusBusiness;

	@Autowired
	public OrderControllerApi(IOrderBusiness orderBusiness,
			IBrokerBusiness brokerBusiness,
			IOrderAcceptAlacrityBusiness alacrityBuiness,
			IOrderStatusBusiness orderStatusBusiness) {
		this.orderBusiness = orderBusiness;
		this.brokerBusiness = brokerBusiness;
		this.alacrityBuiness = alacrityBuiness;
		this.orderStatusBusiness = orderStatusBusiness;
	}

	@RequestMapping(value = "/alacrity", method = RequestMethod.GET)
	public void alacrity(@RequestParam("apiId") String apiId,
			@RequestParam("apiKey") String apiKey,
			@RequestParam("orderId") Long orderId, HttpServletResponse response) {

		Broker broker = brokerBusiness.getByApiId(apiId);
		Order order = orderBusiness.get(orderId);

		if(broker == null) {
			response.setStatus(403);
			return;
		}
		
		if (broker.getApiKey().trim().equals(apiKey.trim()) == false) {
			response.setStatus(403);
			return;
		}

		if (order == null) {
			response.setStatus(404);
			return;
		}

		OrderAcceptAlacrity alacrity = new OrderAcceptAlacrity();

		alacrity.setBroker(broker);
		alacrity.setOrder(order);
		alacrity.setDate(new Date());

		alacrityBuiness.save(alacrity);

		response.setStatus(200);
	}

	@RequestMapping(value = "/setStatus", method = RequestMethod.POST)
	public void setStatus(HttpServletRequest request,
			HttpServletResponse response) {

		Broker broker = brokerBusiness
				.getByApiId(request.getParameter("apiId"));

		if (broker == null) {
			response.setStatus(403);
			return;
		}

		if (broker.getApiKey().trim()
				.equals(request.getParameter("apiKey").trim()) == false) {
			response.setStatus(403);
			return;
		}

		Order order = orderBusiness.get(Long.parseLong(request
				.getParameter("orderId")));

		if (order == null) {
			response.setStatus(404);
			return;
		}

		if (order.getBroker() == null || order.getBroker().getId() != broker.getId()) {
			response.setStatus(403);
			return;
		}

		if (OrderStatusType.valueOf(request.getParameter("status")) == null) {
			response.setStatus(404);
			return;
		}

		OrderStatus status = new OrderStatus();

		status.setOrder(order);
		status.setCategory(request.getParameter("category"));
		status.setDate(new Date());
		status.setDirection(Integer.parseInt(request.getParameter("direction")));
		status.setSpeed(Integer.parseInt(request.getParameter("speed")));
		status.setLatitude(Double.parseDouble(request.getParameter("latitude")));
		status.setLongitude(Double.parseDouble(request
				.getParameter("longitude")));
		status.setStatus(OrderStatusType.valueOf(request.getParameter("status")));

		orderStatusBusiness.save(status);

		response.setStatus(200);
	}
}

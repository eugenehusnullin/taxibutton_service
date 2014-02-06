package tb2014.service.order;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import tb2014.Run;
import tb2014.business.IBrokerBusiness;
import tb2014.business.IOfferedOrderBrokerBusiness;
import tb2014.business.IOrderBusiness;
import tb2014.business.IOrderCancelBusiness;
import tb2014.business.IOrderStatusBusiness;
import tb2014.domain.Broker;
import tb2014.domain.order.OfferedOrderBroker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderCancel;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.OrderStatusType;
import tb2014.service.serialize.OrderSerializer;

@Service("OrdersProcessing")
public class OrderProcessing {

	private static final Logger log = LoggerFactory.getLogger(Run.class);

	private IOrderBusiness orderBusiness;
	private IBrokerBusiness brokerBusiness;
	private IOrderCancelBusiness orderCancelBusiness;
	private IOrderStatusBusiness orderStatusBusiness;
	private IOfferedOrderBrokerBusiness offeredOrderBrokerBusiness;

	@Autowired
	public OrderProcessing(IOrderBusiness orderBusiness, IBrokerBusiness brokerBusiness,
			IOrderCancelBusiness orderCancelBusiness, IOrderStatusBusiness orderStatusBusiness,
			IOfferedOrderBrokerBusiness offeredOrderBrokerBusiness) {
		this.orderBusiness = orderBusiness;
		this.brokerBusiness = brokerBusiness;
		this.orderCancelBusiness = orderCancelBusiness;
		this.orderStatusBusiness = orderStatusBusiness;
		this.offeredOrderBrokerBusiness = offeredOrderBrokerBusiness;
	}

	// offer order to all connected brokers (need to apply any rules to share
	// order between bounded set of brokers)
	public void offerOrder(Long orderId) {
		List<Broker> brokers = brokerBusiness.getAll();
		Order order = orderBusiness.getWithChilds(orderId);
		Document orderXml = OrderSerializer.OrderToXml(order);

		boolean offered = false;
		for (Broker currentBroker : brokers) {
			try {
				offerOrderHTTP(currentBroker, orderXml);

				offered = true;

				OfferedOrderBroker offeredOrderBroker = new OfferedOrderBroker();
				offeredOrderBroker.setOrder(order);
				offeredOrderBroker.setBroker(currentBroker);
				offeredOrderBroker.setTimestamp(new Date());
				offeredOrderBrokerBusiness.save(offeredOrderBroker);
			} catch (Exception ex) {
				log.error("Offer order to broker " + currentBroker.getId() + " error: " + ex.toString());
			}
		}

		if (offered) {
			// TODO: remove this order from need offer queue
		}
	}

	// offer order via HTTP protocol
	private void offerOrderHTTP(Broker broker, Document document) throws IOException,
			TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		// String url = broker.getApiurl() + "/offer";
		String url = "http://localhost:8080/tb2014/test/offer";
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

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
		}
	}

	// assign order executer
	public void giveOrder(Long orderId, Broker broker) {

		try {

			String url = "http://localhost:8080/tb2014/test/give";
			// String url = broker.getApiurl() + "/give";
			url += "?orderId=" + orderId.toString();
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("GET");

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				log.info("Error giving order to broker (code: " + responceCode + "): " + broker.getId().toString());
			} else {

				Order order = orderBusiness.get(orderId);

				order.setBroker(broker);
				orderBusiness.saveOrUpdate(order);
			}
		} catch (Exception ex) {
			log.info("Giving order for broker " + broker.getId() + " error: " + ex.toString());
		}
	}

	// cancelling order
	public void cancelOrder(Order order, String reason) {

		@SuppressWarnings("unused")
		Broker broker = order.getBroker();

		// String url = broker.getApiurl() + "/cancel";
		String url = "/tb2014/test/cancel";
		String params = "orderId=" + order.getId() + "&reason=" + reason;

		int resultCode = sendHttpGet(url, params);

		if (resultCode == 200) {// if broker has accepted cancelling order,
								// adding cancel row into the table & saving a
								// status of order as Cancelled
			OrderCancel orderCancel = new OrderCancel();
			orderCancel.setOrder(order);
			orderCancel.setReason(reason);

			orderCancelBusiness.save(orderCancel);

			OrderStatus orderStatus = new OrderStatus();

			orderStatus.setOrder(order);
			orderStatus.setStatus(OrderStatusType.valueOf("Cancelled"));
			orderStatus.setDate(new Date());

			orderStatusBusiness.save(orderStatus);
		}
	}

	// sending HTTP GET request
	private int sendHttpGet(String url, String params) {

		int responseCode = 0;

		try {
			URI uriObject = new URI("http", "localhost:8080", url, params, null);

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

	// deleting order with all childs
	public void deleteOrder(Long orderId) {

		Order order = orderBusiness.get(orderId);

		orderBusiness.delete(order);
	}
}

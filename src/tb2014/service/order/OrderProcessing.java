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
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import tb2014.dao.IBrokerDao;
import tb2014.dao.IOfferedOrderBrokerDao;
import tb2014.dao.IOrderAcceptAlacrityDao;
import tb2014.dao.IOrderCancelDao;
import tb2014.dao.IOrderDao;
import tb2014.dao.IOrderStatusDao;
import tb2014.domain.Broker;
import tb2014.domain.order.OfferedOrderBroker;
import tb2014.domain.order.Order;
import tb2014.domain.order.OrderCancelType;
import tb2014.domain.order.OrderStatus;
import tb2014.domain.order.OrderStatusType;
import tb2014.service.serialize.OrderSerializer;
import tb2014.utils.DatetimeUtil;

@Service("ordersProcessing")
public class OrderProcessing {

	private static final Logger log = LoggerFactory.getLogger(OrderProcessing.class);

	@Autowired
	private IOrderDao orderDao;
	@Autowired
	private IBrokerDao brokerDao;
	@Autowired
	private IOrderCancelDao orderCancelDao;
	@Autowired
	private IOrderStatusDao orderStatusDao;
	@Autowired
	private IOfferedOrderBrokerDao offeredOrderBrokerDao;
	@Autowired
	private IOrderAcceptAlacrityDao alacrityDao;

	// offer order to all connected brokers (need to apply any rules to share
	// order between bounded set of brokers)
	@Transactional
	public boolean offerOrder(Order order) {

		List<Broker> brokers = brokerDao.getAll();
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
	public Boolean offerOrderProcessing(Order order) {
		// check right status
		OrderStatus orderStatus = orderStatusDao.getLast(order);
		if (!OrderStatusType.IsValidForOffer(orderStatus.getStatus())) {
			return null;
		}

		// do offer
		return offerOrder(order);
	}
}

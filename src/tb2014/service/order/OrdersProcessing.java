package tb2014.service.order;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import tb2014.Run;
import tb2014.business.IBrokerBusiness;
import tb2014.business.IOrderBusiness;
import tb2014.domain.Broker;
import tb2014.domain.order.Order;
import tb2014.service.serialize.OrderSerializer;

public class OrdersProcessing {

	private static final Logger log = LoggerFactory.getLogger(Run.class);

	private IOrderBusiness orderBusiness;
	private IBrokerBusiness brokerBusiness;

	@Autowired
	public OrdersProcessing(IOrderBusiness orderBusiness,
			IBrokerBusiness brokerBusiness) {
		this.orderBusiness = orderBusiness;
		this.brokerBusiness = brokerBusiness;
	}

	// create client order object in database (returns a new order id)
	public Long CreateOrder(Order order) {

		try {

			orderBusiness.save(order);

			return order.getId();
		} catch (Exception ex) {

			log.info("Creating new order error: " + ex.toString());

			return null;
		}
	}

	// offer order to all connected brokers (need to apply any rules to share
	// order between bounded set of brokers)
	public void OfferOrder(Long orderId) {

		List<Broker> brokers = brokerBusiness.getAll();
		Order order = orderBusiness.getWithChilds(orderId);
		Document orderXml = OrderSerializer.OrderToXml(order);
		
		for (Broker currentBroker : brokers) {
			try {
				OfferOrderHTTP(currentBroker, orderXml);
			} catch (Exception ex) {
				log.info("Offer order to broker " + currentBroker.getId()
						+ " error: " + ex.toString());
			}
		}
	}

	private void OfferOrderHTTP(Broker broker, Document document) {

		try {

			String url = broker.getApiurl() + "/offer";
			//String url = "http://localhost:8080/tb2014/test/offer";
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj
					.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/xml");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());

			Source source = new DOMSource(document);
			Result result = new StreamResult(wr);

			TransformerFactory.newInstance().newTransformer()
					.transform(source, result);
			wr.flush();
			wr.close();

			int responceCode = connection.getResponseCode();

			if (responceCode != 200) {
				log.info("Error offering order to broker (code: " + responceCode + "): "
						+ broker.getId().toString());
			}
		} catch (Exception ex) {
			log.info("Offering order for broker " + broker.getId() + " error: "
					+ ex.toString());
		}
	}
}

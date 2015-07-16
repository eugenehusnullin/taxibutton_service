package tb.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb.dao.IOrderDao;
import tb.domain.order.Order;
import tb.service.processing.OfferOrderProcessing;

@Service
@EnableScheduling
public class ExactOrderToOffer {

	private static final Logger logger = LoggerFactory.getLogger(ExactOrderToOffer.class);
	@Autowired
	private IOrderDao orderDao;
	@Autowired
	private OfferOrderProcessing offerOrderProcessing;
	
	@Value("#{mainSettings['offerorder.exact.min']}")
	private int EXACT_MIN;

	@Transactional
	@Scheduled(cron = "0 */2 * * * *")
	// every two minutes
	public void toOffer() {
		logger.info("++ Start exact orders to offer.");

		try {
			List<Order> orders = orderDao.getExactOrdersNeedOffering(EXACT_MIN);
			for (Order order : orders) {
				try {
					OrderExecHolder orderExecHolder = new OrderExecHolder(order);
					offerOrderProcessing.addOrder(orderExecHolder);
					
					order.setExactOffered(true);
					// potential problem here
					orderDao.save(order);

					logger.info("Exact order offered id=" + order.getId().toString() + ".");
				} catch (Exception e) {
					logger.error("Exact order offering error: ", e);
				}
			}
			logger.info("== End exact orders to offer.");
		} catch (Exception e) {

		}
	}
}

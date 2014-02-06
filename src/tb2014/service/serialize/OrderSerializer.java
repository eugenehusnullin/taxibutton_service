package tb2014.service.serialize;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tb2014.Run;
import tb2014.domain.order.AddressPoint;
import tb2014.domain.order.Order;
import tb2014.domain.order.Requirement;

public class OrderSerializer {

	private static final Logger log = LoggerFactory.getLogger(Run.class);

	public static Document OrderToXml(Order order) {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element offer = doc.createElement("Offer");

			doc.appendChild(offer);

			Element orderId = doc.createElement("OrderId");

			orderId.appendChild(doc.createTextNode(Long.toString(order.getId())));
			offer.appendChild(orderId);

			Element recipient = doc.createElement("Recipient");

			recipient.setAttribute("blacklisted", "no");
			recipient.setAttribute("loyal", "yes");
			recipient.appendChild(doc.createTextNode(order.getPhone()));
			offer.appendChild(recipient);

			Element source = doc.createElement("Source");
			Element fullAddress = doc.createElement("FullAddress");

			fullAddress.appendChild(doc.createTextNode(order.getSource()
					.getFullAddress()));

			Element shortAddress = doc.createElement("ShortAddress");

			shortAddress.appendChild(doc.createTextNode(order.getSource()
					.getShortAddress()));

			Element closestStation = doc.createElement("ClosestStation");

			closestStation.appendChild(doc.createTextNode(order.getSource()
					.getClosesStation()));

			Element point = doc.createElement("Point");
			Element lon = doc.createElement("Lon");
			Element lat = doc.createElement("Lat");

			lon.appendChild(doc.createTextNode(Double.toString(order
					.getSource().getLon())));
			lat.appendChild(doc.createTextNode(Double.toString(order
					.getSource().getLat())));
			point.appendChild(lon);
			point.appendChild(lat);

			Element splittedAddress = doc.createElement("SplittedAddress");
			Element country = doc.createElement("Country");
			Element locality = doc.createElement("Locality");
			Element street = doc.createElement("Street");
			Element housing = doc.createElement("Housing");

			country.appendChild(doc.createTextNode(order.getSource()
					.getCounty()));
			locality.appendChild(doc.createTextNode(order.getSource()
					.getLocality()));
			street.appendChild(doc
					.createTextNode(order.getSource().getStreet()));
			housing.appendChild(doc.createTextNode(order.getSource()
					.getHousing()));

			splittedAddress.appendChild(country);
			splittedAddress.appendChild(locality);
			splittedAddress.appendChild(street);
			splittedAddress.appendChild(housing);

			source.appendChild(fullAddress);
			source.appendChild(shortAddress);
			source.appendChild(closestStation);
			source.appendChild(point);
			source.appendChild(splittedAddress);
			offer.appendChild(source);

			Element destinations = doc.createElement("Destinations");

			int i = 0;

			for (AddressPoint currentDestination : order.getDestinations()) {

				if (currentDestination.getIndexNumber() == 0) {
					continue;
				}

				i++;
				Element destination = doc.createElement("Destination");
				destination.setAttribute("order", Integer.toString(i));

				fullAddress = doc.createElement("FullAddress");

				fullAddress.appendChild(doc.createTextNode(currentDestination
						.getFullAddress()));

				shortAddress = doc.createElement("ShortAddress");

				shortAddress.appendChild(doc.createTextNode(currentDestination
						.getShortAddress()));

				closestStation = doc.createElement("ClosestStation");

				closestStation.appendChild(doc
						.createTextNode(currentDestination.getClosesStation()));

				point = doc.createElement("Point");
				lon = doc.createElement("Lon");
				lat = doc.createElement("Lat");

				lon.appendChild(doc.createTextNode(Double
						.toString(currentDestination.getLon())));
				lat.appendChild(doc.createTextNode(Double
						.toString(currentDestination.getLat())));
				point.appendChild(lon);
				point.appendChild(lat);

				splittedAddress = doc.createElement("SplittedAddress");
				country = doc.createElement("Country");
				locality = doc.createElement("Locality");
				street = doc.createElement("Street");
				housing = doc.createElement("Housing");

				country.appendChild(doc.createTextNode(order.getSource()
						.getCounty()));
				locality.appendChild(doc.createTextNode(order.getSource()
						.getLocality()));
				street.appendChild(doc.createTextNode(order.getSource()
						.getStreet()));
				housing.appendChild(doc.createTextNode(order.getSource()
						.getHousing()));

				splittedAddress.appendChild(country);
				splittedAddress.appendChild(locality);
				splittedAddress.appendChild(street);
				splittedAddress.appendChild(housing);

				destination.appendChild(fullAddress);
				destination.appendChild(shortAddress);
				destination.appendChild(closestStation);
				destination.appendChild(point);
				destination.appendChild(splittedAddress);

				destinations.appendChild(destination);
			}

			offer.appendChild(destinations);

			Element bookingTime = doc.createElement("BookingTime");

			bookingTime.setAttribute("type", order.getType());

			bookingTime
					.appendChild(doc.createTextNode((order.getSupplyDate() == null ? "-"
							: order.getSupplyDate().toString())
							+ "T"
							+ order.getSupplyHour()
							+ ":"
							+ order.getSupplyMin()));

			offer.appendChild(bookingTime);

			Element requirements = doc.createElement("Requirements");

			for (Requirement currentRequirement : order.getRequirements()) {

				Element requirement = doc.createElement("Requirement");

				requirement.setAttribute("name", currentRequirement.getType());

				if (currentRequirement.getOptions() != null) {
					if (currentRequirement.getOptions().trim() != "") {
						requirement
								.appendChild(doc
										.createTextNode(currentRequirement
												.getOptions()));
					}
				}

				requirements.appendChild(requirement);
			}

			offer.appendChild(requirements);

			return doc;
		} catch (Exception ex) {

			log.info("Creating XML document from order object exception: "
					+ ex.toString());
		}

		return null;
	}
}

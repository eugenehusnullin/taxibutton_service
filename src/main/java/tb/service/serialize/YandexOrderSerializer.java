package tb.service.serialize;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tb.Run;
import tb.car.domain.Car4Request;
import tb.domain.Tariff;
import tb.domain.order.AddressPoint;
import tb.domain.order.Order;
import tb.domain.order.Requirement;

public class YandexOrderSerializer {

	private static final Logger log = LoggerFactory.getLogger(Run.class);

	public static Document OrderToXml(Order order, List<Tariff> tariffs, List<Car4Request> cars, boolean notlater) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element requestElement = doc.createElement("Request");
			doc.appendChild(requestElement);

			Element orderIdElement = doc.createElement("Orderid");
			orderIdElement.appendChild(doc.createTextNode(order.getUuid()));
			requestElement.appendChild(orderIdElement);

			if (tariffs != null && tariffs.size() > 0) {
				Element tariffsElement = doc.createElement("Tariffs");
				requestElement.appendChild(tariffsElement);
				for (Tariff tariff : tariffs) {
					Element tariffElement = doc.createElement("Tariff");
					tariffElement.appendChild(doc.createTextNode(tariff.getTariffId()));
					tariffsElement.appendChild(tariffElement);
				}
			}

			Element carsElement = doc.createElement("Cars");
			requestElement.appendChild(carsElement);
			for (Car4Request car : cars) {
				Element carElement = doc.createElement("Car");
				carsElement.appendChild(carElement);

				Element uuidElement = doc.createElement("Uuid");
				uuidElement.appendChild(doc.createTextNode(car.getUuid()));
				carElement.appendChild(uuidElement);

				Element distElement = doc.createElement("Dist");
				distElement.appendChild(doc.createTextNode(Integer.toString(car.getDist())));
				carElement.appendChild(distElement);

				Element timeElement = doc.createElement("Time");
				timeElement.appendChild(doc.createTextNode(Integer.toString(car.getTime())));
				carElement.appendChild(timeElement);

				Element tariffElement = doc.createElement("Tariff");
				tariffElement.appendChild(doc.createTextNode(car.getTariff()));
				carElement.appendChild(tariffElement);

				Element maphrefElement = doc.createElement("MapHref");
				maphrefElement.appendChild(doc.createTextNode(""));
				carElement.appendChild(maphrefElement);
			}

			Element recipient = doc.createElement("Recipient");
			recipient.setAttribute("blacklisted", "no");
			recipient.setAttribute("loyal", "yes");
			requestElement.appendChild(recipient);

			Element source = doc.createElement("Source");
			requestElement.appendChild(source);
			insertAddressPoint(doc, source, order.getSource());

			// destinations
			Element destinations = doc.createElement("Destinations");
			requestElement.appendChild(destinations);
			int i = 0;
			for (AddressPoint currentDestination : order.getDestinations()) {
				if (currentDestination.getIndexNumber() == 0) {
					continue;
				}
				i++;

				Element destination = doc.createElement("Destination");
				destinations.appendChild(destination);
				destination.setAttribute("order", Integer.toString(i));
				insertAddressPoint(doc, destination, currentDestination);
			}

			Element bookingTime = doc.createElement("BookingTime");
			requestElement.appendChild(bookingTime);
			bookingTime.setAttribute("type", notlater ? "notlater" : "exact");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			bookingTime.appendChild(doc.createTextNode(df.format(order.getBookingDate())));

			if (order.getRequirements() != null && order.getRequirements().size() > 0) {
				Element requirements = doc.createElement("Requirements");
				requestElement.appendChild(requirements);
				for (Requirement requirement : order.getRequirements()) {
					Element requireElement = doc.createElement("Require");
					requireElement.setAttribute("name", defineRequireName(requirement.getType()));
					if (requirement.getOptions() != null) {
						if (requirement.getOptions().trim() != "") {
							requireElement.appendChild(doc.createTextNode(requirement.getOptions()));
						}
					}
					requirements.appendChild(requireElement);
				}
			}

			if (order.getDestinations() != null && order.getDestinations().size() > 0) {
				Element routeInfo = doc.createElement("RouteInfo");
				requestElement.appendChild(routeInfo);

				Element time = doc.createElement("Time");
				routeInfo.appendChild(time);
				time.setAttribute("unit", "second");
				time.appendChild(doc.createTextNode("0"));

				Element distance = doc.createElement("Distance");
				routeInfo.appendChild(distance);
				distance.setAttribute("unit", "meter");
				distance.appendChild(doc.createTextNode("0"));
			}

			return doc;
		} catch (Exception ex) {
			log.info("Creating XML document from order object exception: " + ex.toString());
			return null;
		}
	}

	private static String defineRequireName(String old) {
		String requireName = null;

		switch (old) {
		case "isConditioner":
			requireName = "has_conditioner";
			break;

		case "noSmoking":
			requireName = "no_smoking";
			break;

		case "isCheck":
			requireName = "check";
			break;

		case "isChildChair":
			requireName = "child_chair";
			break;

		case "isAnimalTransport":
			requireName = "animal_transport";
			break;

		case "isUniversal":
			requireName = "universal";
			break;

		case "isCoupon":
			requireName = "coupon";
			break;

		default:
			requireName = old;
			break;
		}

		return requireName;
	}

	private static void insertAddressPoint(Document doc, Element node, AddressPoint address) {
		Element fullName = doc.createElement("FullName");
		fullName.appendChild(doc.createTextNode(address.getFullAddress()));
		node.appendChild(fullName);

		Element shortName = doc.createElement("ShortName");
		shortName.appendChild(doc.createTextNode(address.getShortAddress()));
		node.appendChild(shortName);

		Element point = doc.createElement("Point");
		node.appendChild(point);
		Element lon = doc.createElement("Lon");
		lon.appendChild(doc.createTextNode(Double.toString(address.getLon())));
		point.appendChild(lon);
		Element lat = doc.createElement("Lat");
		lat.appendChild(doc.createTextNode(Double.toString(address.getLat())));
		point.appendChild(lat);

		Element country = doc.createElement("Country");
		node.appendChild(country);

		Element countryName = doc.createElement("CountryName");
		countryName.appendChild(doc.createTextNode(address.getCounty()));
		country.appendChild(countryName);

		Element locality = doc.createElement("Locality");
		country.appendChild(locality);

		Element localityName = doc.createElement("LocalityName");
		localityName.appendChild(doc.createTextNode(address.getLocality()));
		locality.appendChild(localityName);

		Element thoroughfare = doc.createElement("Thoroughfare");
		locality.appendChild(thoroughfare);

		Element thoroughfareName = doc.createElement("ThoroughfareName");
		thoroughfareName.appendChild(doc.createTextNode(address.getStreet()));
		thoroughfare.appendChild(thoroughfareName);

		Element premise = doc.createElement("Premise");
		thoroughfare.appendChild(premise);

		Element premiseNumber = doc.createElement("PremiseNumber");
		premiseNumber.appendChild(doc.createTextNode(address.getHousing()));
		premise.appendChild(premiseNumber);
	}
}

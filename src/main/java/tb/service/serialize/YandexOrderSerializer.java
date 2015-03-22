package tb.service.serialize;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tb.Run;
import tb.car.domain.Car4Request;
import tb.domain.order.AddressPoint;
import tb.domain.order.Order;
import tb.domain.order.Requirement;

public class YandexOrderSerializer {

	private static final Logger log = LoggerFactory.getLogger(Run.class);

	public static Document orderToSetcarXml(Order order, Car4Request car, String clientPhone) {
		try {
			Document doc = createDoc();
			Element requestElement = doc.createElement("Request");
			doc.appendChild(requestElement);
			requestElement.appendChild(createOrderId(doc, order.getUuid()));
			requestElement.appendChild(createSetCar(doc, car));
			requestElement.appendChild(createContactInfo(doc, clientPhone));
			requestElement.appendChild(createSource(doc, order.getSource()));
			requestElement.appendChild(createDestinations(doc, order.getDestinations()));
			requestElement.appendChild(createBooking(doc, order.getNotlater(), order.getBookingDate()));
			if (order.getRequirements() != null && order.getRequirements().size() > 0) {
				requestElement.appendChild(createRequirements(doc, order.getRequirements()));
			}

			return doc;
		} catch (Exception ex) {
			log.info("Creating XML document from order object exception: " + ex.toString());
			return null;
		}
	}

	public static Document orderToRequestXml(Order order, List<String> tariffs, List<Car4Request> cars) {
		try {
			Document doc = createDoc();
			Element requestElement = doc.createElement("Request");
			doc.appendChild(requestElement);
			requestElement.appendChild(createOrderId(doc, order.getUuid()));
			if (tariffs != null && tariffs.size() > 0) {
				requestElement.appendChild(createTariffs(doc, tariffs));
			}
			if (cars != null && cars.size() > 0) {
				requestElement.appendChild(createRequestCars(doc, cars));
			}
			requestElement.appendChild(createRecipient(doc));
			requestElement.appendChild(createSource(doc, order.getSource()));
			requestElement.appendChild(createDestinations(doc, order.getDestinations()));
			requestElement.appendChild(createBooking(doc, order.getNotlater(), order.getBookingDate()));
			if (order.getRequirements() != null && order.getRequirements().size() > 0) {
				requestElement.appendChild(createRequirements(doc, order.getRequirements()));
			}
			if (order.getDestinations() != null && order.getDestinations().size() > 0) {
				requestElement.appendChild(createRouteInfo(doc));
			}

			return doc;
		} catch (Exception ex) {
			log.info("Creating XML document from order object exception: " + ex.toString());
			return null;
		}
	}

	private static Element createRouteInfo(Document doc) {
		Element routeInfo = doc.createElement("RouteInfo");

		Element time = doc.createElement("Time");
		routeInfo.appendChild(time);
		time.setAttribute("unit", "second");
		time.appendChild(doc.createTextNode("0"));

		Element distance = doc.createElement("Distance");
		routeInfo.appendChild(distance);
		distance.setAttribute("unit", "meter");
		distance.appendChild(doc.createTextNode("0"));

		return routeInfo;
	}

	private static Element createRecipient(Document doc) {
		Element recipient = doc.createElement("Recipient");
		recipient.setAttribute("blacklisted", "no");
		recipient.setAttribute("loyal", "yes");
		return recipient;
	}

	private static Element createTariffs(Document doc, List<String> tariffs) {
		Element tariffsElement = doc.createElement("Tariffs");
		for (String tariff : tariffs) {
			Element tariffElement = doc.createElement("Tariff");
			tariffElement.appendChild(doc.createTextNode(tariff));
			tariffsElement.appendChild(tariffElement);
		}
		return tariffsElement;
	}

	private static Element createContactInfo(Document doc, String clientPhone) {
		Element contactInfo = doc.createElement("ContactInfo");

		Element phones = doc.createElement("Phones");
		contactInfo.appendChild(phones);

		Element phone4Driver = doc.createElement("Phone");
		phones.appendChild(phone4Driver);
		phone4Driver.appendChild(doc.createTextNode(clientPhone));
		phone4Driver.setAttribute("for", "driver");

		Element phone4Dispatch = doc.createElement("Phone");
		phones.appendChild(phone4Dispatch);
		phone4Dispatch.appendChild(doc.createTextNode(clientPhone));
		phone4Dispatch.setAttribute("for", "dispatch");

		return contactInfo;
	}

	private static Element createRequirements(Document doc, Set<Requirement> set) {
		Element requirements = doc.createElement("Requirements");
		for (Requirement requirement : set) {
			Element requireElement = doc.createElement("Require");
			requireElement.setAttribute("name", defineRequireName(requirement.getType()));
			if (requirement.getOptions() != null) {
				if (requirement.getOptions().trim() != "") {
					requireElement.appendChild(doc.createTextNode(requirement.getOptions()));
				}
			}
			requirements.appendChild(requireElement);
		}

		return requirements;
	}

	private static Element createBooking(Document doc, boolean notlater, Date date) {
		Element bookingTime = doc.createElement("BookingTime");
		bookingTime.setAttribute("type", notlater ? "notlater" : "exact");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		bookingTime.appendChild(doc.createTextNode(df.format(date)));
		return bookingTime;
	}

	private static Element createDestinations(Document doc, SortedSet<AddressPoint> list) {
		Element destinations = doc.createElement("Destinations");
		int i = 0;
		for (AddressPoint currentDestination : list) {
			if (currentDestination.getIndexNumber() == 0) {
				continue;
			}
			i++;

			Element destination = doc.createElement("Destination");
			destinations.appendChild(destination);
			destination.setAttribute("order", Integer.toString(i));
			insertAddressPoint(doc, destination, currentDestination);
		}
		return destinations;
	}

	private static Element createSource(Document doc, AddressPoint address) {
		Element source = doc.createElement("Source");
		insertAddressPoint(doc, source, address);
		return source;
	}

	private static Element createOrderId(Document doc, String orderId) {
		Element orderIdElement = doc.createElement("Orderid");
		orderIdElement.appendChild(doc.createTextNode(orderId));
		return orderIdElement;
	}

	private static Document createDoc() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder.newDocument();
	}

	private static Element createSetCar(Document doc, Car4Request car) {
		Element carsElement = doc.createElement("Cars");

		Element carElement = doc.createElement("Car");
		carsElement.appendChild(carElement);

		Element uuidElement = doc.createElement("Uuid");
		uuidElement.appendChild(doc.createTextNode(car.getUuid()));
		carElement.appendChild(uuidElement);

		Element tariffElement = doc.createElement("Tariff");
		tariffElement.appendChild(doc.createTextNode(car.getTariff()));
		carElement.appendChild(tariffElement);

		Element maphrefElement = doc.createElement("MapHref");
		maphrefElement.appendChild(doc.createTextNode(""));
		carElement.appendChild(maphrefElement);

		return carsElement;
	}

	private static Element createRequestCars(Document doc, List<Car4Request> cars) {
		Element carsElement = doc.createElement("Cars");
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

		return carsElement;
	}

	public static String defineRequireName(String old) {
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

package tb.service.serialize;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tb.dao.IBrokerDao;
import tb.domain.Broker;
import tb.domain.order.AddressPoint;
import tb.domain.order.Order;
import tb.domain.order.Requirement;
import tb.domain.order.VehicleClass;
import tb.service.exceptions.ParseOrderException;
import tb.utils.DatetimeUtils;

public class OrderJsonParser {

	private static boolean defineNotlater(Date bookingDateUtc, int notlaterMinutes) {
		Calendar bookingCalendar = DatetimeUtils.utcToLocal(bookingDateUtc);
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.add(Calendar.MINUTE, notlaterMinutes);

		return localCalendar.getTime().after(bookingCalendar.getTime());
	}

	public static Order Json2Order(JSONObject jsonObject, String phone, IBrokerDao brokerDao, int notlaterMinutes)
			throws ParseOrderException {

		Order order = new Order();

		String recipientPhone = phone != null ? phone : jsonObject.optString("recipientPhone");

		Date bookingDate = null;
		try {
			String bookingDateStr = jsonObject.getString("bookingDate");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			bookingDate = dateFormatter.parse(bookingDateStr);
		} catch (JSONException ex) {
			throw new ParseOrderException("bookingDate bad. " + ex.toString());
		} catch (ParseException ex) {
			throw new ParseOrderException("bookingDate format bad. " + ex.toString());
		}

		SortedSet<AddressPoint> addressPoints = new TreeSet<AddressPoint>();
		try {
			JSONObject sourceJson = jsonObject.getJSONObject("source");
			addressPoints.add(OrderJsonParser.getPoint(sourceJson, order, 0));
		} catch (JSONException ex) {
			throw new ParseOrderException("source bad. " + ex.toString());
		}

		JSONArray destinationsJson = jsonObject.optJSONArray("destinations");
		if (destinationsJson != null) {
			for (int i = 0; i < destinationsJson.length(); i++) {
				JSONObject destinationJson = destinationsJson.getJSONObject(i);
				int index = 0;

				try {
					index = destinationJson.getInt("index");
				} catch (JSONException ex) {
					throw new ParseOrderException("destination index bad. " + ex.toString());
				}

				addressPoints.add(OrderJsonParser.getPoint(destinationJson, order, index));
			}
		} else {
			boolean dest = jsonObject.optBoolean("destinations");
			if (!dest) {
				throw new ParseOrderException("destinations bad.");
			}
		}

		Set<Requirement> requirements = new HashSet<Requirement>();
		try {
			JSONArray requirementsJson = jsonObject.optJSONArray("requirements");

			if (requirementsJson != null) {
				for (int i = 0; i < requirementsJson.length(); i++) {
					JSONObject requirementJson = requirementsJson.getJSONObject(i);
					Requirement currentRequirement = new Requirement();

					try {
						String name = YandexOrderSerializer.defineRequireName(requirementJson.getString("name"));
						currentRequirement.setType(name);
						currentRequirement.setOptions(requirementJson.getString("value"));
					} catch (JSONException ex) {
						throw new ParseOrderException("requirement bad. " + ex.toString());
					}

					currentRequirement.setOrder(order);
					requirements.add(currentRequirement);
				}
			}
		} catch (JSONException ex) {
			throw new ParseOrderException("requirements bad. " + ex.toString());
		}

		try {
			int vehicleClass = jsonObject.getInt("vehicleClass");
			order.setOrderVehicleClass(VehicleClass.values()[vehicleClass]);
		} catch (JSONException ex) {
			throw new ParseOrderException("vehicleClass bad. " + ex.toString());
		}

		Set<Broker> offerBrokerList = new HashSet<Broker>();
		if (!jsonObject.isNull("brokers")) {
			JSONArray brokersUuids = jsonObject.getJSONArray("brokers");
			for (int i = 0; i < brokersUuids.length(); i++) {
				String currentBrokerUuid = brokersUuids.getString(i);
				Broker broker = brokerDao.get(currentBrokerUuid);
				offerBrokerList.add(broker);
			}
		}

		order.setComments(jsonObject.optString("comments", null));

		order.setNotlater(defineNotlater(bookingDate, notlaterMinutes));
		order.setPhone(recipientPhone);
		order.setBookingDate(bookingDate);
		order.setDestinations(addressPoints);
		order.setRequirements(requirements);
		order.setOfferBrokerList(offerBrokerList);

		return order;
	}

	private static AddressPoint getPoint(JSONObject pointJson, Order order, int index) {
		AddressPoint result = new AddressPoint();
		Double lon = null;
		Double lat = null;
		String fullAddress = null;
		String shortAddress = null;
		String closestStation = null;
		String country = null;
		String locality = null;
		String street = null;
		String housing = null;

		lon = pointJson.optDouble("lon", 0.0);
		lat = pointJson.optDouble("lat", 0.0);
		fullAddress = pointJson.optString("fullAddress", "");
		shortAddress = pointJson.optString("shortAddress", "");
		closestStation = pointJson.optString("closestStation", "");
		country = pointJson.optString("country", "");
		locality = pointJson.optString("locality", "");
		street = pointJson.optString("street", "");
		housing = pointJson.optString("housing", "");

		result.setLon(lon);
		result.setLat(lat);
		result.setFullAddress(fullAddress);
		result.setShortAddress(shortAddress);
		result.setClosesStation(closestStation);
		result.setCounty(country);
		result.setLocality(locality);
		result.setStreet(street);
		result.setHousing(housing);
		result.setOrder(order);
		result.setIndexNumber(index);

		return result;
	}
}

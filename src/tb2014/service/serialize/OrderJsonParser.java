package tb2014.service.serialize;

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

import tb2014.business.IBrokerBusiness;
import tb2014.domain.Broker;
import tb2014.domain.order.AddressPoint;
import tb2014.domain.order.Order;
import tb2014.domain.order.Requirement;
import tb2014.domain.order.VehicleClass;

public class OrderJsonParser {

	public static Order Json2Order(JSONObject jsonObject, IBrokerBusiness brokerBusiness) {

		Order order = new Order();

		Boolean urgent = null;
		String recipientPhone = null;
		Date bookingDate = null;
		int bookingHour = 0;
		int bookingMinute = 0;

		try {
			urgent = jsonObject.getBoolean("urgent");
			recipientPhone = jsonObject.getString("recipientPhone");
		} catch (JSONException ex) {
			return null;
		}

		try {

			String bookingDateStr = jsonObject.getString("bookingDate");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
			bookingDate = dateFormatter.parse(bookingDateStr);
		} catch (JSONException ex) {
			return null;
		} catch (ParseException ex) {
			return null;
		}

		try {
			bookingHour = jsonObject.getInt("bookingHour");
			bookingMinute = jsonObject.getInt("bookingMin");
		} catch (JSONException ex) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(bookingDate);
		cal.set(Calendar.HOUR_OF_DAY, bookingHour);
		cal.set(Calendar.MINUTE, bookingMinute);
		cal.set(Calendar.SECOND, 0);
		bookingDate = cal.getTime();

		SortedSet<AddressPoint> addressPoints = new TreeSet<AddressPoint>();
		AddressPoint source = new AddressPoint();
		JSONObject sourceJson = null;
		Double lon = null;
		Double lat = null;
		String fullAddress = null;
		String shortAddress = null;
		String closestStation = null;
		String country = null;
		String locality = null;
		String street = null;
		String housing = null;

		source.setIndexNumber(0);

		try {
			sourceJson = jsonObject.getJSONObject("source");
		} catch (JSONException ex) {
			return null;
		}

		try {
			lon = sourceJson.getDouble("lon");
			lat = sourceJson.getDouble("lat");
		} catch (JSONException ex) {
			return null;
		}

		try {

			fullAddress = sourceJson.getString("fullAddress");
			shortAddress = sourceJson.getString("shortAddress");
			closestStation = sourceJson.getString("closestStation");
			country = sourceJson.getString("country");
			locality = sourceJson.getString("locality");
			street = sourceJson.getString("street");
			housing = sourceJson.getString("housing");
		} catch (JSONException ex) {
			return null;
		}

		source.setLon(lon);
		source.setLat(lat);
		source.setFullAddress(fullAddress);
		source.setShortAddress(shortAddress);
		source.setClosesStation(closestStation);
		source.setCounty(country);
		source.setLocality(locality);
		source.setStreet(street);
		source.setHousing(housing);
		source.setOrder(order);

		addressPoints.add(source);

		JSONArray destinationsJson = null;

		try {
			destinationsJson = jsonObject.getJSONArray("destinations");
		} catch (JSONException ex) {
			return null;
		}

		for (int i = 0; i < destinationsJson.length(); i++) {

			JSONObject destinationJson = destinationsJson.getJSONObject(i);
			AddressPoint destination = new AddressPoint();
			lon = null;
			lat = null;
			int index = 0;

			try {

				lon = destinationJson.getDouble("lon");
				lat = destinationJson.getDouble("lat");
				index = destinationJson.getInt("index");
				fullAddress = destinationJson.getString("fullAddress");
				shortAddress = destinationJson.getString("shortAddress");
				closestStation = destinationJson.getString("closestStation");
				country = destinationJson.getString("country");
				locality = destinationJson.getString("locality");
				street = destinationJson.getString("street");
				housing = destinationJson.getString("housing");
			} catch (JSONException ex) {
				return null;
			}

			destination.setLon(lon);
			destination.setLat(lat);
			destination.setIndexNumber(index);
			destination.setFullAddress(fullAddress);
			destination.setShortAddress(shortAddress);
			destination.setClosesStation(closestStation);
			destination.setCounty(country);
			destination.setLocality(locality);
			destination.setStreet(street);
			destination.setHousing(housing);
			destination.setOrder(order);

			addressPoints.add(destination);
		}

		Set<Requirement> requirements = new HashSet<Requirement>();
		JSONArray requirementsJson = null;

		try {
			requirementsJson = jsonObject.getJSONArray("requirements");
		} catch (JSONException ex) {
			return null;
		}

		for (int i = 0; i < requirementsJson.length(); i++) {

			JSONObject requirementJson = requirementsJson.getJSONObject(i);
			Requirement currentRequirement = new Requirement();
			String name = null;
			String value = null;

			try {
				name = requirementJson.getString("name");
				value = requirementJson.getString("value");
			} catch (JSONException ex) {
				return null;
			}

			if (value.equals("yes") == false) {
				currentRequirement.setOptions(value);
			}

			currentRequirement.setType(name);
			currentRequirement.setOrder(order);
			requirements.add(currentRequirement);
		}

		try {

			int vehicleClass = jsonObject.getInt("vehicleClass");
			order.setOrderVehicleClass(VehicleClass.values()[vehicleClass]);
		} catch (JSONException ex) {
			return null;
		}

		Set<Broker> offerBrokerList = new HashSet<Broker>();

		if (!jsonObject.isNull("brokers")) {

			JSONArray brokersUuids = jsonObject.getJSONArray("brokers");

			for (int i = 0; i < brokersUuids.length(); i++) {

				String currentBrokerUuid = brokersUuids.getString(i);
				Broker broker = brokerBusiness.get(currentBrokerUuid);

				offerBrokerList.add(broker);
			}
		}

		order.setUrgent(urgent);
		order.setPhone(recipientPhone);
		order.setBookingDate(bookingDate);
		order.setDestinations(addressPoints);
		order.setRequirements(requirements);
		order.setOfferBrokerList(offerBrokerList);

		return order;
	}
}

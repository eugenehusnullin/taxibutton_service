package tb2014.service.serialize;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tb2014.business.IBrokerBusiness;
import tb2014.domain.Broker;
import tb2014.domain.order.AddressPoint;
import tb2014.domain.order.Requirement;
import tb2014.domain.order.Order;
import tb2014.domain.order.VehicleClass;

import org.json.JSONArray;
import org.json.JSONObject;

public class OrderJsonParser {

	public static Order Json2Order(JSONObject jsonObject, IBrokerBusiness brokerBusiness) {

		Order order = new Order();

		order.setUrgent(jsonObject.getBoolean("urgent"));

		if (!jsonObject.isNull("recipientPhone")) {
			order.setPhone(jsonObject.getString("recipientPhone"));
		} else {
			order.setPhone("");
		}

		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

		if (jsonObject.getString("bookingDate").isEmpty() == false) {

			String orderDate = jsonObject.getString("bookingDate");
			Date resultDate = null;

			try {
				resultDate = dateFormatter.parse(orderDate);
				order.setSupplyDate(resultDate);
			} catch (Exception ex) {
				System.out.println("Exception parsing order supply date: " + ex.toString());
			}
		}

		if (jsonObject.getString("bookingHour").isEmpty() == false) {
			order.setSupplyHour(Integer.parseInt(jsonObject.getString("bookingHour")));
		}

		if (jsonObject.getString("bookingMin").isEmpty() == false) {
			order.setSupplyMin(Integer.parseInt(jsonObject.getString("bookingMin")));
		}

		List<AddressPoint> addressPoints = new ArrayList<AddressPoint>();
		AddressPoint source = new AddressPoint();
		source.setIndexNumber(0);

		JSONObject sourceJson = jsonObject.getJSONObject("source");

		if (sourceJson.getString("lon").isEmpty() == false) {
			source.setLon(Double.parseDouble(sourceJson.getString("lon")));
		}

		if (sourceJson.getString("lat").isEmpty() == false) {
			source.setLat(Double.parseDouble(sourceJson.getString("lat")));
		}

		source.setFullAddress(sourceJson.getString("fullAddress"));
		source.setShortAddress(sourceJson.getString("shortAddress"));
		source.setClosesStation(sourceJson.getString("closestStation"));
		source.setCounty(sourceJson.getString("country"));
		source.setLocality(sourceJson.getString("locality"));
		source.setStreet(sourceJson.getString("street"));
		source.setHousing(sourceJson.getString("housing"));
		source.setOrder(order);

		addressPoints.add(source);

		JSONArray destinationsJson = jsonObject.getJSONArray("destinations");

		for (int i = 0; i < destinationsJson.length(); i++) {

			JSONObject destinationJson = destinationsJson.getJSONObject(i);
			AddressPoint destination = new AddressPoint();

			if (destinationJson.getString("lon").isEmpty() == false) {
				destination.setLon(Double.parseDouble(destinationJson.getString("lon")));
			}

			if (destinationJson.getString("lat").isEmpty() == false) {
				destination.setLat(Double.parseDouble(destinationJson.getString("lat")));
			}

			if (destinationJson.getString("index").isEmpty() == false) {
				destination.setIndexNumber(Integer.parseInt(destinationJson.getString("index")));
			}

			destination.setFullAddress(destinationJson.getString("fullAddress"));
			destination.setShortAddress(destinationJson.getString("shortAddress"));
			destination.setClosesStation(destinationJson.getString("closestStation"));
			destination.setCounty(destinationJson.getString("country"));
			destination.setLocality(destinationJson.getString("locality"));
			destination.setStreet(destinationJson.getString("street"));
			destination.setHousing(destinationJson.getString("housing"));
			destination.setOrder(order);

			addressPoints.add(destination);
		}

		order.setDestinations(addressPoints);

		Set<Requirement> requirements = new HashSet<Requirement>();

		JSONArray requirementsJson = jsonObject.getJSONArray("requirements");

		for (int i = 0; i < requirementsJson.length(); i++) {

			JSONObject requirementJson = requirementsJson.getJSONObject(i);
			Requirement currentRequirement = new Requirement();

			currentRequirement.setType(requirementJson.getString("name"));

			if (requirementJson.getString("value").equals("yes") == false) {
				currentRequirement.setOptions(requirementJson.getString("value"));
			}

			currentRequirement.setOrder(order);
			requirements.add(currentRequirement);
		}

		order.setRequirements(requirements);

		VehicleClass vehicleClass = VehicleClass.valueOf(jsonObject.getString("vehicleClass"));

		order.setOrderVehicleClass(vehicleClass);

		Set<Broker> offerBrokerList = new HashSet<Broker>();
		JSONArray brokersUuids = jsonObject.getJSONArray("brokers");

		for (int i = 0; i < brokersUuids.length(); i++) {

			String currentBrokerUuid = brokersUuids.getString(i);
			Broker broker = brokerBusiness.get(currentBrokerUuid);

			offerBrokerList.add(broker);
		}

		order.setOfferBrokerList(offerBrokerList);

		return order;
	}
}

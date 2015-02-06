package tb2014.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class SmsSelf {

	private Queue<JSONObject> queue = new LinkedList<>();

	public void send(String reciever, String msg) {
		JSONObject smsJson = new JSONObject();
		smsJson.put("sendText", msg);
		smsJson.put("sendNumber", reciever);
		smsJson.put("sendStatus", "1");
		smsJson.put("sendId", UUID.randomUUID().toString());

		synchronized (queue) {
			queue.add(smsJson);
			queue.notifyAll();
		}
	}

	public String get4Send() {
		JSONObject smsJson = null;
		synchronized (queue) {
			smsJson = queue.poll();
			queue.notifyAll();
		}

		JSONArray jsonArray = new JSONArray();
		if (smsJson != null) {
			jsonArray.put(smsJson);
		}

		return jsonArray.toString();
	}
}

package tb.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsCrm {
	@Value("#{mainSettings['smscrm.request']}")
	private String request;

	public void send(String reciever, String msg) {
		String body = createOutput(reciever, msg);

		try {
			URL obj = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			connection.setDoOutput(true);
			IOUtils.write(body.getBytes("UTF-8"), connection.getOutputStream());
			IOUtils.closeQuietly(connection.getOutputStream());

			int responceCode = connection.getResponseCode();
			if (responceCode != 200) {
				;
			} else {
				IOUtils.toString(connection.getInputStream());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String createOutput(String reciever, String msg) {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("sendNumber", reciever);
		jsonMsg.put("sendText", msg);

		JSONArray array = new JSONArray();
		array.put(jsonMsg);

		return array.toString();
	}
}
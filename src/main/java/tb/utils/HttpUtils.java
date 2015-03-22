package tb.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

public class HttpUtils {

	public static boolean postDocumentOverHttp(Document document, String url) throws IOException,
			TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setDoOutput(true);

		String s = XmlUtils.nodeToString(document.getFirstChild());
		IOUtils.write(s, connection.getOutputStream(), "UTF-8");
		connection.getOutputStream().flush();
		connection.getOutputStream().close();
		return connection.getResponseCode() == 200;
	}

	public static String getApplicationUrl(HttpServletRequest request) throws URISyntaxException {
		String url = request.getRequestURL().toString();
		int index = url.indexOf("/admin");
		return url.substring(0, index);
	}

	public static int sendHttpGet(String url, String params) {

		String protocol = url.split(":")[0];
		String[] fullAddress = url.split("//")[1].split("/", 2);
		String address = fullAddress[0];
		String path = "/" + fullAddress[1];

		int responseCode = 0;

		try {
			URI uriObject = new URI(protocol, address, path, params, null);

			URL obj = uriObject.toURL();
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("GET");

			responseCode = connection.getResponseCode();
		} catch (Exception ex) {

			System.out.println("Sending HTTP GET to: " + url + " FAILED, error: " + ex.toString());
			responseCode = -1;
		}

		return responseCode;
	}

}
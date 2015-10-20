package tb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;

public class HttpUtils {

	public static HttpURLConnection postDocumentOverHttp(Document document, String url, Logger logger)
			throws IOException,
			TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		return postDocumentOverHttp(document, url, "application/xml", logger);
	}
	
	public static HttpURLConnection postDocumentOverHttp(Document document, String url, String contentType, Logger logger)
			throws IOException,
			TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		String raw = XmlUtils.nodeToString(document.getFirstChild());
		logger.info(raw);
		return postRawData(raw, url, "UTF-8", contentType);
	}

	public static HttpURLConnection postRawData(String raw, String url, String encoding) throws IOException {
		return postRawData(raw, url, encoding, "application/xml");
	}
	
	public static HttpURLConnection postRawData(String raw, String url, String encoding, String contentType) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", contentType);
		//connection.setRequestProperty("charset", encoding);
		//connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setDoOutput(true);

		IOUtils.write(raw, connection.getOutputStream(), encoding);
		connection.getOutputStream().flush();
		connection.getOutputStream().close();
		return connection;
	}

	public static String getApplicationUrl(HttpServletRequest request, String cutFrom) throws URISyntaxException {
		String url = request.getRequestURL().toString();
		int index = url.indexOf(cutFrom);
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

	public static InputStream makeGetRequest(String address, String contentType) throws IOException {
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", contentType);

		if (conn.getResponseCode() == 200) {
			return conn.getInputStream();
		}
		return null;
	}
}

package tb.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
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

}

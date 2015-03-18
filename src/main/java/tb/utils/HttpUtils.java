package tb.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class HttpUtils {

	public static boolean postDocumentOverHttp(Document document, String url) throws IOException,
			TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		URL obj = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/xml");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setReadTimeout(0);

		connection.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

		Source source = new DOMSource(document);
		Result result = new StreamResult(wr);

		TransformerFactory.newInstance().newTransformer().transform(source, result);
		wr.flush();
		wr.close();

		return connection.getResponseCode() == 200;
	}
	
	public static String getApplicationUrl(String uriString) throws URISyntaxException {
		int index = uriString.indexOf("/admin");
		return uriString.substring(0, index);
	}

}

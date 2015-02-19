package tb.utils;

import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class ConverterUtil {
	
	public static String xmlToString(Document document) {

		String xmlString = null;

		try {

			Source source = new DOMSource(document);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);

			TransformerFactory trFactory = TransformerFactory.newInstance();
			Transformer transformer = trFactory.newTransformer();
			transformer.transform(source, result);

			xmlString = stringWriter.getBuffer().toString();
		} catch (Exception ex) {

			return null;
		}

		return xmlString;
	}
}

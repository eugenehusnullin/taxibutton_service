package tb2014.mvc.controllers.order;

import java.io.DataInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/test")
@Controller
public class OrderControllerTest {
	
	@RequestMapping(value = "/offer", method = RequestMethod.POST)
	public void offer(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		DataInputStream inputStream = new DataInputStream(
				request.getInputStream());

		try {
			Source source = new StreamSource(inputStream);
			Result outputTarget = new StreamResult(System.out);

			TransformerFactory.newInstance().newTransformer()
					.transform(source, outputTarget);
		} catch (Exception ex) {
			System.out.println("Error recieving XML document: " + ex.toString());
		}

		response.setContentType("text/html");
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
	}
	
	@RequestMapping(value = "/give", method = RequestMethod.GET)
	public void give(@RequestParam("orderId") Long orderId, HttpServletResponse response) {
		
		Long result = orderId;
		
		response.setStatus(200);
	}
}

package tb.brokerside;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/1.x/requestcar")
public class RequestCarController {
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public void index(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("/1.x/requestcar");
		((List<String>) IOUtils.readLines(request.getInputStream())).forEach(System.out::println);
		response.setStatus(200);
	}
}

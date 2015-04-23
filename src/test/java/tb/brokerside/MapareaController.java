package tb.brokerside;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/maparea")
@Controller("brokerSideMapareaController")
public class MapareaController {

	@RequestMapping(value = "", method = RequestMethod.GET)
	public void index(HttpServletRequest request, HttpServletResponse response) throws IOException {
		URL url = getClass().getResource("/Mapareas.json");
		File file = new File(url.getFile());
		FileInputStream fis = new FileInputStream(file);
		IOUtils.copy(fis, response.getOutputStream());
		fis.close();
		response.getOutputStream().flush();
		response.getOutputStream().close();
		response.setStatus(200);
	}
}

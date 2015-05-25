package tb.apitaxirf;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("apitaxirfTariffController")
@RequestMapping("/tariff")
public class TariffController {
	private static final Logger logger = LoggerFactory.getLogger(TariffController.class);

	@RequestMapping(value = "")
	public void index(HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONArray jsonArray = new JSONArray();

			URL url = getClass().getResource("/tb/apitaxirf/tariffs");
			Path tariffUrl = Paths.get(url.toURI());
			Files.walk(tariffUrl).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					try {
						BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
						String content = IOUtils.toString(reader);

						JSONObject jsonTariff = null;
						try {
							jsonTariff = new JSONObject(content);
						} catch (Exception e) {
							content = content.substring(1);
							jsonTariff = new JSONObject(content);
						}
						jsonArray.put(jsonTariff);
					} catch (Exception e) {
						logger.error("apitaxirfTariffController Files.walk(...).", e);
					}
				}
			});

			IOUtils.write(jsonArray.toString(), response.getOutputStream(), "UTF-8");
		} catch (IOException | URISyntaxException e) {
			logger.error("apitaxirfTariffController.", e);
		}
	}
}

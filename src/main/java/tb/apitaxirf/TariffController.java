package tb.apitaxirf;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import tb.dao.ITariffDefinitionDao;
import tb.domain.TariffDefinition;

@Controller("apitaxirfTariffController")
@RequestMapping("/tariff")
public class TariffController {
	private static final Logger logger = LoggerFactory.getLogger(TariffController.class);

	@Autowired
	private ITariffDefinitionDao tariffDefinitionDao;

	@RequestMapping(value = "")
	public void index(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("Trying load Tariff Definitions of taxirf");

			String raw = loadFromDb();
			IOUtils.write(raw, response.getOutputStream(), "UTF-8");
		} catch (IOException e) {
			logger.error("apitaxirfTariffController.", e);
		}
	}

	@SuppressWarnings("unused")
	private String loadFromFileresource() throws URISyntaxException, IOException {
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

		return jsonArray.toString();
	}

	private String loadFromDb() {
		JSONArray jsonArray = new JSONArray();
		List<TariffDefinition> list = tariffDefinitionDao.getAll();
		for (TariffDefinition tariffDefinition : list) {
			String body = tariffDefinition.getBody();
			JSONObject jsonTariff = null;
			try {
				jsonTariff = new JSONObject(body);
			} catch (Exception e) {
				body = body.substring(1);
				jsonTariff = new JSONObject(body);
			}
			jsonArray.put(jsonTariff);
		}

		return jsonArray.toString();
	}
}

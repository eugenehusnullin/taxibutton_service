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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import tb.dao.ITariffDefinitionMapAreaDao;
import tb.domain.TariffDefinitionMapArea;

@Controller("apitaxirfMapAreaController")
@RequestMapping("/maparea")
public class MapAreaController {
	private static final Logger logger = LoggerFactory.getLogger(MapAreaController.class);

	@Autowired
	private ITariffDefinitionMapAreaDao tariffDefinitionMapAreaDao;

	@RequestMapping(value = "")
	public void index(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("Trying load Tariff Definition Map Areas of taxirf");

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			String raw = loadFromDb();
			IOUtils.write(raw, response.getOutputStream(), "UTF-8");
		} catch (IOException e) {
			logger.error("apitaxirfMapAreaController.", e);
		}
	}

	@SuppressWarnings("unused")
	private String loadFromFileresource() throws URISyntaxException, IOException {
		JSONArray jsonArray = new JSONArray();

		URL url = getClass().getResource("/tb/apitaxirf/mapareas");
		Path mapAreasUrl = Paths.get(url.toURI());
		Files.walk(mapAreasUrl).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				try {
					BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
					String content = IOUtils.toString(reader);

					JSONObject jsonMapArea = null;
					try {
						jsonMapArea = new JSONObject(content);
					} catch (Exception e) {
						content = content.substring(1);
						jsonMapArea = new JSONObject(content);
					}

					JSONObject jsonHolder = new JSONObject();
					jsonHolder.put("name", FilenameUtils.removeExtension(filePath.getFileName().toString()));
					jsonHolder.put("body", jsonMapArea);

					jsonArray.put(jsonHolder);
				} catch (Exception e) {
					logger.error("apitaxirfMapAreaController Files.walk(...).", e);
				}
			}
		});

		return jsonArray.toString();
	}

	private String loadFromDb() {
		JSONArray jsonArray = new JSONArray();

		List<TariffDefinitionMapArea> list = tariffDefinitionMapAreaDao.getAll();
		for (TariffDefinitionMapArea item : list) {
			String body = item.getBody();
			JSONObject jsonMapArea = null;
			try {
				jsonMapArea = new JSONObject(body);
			} catch (Exception e) {
				body = body.substring(1);
				jsonMapArea = new JSONObject(body);
			}

			JSONObject jsonHolder = new JSONObject();
			jsonHolder.put("name", item.getName());
			jsonHolder.put("body", jsonMapArea);

			jsonArray.put(jsonHolder);
		}
		return jsonArray.toString();
	}
}

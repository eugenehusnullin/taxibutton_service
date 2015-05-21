package tb.apidevice;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb.dao.IMapAreaDao;
import tb.domain.Broker;
import tb.domain.maparea.MapArea;
import tb.maparea.MapareaSerializer;
import tb.service.BrokerService;
import utils.NetStreamUtils;

@RequestMapping("/maparea")
@Controller("apiDeviceMapareaController")
public class MapareaController {
	@Autowired
	private BrokerService brokerService;
	@Autowired
	private IMapAreaDao mapareaDao;
	@Autowired
	private MapareaSerializer mapareaSerializer;

	@RequestMapping(value = "/getmapareas", method = RequestMethod.POST)
	@Transactional
	public void getMapareas(HttpServletRequest request, HttpServletResponse response) {
		try {
			StringBuffer stringBuffer = NetStreamUtils.getHttpServletRequestBuffer(request);
			JSONObject requestJson = (JSONObject) new JSONTokener(stringBuffer.toString()).nextValue();

			String uuid = requestJson.optString("uuid");
			if (uuid != null) {
				Broker broker = brokerService.getByUuid(uuid);
				if (broker != null) {
					Set<MapArea> mapAreas = broker.getMapAreas();
					JSONObject result = mapareaSerializer.serialize(mapAreas);
					IOUtils.write(result.toString(), response.getOutputStream(), "UTF-8");
				}
			}
		} catch (Exception e) {

		}
	}
}

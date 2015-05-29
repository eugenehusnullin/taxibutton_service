package tb.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tb.domain.maparea.Point;
import tb.domain.maparea.Polygon;
import tb.service.BrokerService;
import tb.service.MapAreaAssist;

@RequestMapping("/maparea")
@Controller("devMapArea")
public class MapAreaController {

	@Autowired
	private BrokerService brokerService;
	@Autowired
	private MapAreaAssist mapAreaAssist;

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(Model model) {
		return "maparea/create";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String create(HttpServletRequest request) {

		String name = request.getParameter("name");
		if (name != null && !name.isEmpty()) {
			List<Point> points = new ArrayList<>();
			int i = 0;
			while (true) {
				request.getParameterValues("name");
				String[] coord = request.getParameterValues("points[" + i + "][]");
				if (coord == null) {
					break;
				}

				Point point = new Point();
				point.setLatitude(Double.parseDouble(coord[0]));
				point.setLongitude(Double.parseDouble(coord[1]));
				points.add(point);
				i++;
			}

			if (points.size() > 2) {
				Polygon polygon = new Polygon();
				polygon.setName(name);
				polygon.setPoints(points);

				brokerService.addMapArea(polygon);
				mapAreaAssist.updateMapArea(polygon);
			}

		}

		return "maparea/create";
	}
}

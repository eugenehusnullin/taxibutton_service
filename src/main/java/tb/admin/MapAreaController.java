package tb.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb.admin.model.MapAreaModel;
import tb.dao.IMapAreaDao;
import tb.domain.maparea.MapArea;
import tb.domain.maparea.Point;
import tb.domain.maparea.Polygon;
import tb.service.MapAreaAssist;

@RequestMapping("/maparea")
@Controller("devMapArea")
public class MapAreaController {

	@Autowired
	private MapAreaAssist mapAreaAssist;
	@Autowired
	private IMapAreaDao mapAreaDao;

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model) {
		return "maparea/add";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(HttpServletRequest request) {
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

				mapAreaDao.add(polygon);
				mapAreaAssist.updateMapArea(polygon);
			}
		}
		return "redirect:list";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(@RequestParam("id") Long id, Model model) {
		MapAreaModel mapAreaModel = new MapAreaModel();
		MapArea mapArea = mapAreaDao.get(id);
		mapAreaModel.setId(id);
		mapAreaModel.setName(mapArea.getName());
		if (mapArea instanceof Polygon) {
			mapAreaModel.setPoints(((Polygon) mapArea).getPoints());
		}

		model.addAttribute("maparea", mapAreaModel);

		return "maparea/edit";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		List<MapArea> list = mapAreaDao.getAll();
		model.addAttribute("mapareas", list);

		return "maparea/list";
	}

	@RequestMapping(value = "/del", method = RequestMethod.GET)
	public String del(@RequestParam("id") Long id) {
		mapAreaDao.delete(id);
		return "redirect:list";
	}
}

package tb.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb.admin.model.MapAreaModel;
import tb.admin.model.TariffDefinitionModel;
import tb.dao.IMapAreaDao;
import tb.dao.ITariffDefinitionDao;
import tb.domain.TariffDefinition;
import tb.domain.maparea.MapArea;
import tb.domain.order.VehicleClass;

@RequestMapping("/tariffdef")
@Controller("adminTariffDefinitionController")
public class TariffDefinitionController {

	@Autowired
	private IMapAreaDao mapAreaDao;
	@Autowired
	private ITariffDefinitionDao tariffDefinitionDao;
	//@Value("#{'${my.list.of.strings}'.split(',')}")
	@Value("#{mainSettings['costservice.routingservicenames'].split(',')}")
	private List<String> routingServiceNames;

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model) {
		List<String> vcl = new ArrayList<String>();
		for (VehicleClass vc : VehicleClass.values()) {
			vcl.add(vc.name());
		}
		model.addAttribute("vcl", vcl);

		List<MapAreaModel> mapAreaModels = new ArrayList<MapAreaModel>();
		List<MapArea> mapAreas = mapAreaDao.getAll();
		for (MapArea mapArea : mapAreas) {
			MapAreaModel mapAreaModel = new MapAreaModel();
			mapAreaModel.setId(mapArea.getId());
			mapAreaModel.setName(mapArea.getName());
			mapAreaModels.add(mapAreaModel);
		}
		model.addAttribute("mapareas", mapAreaModels);
		model.addAttribute("routingservicenames", routingServiceNames);

		return "tariffdef/add";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(HttpServletRequest request) {
		String idname = request.getParameter("idname");
		String routingServiceName = request.getParameter("routingservicename");
		String body = request.getParameter("body");
		VehicleClass vc = VehicleClass.valueOf(request.getParameter("vc"));

		Set<MapArea> mapAreasSet = new HashSet<MapArea>();
		String[] mapAreaIds = request.getParameterValues("mapareas");
		for (String mapAreaId : mapAreaIds) {
			MapArea mapArea = new MapArea();
			mapArea.setId(Long.parseLong(mapAreaId));
			mapAreasSet.add(mapArea);
		}

		TariffDefinition tariffDefinition = new TariffDefinition();
		tariffDefinition.setIdName(idname);
		tariffDefinition.setRoutingServiceName(routingServiceName);
		tariffDefinition.setBody(body);
		tariffDefinition.setVehicleClass(vc);
		tariffDefinition.setMapAreas(mapAreasSet);

		tariffDefinitionDao.add(tariffDefinition);

		return "redirect:list";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(@RequestParam("idname") String idname, Model model) {
		TariffDefinition tariffDefinition = tariffDefinitionDao.get(idname);
		model.addAttribute("idname", idname);
		model.addAttribute("routingservicenames", routingServiceNames);
		model.addAttribute("currentroutingservicename", tariffDefinition.getRoutingServiceName());

		List<String> vcl = new ArrayList<String>();
		for (VehicleClass vc : VehicleClass.values()) {
			vcl.add(vc.name());
		}
		model.addAttribute("vcl", vcl);
		model.addAttribute("currentvc", tariffDefinition.getVehicleClass().name());

		List<MapAreaModel> mapAreaModels = new ArrayList<MapAreaModel>();
		List<MapArea> mapAreas = mapAreaDao.getAll();
		for (MapArea mapArea : mapAreas) {
			MapAreaModel mapAreaModel = new MapAreaModel();
			mapAreaModel.setId(mapArea.getId());
			mapAreaModel.setName(mapArea.getName());
			mapAreaModels.add(mapAreaModel);
		}
		model.addAttribute("mapareas", mapAreaModels);
		
		List<Long> mapAreasIds = new ArrayList<Long>();
		tariffDefinition.getMapAreas().stream().forEach(a -> mapAreasIds.add(a.getId()));
		model.addAttribute("mapareasids", mapAreasIds);

		model.addAttribute("body", tariffDefinition.getBody());

		return "tariffdef/edit";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String edit(HttpServletRequest request) {
		String oldIdname = request.getParameter("oldidname");
		TariffDefinition tariffDefinition = tariffDefinitionDao.get(oldIdname);
		
		String idname = request.getParameter("idname");
		String routingServiceName = request.getParameter("routingservicename");
		String body = request.getParameter("body");
		VehicleClass vc = VehicleClass.valueOf(request.getParameter("vc"));

		Set<MapArea> mapAreasSet = new HashSet<MapArea>();
		String[] mapAreaIds = request.getParameterValues("mapareas");
		for (String mapAreaId : mapAreaIds) {
			MapArea mapArea = new MapArea();
			mapArea.setId(Long.parseLong(mapAreaId));
			mapAreasSet.add(mapArea);
		}

		tariffDefinition.setIdName(idname);
		tariffDefinition.setRoutingServiceName(routingServiceName);
		tariffDefinition.setBody(body);
		tariffDefinition.setVehicleClass(vc);
		tariffDefinition.setMapAreas(mapAreasSet);

		tariffDefinitionDao.update(tariffDefinition);

		return "redirect:list";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		List<TariffDefinitionModel> tariffDefinitionModelList = new ArrayList<TariffDefinitionModel>();
		List<TariffDefinition> tariffDefinitionList = tariffDefinitionDao.getAll();
		for (TariffDefinition tariffDefinition : tariffDefinitionList) {
			TariffDefinitionModel tariffDefinitionModel = new TariffDefinitionModel();
			tariffDefinitionModel.setIdName(tariffDefinition.getIdName());
			tariffDefinitionModel.setVehicleClass(tariffDefinition.getVehicleClass().name());
			tariffDefinitionModel.setRoutingServiceName(tariffDefinition.getRoutingServiceName());

			String mapAreasnames = tariffDefinition.getMapAreas()
					.stream()
					.map(MapArea::getName)
					.reduce("", (a, b) -> (a == "" ? a : a + ", ") + b);
			tariffDefinitionModel.setMapAreasNames(mapAreasnames);
			tariffDefinitionModelList.add(tariffDefinitionModel);
		}

		model.addAttribute("tariffdefs", tariffDefinitionModelList);
		return "tariffdef/list";
	}

	@RequestMapping(value = "/del", method = RequestMethod.GET)
	public String delete(@RequestParam("idname") String idname) {
		tariffDefinitionDao.delete(idname);

		return "redirect:list";
	}
}

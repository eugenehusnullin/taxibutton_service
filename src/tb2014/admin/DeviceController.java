package tb2014.admin;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tb2014.business.IDeviceBusiness;
import tb2014.domain.Device;

@RequestMapping("/device")
@Controller
public class DeviceController {

	private IDeviceBusiness deviceBusiness;

	@Autowired
	public DeviceController(IDeviceBusiness deviceBusiness) {
		this.deviceBusiness = deviceBusiness;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {

		model.addAttribute("devices", deviceBusiness.getAll());
		return "device/list";
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create() {
		return "device/create";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String create(@RequestParam("apiKey") String apiKey) {

		// move creating device code into device processing (if need)
		Device device = new Device();
		String resultUuid = UUID.randomUUID().toString();

		device.setApiId(resultUuid);
		device.setApiKey(apiKey);

		deviceBusiness.save(device);

		return "redirect:list";
	}
}

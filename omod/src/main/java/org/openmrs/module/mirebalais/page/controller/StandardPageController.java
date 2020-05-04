package org.openmrs.module.mirebalais.page.controller;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

public class StandardPageController {

	public String post(@RequestParam(value = "locationId", required = false) Integer locationId,
	        @SpringBean("locationService") LocationService locationService, UiSessionContext context) {
		Location location = locationService.getLocation(locationId);
		context.setSessionLocation(location);
		return null;
	}

}

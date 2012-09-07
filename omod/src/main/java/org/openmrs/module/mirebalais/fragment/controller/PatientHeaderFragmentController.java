/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mirebalais.fragment.controller;

import org.openmrs.Patient;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;


/**
 *
 */
public class PatientHeaderFragmentController {
	
	public void controller(FragmentModel model,
	                       UiUtils ui,
	                       @FragmentParam("patient") Patient patient) {
		model.addAttribute("personJson", ui.toJson(getPatient(patient, ui)));
	}
	
	public SimpleObject getPatient(Patient patient, UiUtils ui) {
		// TODO move this definition to a standardized utility method
		SimpleObject ret = SimpleObject.fromObject(patient, ui, "patientId", "gender");
		ret.put("preferredName", SimpleObject.fromObject(patient.getPersonName(), ui, "givenName", "familyName"));
		return ret;
	}
	
}

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
package org.openmrs.module.mirebalais.api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.api.db.MirebalaisHospitalDAO;

/**
 * Default implementation of {@link MirebalaisHospitalService}.
 */
public class MirebalaisHospitalServiceImpl extends BaseOpenmrsService implements MirebalaisHospitalService {
	
    protected final Log log = LogFactory.getLog(this.getClass());
	
	private MirebalaisHospitalDAO dao;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(MirebalaisHospitalDAO dao) {
	    this.dao = dao;
    }
    
    /**
     * @return the dao
     */
    public MirebalaisHospitalDAO getDao() {
	    return dao;
    }
    
    /**
     * @see org.openmrs.module.mirebalais.api.MirebalaisHospitalService#getRadiologyOrderables()
     */
    @Override
    public List<Concept> getRadiologyOrderables() {
    	List<Concept> orderables = getGlobalPropertyConceptList(MirebalaisConstants.RADIOLOGY_ORDERABLE_CONCEPTS_GP);
    	return orderables;
    }

	/**
     * Gets the list of concepts specified in a global property
     * 
     * @param propertyName
     * @return
     */
    private List<Concept> getGlobalPropertyConceptList(String propertyName) {
    	// TODO either use HFE util methods, or CustomDatatype
    	String gp = Context.getAdministrationService().getGlobalProperty(propertyName);
    	if (gp == null) {
    		throw new RuntimeException("Module not yet configured");
    	}
	    List<Concept> ret = new ArrayList<Concept>();
	    for (String conceptUuid : gp.split(",")) {
	    	ret.add(Context.getConceptService().getConceptByUuid(conceptUuid));
	    }
	    return ret;
    }
    
    /**
     * @see org.openmrs.module.mirebalais.api.MirebalaisHospitalService#placeRadiologyOrder(org.openmrs.Patient, org.openmrs.Concept)
     */
    @SuppressWarnings("deprecation")
    @Override
    public Order placeRadiologyOrder(Patient p, Concept orderable) {
        Order order = new Order();
        order.setPatient(p);
        order.setConcept(orderable);
        order.setOrderType(getRadiologyOrderType());
        order.setStartDate(new Date());
        order.setOrderer(Context.getAuthenticatedUser());
        return Context.getOrderService().saveOrder(order);
    }

	/**
     * @return the type we use for radiology orders
     */
    @SuppressWarnings("deprecation")
    private OrderType getRadiologyOrderType() {
    	return getGlobalPropertyOrderType(MirebalaisConstants.RADIOLOGY_ORDERTYPE_GP);
    }

	/**
     * @param propertyName
     * @return the order type configured by that GP
     */
    @SuppressWarnings("deprecation")
    private OrderType getGlobalPropertyOrderType(String propertyName) {
    	// TODO either use HFE util methods, or CustomDatatype
    	String gp = Context.getAdministrationService().getGlobalProperty(propertyName);
    	if (gp == null) {
    		throw new RuntimeException("Module not yet configured");
    	}
    	return Context.getOrderService().getOrderTypeByUuid(gp);
    }
    
}
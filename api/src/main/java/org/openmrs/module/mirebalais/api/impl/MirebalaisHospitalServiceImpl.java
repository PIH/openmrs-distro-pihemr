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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.api.db.MirebalaisHospitalDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Default implementation of {@link MirebalaisHospitalService}.
 */
public class MirebalaisHospitalServiceImpl extends BaseOpenmrsService implements MirebalaisHospitalService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private MirebalaisHospitalDAO dao;
	
	/**
	 * @param dao the db to set
	 */
	public void setDao(MirebalaisHospitalDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the db
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
	 * @see org.openmrs.module.mirebalais.api.MirebalaisHospitalService#configureZlIdentifierSources()
	 *
	 */
	@Override
	@Transactional
	public void configureZlIdentifierSources() {
		
	}
	
	/**
	 * @see org.openmrs.module.mirebalais.api.MirebalaisHospitalService#getLocalZlIdentifierPool()
	 *
	 */
	@Override
	public IdentifierPool getLocalZlIdentifierPool() {
		IdentifierPool zlIdentifierPool = (IdentifierPool) Context.getService(IdentifierSourceService.class)
		        .getIdentifierSourceByUuid(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID);
		if (zlIdentifierPool == null) {
			throw new IllegalStateException("Local ZL Identifier Source has not been configured");
		}
		return zlIdentifierPool;
	}
	
	@Override
	public RemoteIdentifierSource getRemoteZlIdentifierSource() {
		RemoteIdentifierSource remoteIdentifierSource = (RemoteIdentifierSource) Context.getService(
		    IdentifierSourceService.class).getIdentifierSourceByUuid(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID);
		if (remoteIdentifierSource == null) {
			throw new IllegalStateException("Remote ZL Identifier Source has not been configured");
		}
		return remoteIdentifierSource;
	}
	
	@Override
	public PatientIdentifierType getZlIdentifierType() {
		PatientIdentifierType zlIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(
		    MirebalaisConstants.ZL_IDENTIFIER_TYPE_UUID);
		if (zlIdentifierType == null) {
			throw new IllegalStateException("ZL Identifier Type has not been configured");
		}
		return zlIdentifierType;
	}

    @Override
    public PatientIdentifierType getExternalDossierIdentifierType() {
        PatientIdentifierType externalDossierIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(
                MirebalaisConstants.EXTERNAL_DOSSIER_NUMBER_IDENTIFIER_TYPE_UUID);
        if (externalDossierIdentifierType == null) {
            throw new IllegalStateException("External Dossier Identifier Type has not been configured");
        }
        return externalDossierIdentifierType;
    }
	
	@Override
	public SequentialIdentifierGenerator getDossierSequenceGenerator() {
		SequentialIdentifierGenerator sequentialIdentifierGenerator = (SequentialIdentifierGenerator) Context.getService(
		    IdentifierSourceService.class).getIdentifierSourceByUuid(
		    MirebalaisConstants.DOSSIER_NUMBER_ZL_IDENTIFIER_SOURCE_UUID);
		
		if (sequentialIdentifierGenerator == null) {
			throw new IllegalStateException("Sequential Identifier Generator For Dossie has not been configured");
		}
		
		return sequentialIdentifierGenerator;
	}
	
	@Override
	public PatientIdentifierType getDossierIdentifierType() {
		PatientIdentifierType dossierNumberIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(
		    MirebalaisConstants.DOSSIER_NUMBER_IDENTIFIER_TYPE_UUID);
		if (dossierNumberIdentifierType == null) {
			throw new IllegalStateException("Dossier Number Identifier Type has not been configured");
		}
		return dossierNumberIdentifierType;
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

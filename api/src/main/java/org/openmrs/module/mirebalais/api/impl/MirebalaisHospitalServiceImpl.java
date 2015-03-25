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
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.api.db.MirebalaisHospitalDAO;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.CoreMetadata;
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

    @Override
    public SequentialIdentifierGenerator getLocalZlIdentifierGenerator() {
        return getIdentifierSource(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_GENERATOR_UUID, SequentialIdentifierGenerator.class);
    }

	@Override
	public IdentifierPool getLocalZlIdentifierPool() {
        return getIdentifierSource(MirebalaisConstants.LOCAL_ZL_IDENTIFIER_POOL_UUID, IdentifierPool.class);
	}
	
	@Override
	public RemoteIdentifierSource getRemoteZlIdentifierSource() {
        return getIdentifierSource(MirebalaisConstants.REMOTE_ZL_IDENTIFIER_SOURCE_UUID, RemoteIdentifierSource.class);
	}
	
	@Override
	public PatientIdentifierType getZlIdentifierType() {
		PatientIdentifierType zlIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(CoreMetadata.PatientIdentifierTypes.ZL_EMR_ID);
		if (zlIdentifierType == null) {
			throw new IllegalStateException("ZL Identifier Type has not been configured");
		}
		return zlIdentifierType;
	}

    @Override
    public PatientIdentifierType getExternalDossierIdentifierType() {
        PatientIdentifierType externalDossierIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(CoreMetadata.PatientIdentifierTypes.EXTERNAL_DOSSIER_NUMBER);
        if (externalDossierIdentifierType == null) {
            throw new IllegalStateException("External Dossier Identifier Type has not been configured");
        }
        return externalDossierIdentifierType;
    }
	
	@Override
	public SequentialIdentifierGenerator getDossierSequenceGenerator(String identifierSourceUuid) {

		SequentialIdentifierGenerator sequentialIdentifierGenerator = (SequentialIdentifierGenerator) Context.getService(
		    IdentifierSourceService.class).getIdentifierSourceByUuid(
		    identifierSourceUuid);
		
		if (sequentialIdentifierGenerator == null) {
			throw new IllegalStateException("Sequential Identifier Generator For Dossie has not been configured");
		}
		
		return sequentialIdentifierGenerator;
	}

	@Override
	public PatientIdentifierType getDossierIdentifierType() {
		PatientIdentifierType dossierNumberIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(CoreMetadata.PatientIdentifierTypes.DOSSIER_NUMBER);
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

    private <T extends IdentifierSource> T getIdentifierSource(String uuid, Class<T> sourceType) {
        IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
        IdentifierSource source = iss.getIdentifierSourceByUuid(uuid);
        if (source == null) {
            throw new IllegalStateException(sourceType.getSimpleName() + " has not been configured");
        }
        return (T) source;
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

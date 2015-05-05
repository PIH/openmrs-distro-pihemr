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
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.mirebalais.api.db.MirebalaisHospitalDAO;
import org.openmrs.module.pihcore.metadata.haiti.HaitiPatientIdentifierTypes;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
     * @see org.openmrs.api.OrderService#getNextRadiologyOrderNumberSeedSequenceValue()
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized Long getNextRadiologyOrderNumberSeedSequenceValue() {
        return dao.getNextRadiologyOrderNumberSeedSequenceValue();
    }

    private <T extends IdentifierSource> T getIdentifierSource(String uuid, Class<T> sourceType) {
        IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
        IdentifierSource source = iss.getIdentifierSourceByUuid(uuid);
        if (source == null) {
            throw new IllegalStateException(sourceType.getSimpleName() + " has not been configured");
        }
        return (T) source;
    }
	
}

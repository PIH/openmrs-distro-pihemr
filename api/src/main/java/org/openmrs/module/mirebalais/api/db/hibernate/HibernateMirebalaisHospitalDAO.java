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
package org.openmrs.module.mirebalais.api.db.hibernate;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.GlobalProperty;
import org.openmrs.api.APIException;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.api.db.MirebalaisHospitalDAO;
import org.openmrs.util.OpenmrsConstants;

/**
 * Default implementation of {@link MirebalaisHospitalDAO}.
 */
public class HibernateMirebalaisHospitalDAO implements MirebalaisHospitalDAO {

    protected final Log log = LogFactory.getLog(this.getClass());

    private SessionFactory sessionFactory;

    /**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * @see org.openmrs.api.db.OrderDAO#getNextRadiologyOrderNumberSeedSequenceValue()
     */
    @Override
    public Long getNextRadiologyOrderNumberSeedSequenceValue() {
        Criteria searchCriteria = sessionFactory.getCurrentSession().createCriteria(GlobalProperty.class);
        searchCriteria.add(Restrictions.eq("property", MirebalaisConstants.GP_NEXT_RADIOLOGY_ORDER_NUMBER_SEED));
        GlobalProperty globalProperty = (GlobalProperty) sessionFactory.getCurrentSession().get(GlobalProperty.class,
                MirebalaisConstants.GP_NEXT_RADIOLOGY_ORDER_NUMBER_SEED, LockOptions.UPGRADE);

        if (globalProperty == null) {
            throw new APIException("Missing global property named: " + MirebalaisConstants.GP_NEXT_RADIOLOGY_ORDER_NUMBER_SEED);
        }

        String gpTextValue = globalProperty.getPropertyValue();
        if (StringUtils.isBlank(gpTextValue)) {
            throw new APIException("Invalid value for global property named: " + MirebalaisConstants.GP_NEXT_RADIOLOGY_ORDER_NUMBER_SEED);
        }

        Long gpNumericValue = null;
        try {
            gpNumericValue = Long.parseLong(gpTextValue);
        } catch (NumberFormatException ex) {
            throw new APIException("Invalid value for global property named: " + MirebalaisConstants.GP_NEXT_RADIOLOGY_ORDER_NUMBER_SEED);
        }

        globalProperty.setPropertyValue(String.valueOf(gpNumericValue + 1));

        sessionFactory.getCurrentSession().save(globalProperty);

        return gpNumericValue;
    }
}
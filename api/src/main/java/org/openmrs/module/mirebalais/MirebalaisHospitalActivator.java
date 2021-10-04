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
package org.openmrs.module.mirebalais;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.mirebalais.setup.ConfigurationSetup;
import org.openmrs.module.pihcore.PihCoreConstants;
import org.openmrs.module.pihcore.setup.MergeActionsSetup;

/**
 * Performs all necessary setup when the PIH EMR distribution starts
 * Ultimately this will be moved to the PihCoreActivator
 */
public class MirebalaisHospitalActivator extends BaseModuleActivator implements DaemonTokenAware {

    protected Log log = LogFactory.getLog(getClass());

    private DaemonToken daemonToken;

    public MirebalaisHospitalActivator() {
    }

    @Override
    public void setDaemonToken(DaemonToken daemonToken) {
        this.daemonToken = daemonToken;
    }

    /**
     * @see ModuleActivator#started()
     */
    @Override
    public void started() {
        try {
            log.info("Mirebalais Module Started, initiating configuration");

            final ConfigurationSetup configurationSetup = Context.getRegisteredComponents(ConfigurationSetup.class).get(0);
            configurationSetup.setupBase();
            configurationSetup.configureNonConceptDependencies();

            if (runInSeparateThread()) {
                log.info("Setting up configuration in a separate thread. Please monitor logs to check status.");
                Daemon.runInDaemonThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            configurationSetup.configureConceptDependencies();
                        }
                        catch (Exception e) {
                            log.error("Configuration Setup Failed", e);
                            throw new RuntimeException(e);
                        }
                    }
                }, daemonToken);
            }
            else {
                configurationSetup.configureConceptDependencies();
            }
            log.info("Distribution startup complete.");
        }
        catch (Exception e) {
            Module mod = ModuleFactory.getModuleById(MirebalaisConstants.MIREBALAIS_MODULE_ID);
            ModuleFactory.stopModule(mod, false, false);
            throw new RuntimeException("An error occurred while starting the mirebalais module", e);
        }
    }

	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("Mirebalais Hospital Module stopped");
        MergeActionsSetup.deregisterMergeActions();
	}

    // see https://pihemr.atlassian.net/browse/UHM-4459
    private boolean runInSeparateThread() {
        AdministrationService as = Context.getAdministrationService();
        String runInSeparateThread = as.getGlobalProperty(PihCoreConstants.GP_RUN_CONCEPT_SETUP_TASK_IN_SEPARATE_THREAD);
        return "true".equalsIgnoreCase(runInSeparateThread);
    }
}

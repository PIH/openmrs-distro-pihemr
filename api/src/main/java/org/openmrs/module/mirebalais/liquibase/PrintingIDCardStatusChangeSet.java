/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.mirebalais.liquibase;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.pihcore.deploy.bundle.AdministrativeConcepts;
import org.openmrs.module.pihcore.deploy.bundle.CommonConcepts;
import org.openmrs.util.DatabaseUpdater;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * The legacy patientregistration module used a concept named "PrintingIDCardStatus" to store observations as
 * to whether an id card was successfully scanned after printing, or if printing failed.
 * We have decided to migrate away from this concept (which models answers as text-obs and does not have a consistent uuid across our servers),
 * in favor of a new coded concept with a consistent uuid.  This changeset handles the data migration necessary for this,
 * but only should be enabled/run on a given system once we have transitioned to the new patient registration.
 */
public class PrintingIDCardStatusChangeSet implements CustomTaskChange {

	private static Log log = LogFactory.getLog(PrintingIDCardStatusChangeSet.class);

	/**
	 * @see CustomTaskChange#execute(Database)
	 */
	@Override
	public void execute(Database database) throws CustomChangeException {

		String oldQuestion = "(select concept_id from concept_name where name = 'PrintingIDCardStatus')";
		String newQuestion = "(select concept_id from concept where uuid = '"+ AdministrativeConcepts.Concepts.ID_CARD_PRINTING_SUCCESSFUL + "')";
		String yes = "(select concept_id from concept where uuid = '"+ CommonConcepts.Concepts.YES + "')";
		String no = "(select concept_id from concept where uuid = '"+ CommonConcepts.Concepts.NO + "')";

		StringBuilder migrateSuccessful = new StringBuilder();
		migrateSuccessful.append("update obs set ");
		migrateSuccessful.append("		concept_id = ").append(newQuestion).append(", ");
		migrateSuccessful.append("		value_coded =").append(yes).append(", ");
		migrateSuccessful.append("		value_text = null ");
		migrateSuccessful.append("where ");
		migrateSuccessful.append("		concept_id = ").append(oldQuestion).append(" ");
		migrateSuccessful.append("and   value_text = 'true'");
		executeUpdate(migrateSuccessful, database);
		log.warn("Successfully migrated 'print successful' obs");

		StringBuilder migrateFailed = new StringBuilder();
		migrateFailed.append("update obs set ");
		migrateFailed.append("		concept_id = ").append(newQuestion).append(", ");
		migrateFailed.append("		value_coded =").append(no).append(", ");
		migrateFailed.append("		value_text = null ");
		migrateFailed.append("where ");
		migrateFailed.append("		concept_id = ").append(oldQuestion).append(" ");
		migrateFailed.append("and   value_text = 'false'");
		executeUpdate(migrateFailed, database);
		log.warn("Successfully migrated 'print failed' obs");

		// First find the Printing obs with the highest id for the same person, and date.  These are the obs we'll keep.
		StringBuilder obsToKeep = new StringBuilder();
		obsToKeep.append("select max_obs.obs_id from (");
		obsToKeep.append("		select 		max(obs_id) as obs_id, person_id, obs_datetime ");
		obsToKeep.append("		from		obs ");
		obsToKeep.append("		where		concept_id = ").append(newQuestion).append(" ");
		obsToKeep.append("		group by 	person_id, obs_datetime ");
		obsToKeep.append(") max_obs ");

		// Now, update all other Printing obs as voided
		StringBuilder voidObs = new StringBuilder();
		voidObs.append("update obs set ");
		voidObs.append("		voided = true, ");
		voidObs.append("		date_voided = now(), ");
		voidObs.append("		voided_by = ").append(DatabaseUpdater.getAuthenticatedUserId()).append(", ");
		voidObs.append("		void_reason = 'Superceded by Obs with same concept, person, and datetime, with higher obs_id' ");
		voidObs.append("where ");
		voidObs.append("	 	concept_id = ").append(newQuestion).append(" ");
		voidObs.append("and		obs_id not in ( ").append(obsToKeep.toString()).append(" ) ");
		executeUpdate(voidObs, database);
		log.warn("Successfully voided obs which had been superceded by another obs");
	}

	/**
	 * Executes a SQL update
	 * @throws CustomChangeException
	 */
	protected void executeUpdate(StringBuilder sql, Database database) throws CustomChangeException {
		PreparedStatement s = null;
		try {
			JdbcConnection connection = (JdbcConnection) database.getConnection();
			s = connection.prepareStatement(sql.toString());
			s.executeUpdate();
		}
		catch (Exception e) {
			throw new CustomChangeException("Failed to execute sql: " + sql.toString());
		}
		finally {
			try {
				if (s != null) {
					s.close();
				}
			}
			catch (Exception e) {}
		}
	}

	/**
	 * @see liquibase.change.custom.CustomChange#getConfirmationMessage()
	 */
	@Override
	public String getConfirmationMessage() {
		return "Successfully migrated PrintingIDCardStatus observations";
	}

	/**
	 * @see liquibase.change.custom.CustomChange#setFileOpener(ResourceAccessor)
	 */
	@Override
	public void setFileOpener(ResourceAccessor fileOpener) {
	}

	/**
	 * @see liquibase.change.custom.CustomChange#setUp()
	 */
	@Override
	public void setUp() throws SetupException {
	}

	/**
	 * @see liquibase.change.custom.CustomChange#validate(liquibase.database.Database)
	 */
	@Override
	public ValidationErrors validate(Database database) {
		return new ValidationErrors();
	}
}

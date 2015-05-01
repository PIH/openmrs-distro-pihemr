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

import java.sql.PreparedStatement;

/**
 * The legacy patientregistration module used a concept named "PrintingIDCardStatus" to store observations as
 to whether an id card was successfully scanned after printing, or if printing failed.
 We have decided to migrate away from this concept (which models answers as text-obs and does not have a consistent uuid across our servers),
 in favor of a new coded concept with a consistent uuid.  This changeset handles the data migration necessary for this,
 but only should be enabled/run on a given system once we have transitioned to the new patient registration.
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
		migrateSuccessful.append("		concept_id = ").append(oldQuestion).append(", ");
		migrateSuccessful.append("and     value_text = 'true'");
		executeSql(migrateSuccessful, database);
		log.info("Successfully migrated 'print successful' obs");

		StringBuilder migrateFailed = new StringBuilder();
		migrateFailed.append("update obs set ");
		migrateFailed.append("		concept_id = ").append(newQuestion).append(", ");
		migrateFailed.append("		value_coded =").append(no).append(", ");
		migrateFailed.append("		value_text = null ");
		migrateFailed.append("where ");
		migrateFailed.append("		concept_id = ").append(oldQuestion).append(", ");
		migrateFailed.append("and     value_text = 'false'");
		executeSql(migrateFailed, database);
		log.info("Successfully migrated 'print failed' obs");

		// TODO: Implement this
		// The legacy Obs are being created far more frequently (and for broader reasons) in the legacy module.
		// We should consider cleaning up these obs if possible
	}

	/**
	 * Executes a SQL update
	 * @throws CustomChangeException
	 */
	protected void executeSql(StringBuilder sql, Database database) throws CustomChangeException {
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

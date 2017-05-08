/*
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
package org.openmrs.module.mirebalais.liquibase;

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.pihcore.deploy.bundle.core.LocationBundle;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.AdministrativeConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.AnswerConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CommonConcepts;
import org.openmrs.module.pihcore.deploy.bundle.core.concept.CoreConceptMetadataBundle;
import org.openmrs.module.pihcore.deploy.bundle.haiti.PihHaitiPatientIdentifierTypeBundle;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Tests that the change set executes
 */
@SkipBaseSetup
public class PrintingIDCardStatusChangeSetTest extends BaseModuleContextSensitiveTest {

	private Database database;
	private Concept oldQuestion;

	@Autowired
	private TestDataManager testData;

	@Autowired
    AdministrativeConcepts administrativeConcepts;

	@Autowired
    CoreConceptMetadataBundle coreConceptMetadataBundle;

	@Autowired
    CommonConcepts commonConcepts;

	@Autowired
    AnswerConcepts answerConcepts;

	@Autowired
	PihHaitiPatientIdentifierTypeBundle pihHaitiPatientIdentifierTypeBundle;

	@Autowired
    LocationBundle locationBundle;

	@Autowired @Qualifier("adminService")
	AdministrationService administrationService;

	@Before
	public void before() throws Exception {
		initializeInMemoryDatabase();
		authenticate();

		database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(getConnection()));
		database.setDatabaseChangeLogTableName("LIQUIBASECHANGELOG");
		database.setDatabaseChangeLogLockTableName("LIQUIBASECHANGELOGLOCK");

		executeDataSet("openmrsCoreDataset.xml");
		coreConceptMetadataBundle.install();
		commonConcepts.install();
		answerConcepts.install();
		administrativeConcepts.install();
		pihHaitiPatientIdentifierTypeBundle.install();
		locationBundle.install();

		oldQuestion = testData.concept().name(Locale.US, "PrintingIDCardStatus").datatype("Text").conceptClass("Misc").save();
	}

	@After
	public void after() throws Exception {
		database.close();
	}
	
	@Test
	public void shouldMigrateObsAndVoidAppropriately() throws Exception{

		Patient patient = testData.randomPatient().save();
		Date date = new Date();

		Concept newQuestion = MetadataUtils.existing(Concept.class, AdministrativeConcepts.Concepts.ID_CARD_PRINTING_SUCCESSFUL);
		Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
		Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);

		// Simulate the current behavior where two non-voided obs with the same concept and date are created with different values
		testData.obs().person(patient).concept(oldQuestion).obsDatetime(date).value("true").save();
		testData.obs().person(patient).concept(oldQuestion).obsDatetime(date).value("false").save();

		// We create an obs of this concept too to make sure that we don't void obs of other questions
		Concept otherConcept = MetadataUtils.existing(Concept.class, AdministrativeConcepts.Concepts.ID_CARD_PRINTING_REQUESTED);
		testData.obs().person(patient).concept(otherConcept).obsDatetime(date).value(yes).save();

		Assert.assertEquals(2, getNumObs(patient, oldQuestion, null, null, null));  // Before migration, we have 2 old obs

		PrintingIDCardStatusChangeSet changeSet = new PrintingIDCardStatusChangeSet();
		changeSet.execute(database);

		Assert.assertEquals(2, getNumObs(patient, newQuestion, null, null, null)); // After migration we have 2 new obs
		Assert.assertEquals(1, getNumObs(patient, newQuestion, yes, null, true)); // The first true answer is voided
		Assert.assertEquals(1, getNumObs(patient, newQuestion, no, null, false)); // The second false answer is not voided
		Assert.assertEquals(1, getNumObs(patient, otherConcept, yes, null, null)); // This other obs is left unaffected
	}

	protected int getNumObs(Patient patient, Concept question, Concept valueCoded, String valueText, Boolean voided) {
		StringBuilder q = new StringBuilder();
		q.append("select count(*) from obs where person_id = ").append(patient.getId()).append(" and concept_id = ").append(question.getId()).append(" ");
		if (valueCoded != null) {
			q.append("and value_coded ").append(valueCoded.equals("null") ? "is null " : " = " + valueCoded.getId()).append(" ");
		}
		if (valueText != null) {
			q.append("and value_text ").append(valueText.equals("null") ? "is null " : " = '" + valueText).append("' ");
		}
		if (voided != null) {
			q.append("and voided = " + voided.booleanValue());
		}
		List<List<Object>> ret = administrationService.executeSQL(q.toString(), true);
		return ((Number)ret.get(0).get(0)).intValue();
	}
}

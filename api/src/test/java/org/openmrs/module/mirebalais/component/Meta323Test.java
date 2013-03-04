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

package org.openmrs.module.mirebalais.component;

import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatasharing.ImportConfig;
import org.openmrs.module.metadatasharing.ImportMode;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.wrapper.PackageImporter;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class Meta323Test extends BaseModuleContextSensitiveTest {

    @Test
    public void testImporting() throws Exception {
        ConceptService conceptService = Context.getConceptService();

        System.out.println("Importing version 1, that has only the concept that is 7164 on concepts.pih-emr.org");

        installPackage("META-323_test_case-1.zip");

        // we imported just this concept
        assertThat(conceptService.getConceptByUuid("06cc08fb-414a-46e6-8c20-136535609812"), notNullValue());
        assertThat(conceptService.getConceptByUuid("06cc08fb-414a-46e6-8c20-136535609812").getName().getName(), is("Rheumatic Fever Without Heart Involvement"));

        // haven't imported this yet
        assertThat(conceptService.getConceptByUuid("006ab3b2-a0ea-45bf-b495-83e06f26f87a"), nullValue());

        Context.flushSession();

        System.out.println("Importing version 2, that has both concepts, 7164 and 3308, from concepts.pih-emr.org");
        System.out.println("Both are mapped to ICD-10-WHO I00, one same-as, one narrower-than");

        installPackage("META-323_test_case-2.zip");

        assertThat(conceptService.getConceptByUuid("06cc08fb-414a-46e6-8c20-136535609812"), notNullValue());
        // this currently fails because this concept was actually overwritten by 006ab3b2-a0ea-45bf-b495-83e06f26f87a, so it has different names
        assertThat(conceptService.getConceptByUuid("06cc08fb-414a-46e6-8c20-136535609812").getName().getName(), is("Rheumatic Fever Without Heart Involvement"));
        assertThat(conceptService.getConceptByUuid("006ab3b2-a0ea-45bf-b495-83e06f26f87a"), notNullValue());
        assertThat(conceptService.getConceptByUuid("006ab3b2-a0ea-45bf-b495-83e06f26f87a").getName().getName(), is("Acute rheumatic fever"));
    }

    private void installPackage(String filename) throws IOException {
        PackageImporter metadataImporter = MetadataSharing.getInstance().newPackageImporter();
        metadataImporter.setImportConfig(ImportConfig.valueOf(ImportMode.MIRROR));
        metadataImporter.loadSerializedPackageStream(getClass().getClassLoader().getResourceAsStream(filename));
        metadataImporter.importPackage();
    }

}

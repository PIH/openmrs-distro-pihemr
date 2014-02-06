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

package org.openmrs.module.mirebalais.api.db.hibernate;

import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * We can't easily test the full behavior against a MySQL 5.6 database, so we just verify that milliseconds are
 * zeroed out when saving or updating an item.
 */
public class DropMillisecondsHibernateInterceptorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    PersonService personService;

    @Test
    public void testClearingMillisecondsOnNewObject() throws Exception {
        Date dateWithMillisecond = new Date(567l);
        Date dateWithoutMillisecond = new Date(0l);

        Person person = new Person();
        person.addName(new PersonName("Alice", null, "Paul"));
        person.setGender("F");
        person.setBirthdate(dateWithMillisecond);

        personService.savePerson(person);
        Context.flushSession();

        assertThat(person.getBirthdate(), is(dateWithoutMillisecond));
    }

    @Test
    public void testClearingMillisecondsWhenUpdatingExistingObject() throws Exception {
        Date dateWithMillisecond = new Date(567l);
        Date dateWithoutMillisecond = new Date(0l);

        Person person = personService.getPerson(1);
        person.setBirthdate(dateWithMillisecond);

        personService.savePerson(person);
        Context.flushSession();

        assertThat(person.getBirthdate(), is(dateWithoutMillisecond));
    }
}

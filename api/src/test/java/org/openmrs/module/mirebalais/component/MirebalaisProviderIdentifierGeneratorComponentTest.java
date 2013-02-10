package org.openmrs.module.mirebalais.component;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.account.AccountDomainWrapper;
import org.openmrs.module.emr.account.AccountService;
import org.openmrs.module.mirebalais.MirebalaisHospitalActivator;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class MirebalaisProviderIdentifierGeneratorComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProviderManagementService providerManagementService;

    private MirebalaisHospitalActivator activator;

    @Before
    public void beforeAllTests() throws Exception {
        executeDataSet("requiredDataTestDataset.xml");
        executeDataSet("globalPropertiesTestDataset.xml");
        executeDataSet("mirebalaisProviderIdentifierGeneratorComponentTestDataset.xml");
        activator = new MirebalaisHospitalActivator();
        activator.started();
    }

    @Test
    public void shouldCreateProviderWithAppropriateIdentifier() {

        Person person = Context.getPersonService().getPerson(2);

        AccountDomainWrapper account = accountService.getAccountByPerson(person);

        ProviderRole providerRole = providerManagementService .getProviderRole(1001);
        account.setProviderRole(providerRole);

        accountService.saveAccount(account);

        Assert.assertEquals(providerRole, account.getProvider().getProviderRole());
        Assert.assertEquals("MAAAD", account.getProvider().getIdentifier());

    }
}

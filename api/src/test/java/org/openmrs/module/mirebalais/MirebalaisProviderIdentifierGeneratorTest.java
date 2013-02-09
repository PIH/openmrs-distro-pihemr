package org.openmrs.module.mirebalais;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.module.providermanagement.Provider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MirebalaisProviderIdentifierGeneratorTest {

    @Test
    public void shouldGenerateAppropriateIdentifierForProvider() {
        Provider provider = new Provider();
        provider.setId(4);
        String identifier = (new MirebalaisProviderIdentifierGenerator()).generateIdentifier(provider);
        Assert.assertEquals("MAAFD", identifier);
    }

    @Test
    public void shouldNotFailForHighId() {
        Provider provider = new Provider();
        provider.setId(26000);
        String identifier = (new MirebalaisProviderIdentifierGenerator()).generateIdentifier(provider);
        Assert.assertEquals("M971E", identifier);
    }

    // we are generating a 3-digit base 30 number, which gives us 27000 possible combinations
    // after that, things will start to fail

    @Test(expected = RuntimeException.class)
    public void shouldFailForVeryHighId() {
        Provider provider = new Provider();
        provider.setId(27000);
        String identifier = (new MirebalaisProviderIdentifierGenerator()).generateIdentifier(provider);
    }
}

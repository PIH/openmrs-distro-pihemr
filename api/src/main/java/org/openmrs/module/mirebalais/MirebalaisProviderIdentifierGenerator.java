package org.openmrs.module.mirebalais;

import org.openmrs.module.emr.account.ProviderIdentifierGenerator;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.validator.LuhnMod30IdentifierValidator;
import org.openmrs.module.providermanagement.Provider;

public class MirebalaisProviderIdentifierGenerator implements ProviderIdentifierGenerator {

    // this generates a 3digit base 30 identifier with an "M" prefix and a check-digit suffix
    // the identifier is seeded on the primary key of the associated provider
    // since 30^3 = 27000, this will start to fail if the primary key every goes above 27000

    @Override
    public String generateIdentifier(Provider provider) {

        if (provider.getId() == null) {
            throw new IllegalStateException("Cannot generate identifier for provider without primary key");
        }

        SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
        generator.setBaseCharacterSet("ACDEFGHJKLMNPRTUVWXY1234567890");
        generator.setFirstIdentifierBase("100");
        generator.setPrefix("M");
        generator.setLength(4);

        String identifier = generator.getIdentifierForSeed(provider.getId().longValue());

        return new LuhnMod30IdentifierValidator().getValidIdentifier(identifier);
    }
}

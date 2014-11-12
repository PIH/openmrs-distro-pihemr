package org.openmrs.module.mirebalais.setup;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.module.mirebalais.config.Config;
import org.openmrs.module.mirebalais.config.ConfigDescriptor;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.CoreMetadata;
import org.openmrs.module.mirebalaismetadata.deploy.bundle.ZanmiLocations;

import java.util.Arrays;
import java.util.Collection;

public class LocationTagSetup {


    public static void setupLocationTags(LocationService locationService, Config config, FeatureToggleProperties featureToggles) {

        if (config.getSite().equals(ConfigDescriptor.Site.LACOLLINE)) {
            setupLocationTagsForLacolline(locationService);
        }
        else if (config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
            setupLocationTagsForMirebalais(locationService, featureToggles);
        }

    }

    private static void setupLocationTagsForLacolline(LocationService locationService) {

        setLocationTagsFor(locationService, CoreMetadata.LocationTags.LOGIN_LOCATION, Arrays.asList(
                ZanmiLocations.Locations.LACOLLINE
        ));

        setLocationTagsFor(locationService, CoreMetadata.LocationTags.CONSULT_NOTE_LOCATION, Arrays.asList(
                ZanmiLocations.Locations.LACOLLINE
        ));
    }

    private static void setupLocationTagsForMirebalais(LocationService locationService, FeatureToggleProperties featureToggles) {

        setLocationTagsFor(locationService, CoreMetadata.LocationTags.LOGIN_LOCATION, Arrays.asList(
            ZanmiLocations.MirebalaisLocations.CLINIC_REGISTRATION,
            ZanmiLocations.MirebalaisLocations.EMERGENCY_DEPARTMENT_RECEPTION,
            ZanmiLocations.MirebalaisLocations.CENTRAL_ARCHIVES,
            ZanmiLocations.MirebalaisLocations.OUTPATIENT_CLINIC,
            ZanmiLocations.MirebalaisLocations.EMERGENCY,
            ZanmiLocations.MirebalaisLocations.COMMUNITY_HEALTH,
            ZanmiLocations.MirebalaisLocations.DENTAL,
            ZanmiLocations.MirebalaisLocations.WOMENS_CLINIC,
            ZanmiLocations.MirebalaisLocations.WOMENS_TRIAGE,
            ZanmiLocations.MirebalaisLocations.LABOR_AND_DELIVERY,
            ZanmiLocations.MirebalaisLocations.ANTEPARTUM_WARD,
            ZanmiLocations.MirebalaisLocations.POSTPARTUM_WARD,
            ZanmiLocations.MirebalaisLocations.POST_OP_GYN,
            ZanmiLocations.MirebalaisLocations.SURGICAL_WARD,
            ZanmiLocations.MirebalaisLocations.OPERATING_ROOMS,
            ZanmiLocations.MirebalaisLocations.PRE_OP_PACU,
            ZanmiLocations.MirebalaisLocations.MAIN_LABORATORY,
            ZanmiLocations.MirebalaisLocations.WOMENS_OUTPATIENT_LABORATORY,
            ZanmiLocations.MirebalaisLocations.RADIOLOGY,
            ZanmiLocations.MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            ZanmiLocations.MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            ZanmiLocations.MirebalaisLocations.PEDIATRICS,
            ZanmiLocations.MirebalaisLocations.ICU,
            ZanmiLocations.MirebalaisLocations.NICU,
            ZanmiLocations.MirebalaisLocations.ISOLATION,
            ZanmiLocations.MirebalaisLocations.CHEMOTHERAPY,
            ZanmiLocations.MirebalaisLocations.OUTPATIENT_CLINIC_PHARMACY,
            ZanmiLocations.MirebalaisLocations.WOMENS_AND_CHILDRENS_PHARMACY,
            ZanmiLocations.MirebalaisLocations.REHABILITATION,
            ZanmiLocations.MirebalaisLocations.FAMILY_PLANNING,
            ZanmiLocations.MirebalaisLocations.BLOOD_BANK,
            (featureToggles.isFeatureEnabled("cdi") ? ZanmiLocations.MirebalaisLocations.CDI_ACHIV : null),
            (featureToggles.isFeatureEnabled("cdi") ? ZanmiLocations.MirebalaisLocations.CDI_KLINIK_EKSTEN : null),
            (featureToggles.isFeatureEnabled("cdi") ? ZanmiLocations.MirebalaisLocations.CDI_RESEPSYON : null)
        ));


        setLocationTagsFor(locationService, CoreMetadata.LocationTags.ADMISSION_LOCATION, Arrays.asList(
            ZanmiLocations.MirebalaisLocations.SURGICAL_WARD,
            ZanmiLocations.MirebalaisLocations.ANTEPARTUM_WARD,
            ZanmiLocations.MirebalaisLocations.LABOR_AND_DELIVERY,
            ZanmiLocations.MirebalaisLocations.POSTPARTUM_WARD,
            ZanmiLocations.MirebalaisLocations.PEDIATRICS,
            ZanmiLocations.MirebalaisLocations.NICU,
            ZanmiLocations.MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            ZanmiLocations.MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            ZanmiLocations.MirebalaisLocations.ISOLATION,
            ZanmiLocations.MirebalaisLocations.REHABILITATION,
            ZanmiLocations.MirebalaisLocations.POST_OP_GYN
        ));

        setLocationTagsFor(locationService, CoreMetadata.LocationTags.TRANSFER_LOCAITON, Arrays.asList(
            ZanmiLocations.MirebalaisLocations.SURGICAL_WARD,
            ZanmiLocations.MirebalaisLocations.ANTEPARTUM_WARD,
            ZanmiLocations.MirebalaisLocations.LABOR_AND_DELIVERY,
            ZanmiLocations.MirebalaisLocations.POSTPARTUM_WARD,
            ZanmiLocations.MirebalaisLocations.EMERGENCY,
            ZanmiLocations.MirebalaisLocations.COMMUNITY_HEALTH,
            ZanmiLocations.MirebalaisLocations.OUTPATIENT_CLINIC,
            ZanmiLocations.MirebalaisLocations.WOMENS_CLINIC,
            ZanmiLocations.MirebalaisLocations.WOMENS_TRIAGE,
            ZanmiLocations.MirebalaisLocations.PEDIATRICS,
            ZanmiLocations.MirebalaisLocations.NICU,
            ZanmiLocations.MirebalaisLocations.DENTAL,
            ZanmiLocations.MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            ZanmiLocations.MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            ZanmiLocations.MirebalaisLocations.ISOLATION,
            ZanmiLocations.MirebalaisLocations.REHABILITATION,
            ZanmiLocations.MirebalaisLocations.POST_OP_GYN,
            (featureToggles.isFeatureEnabled("cdi") ? ZanmiLocations.MirebalaisLocations.CDI_KLINIK_EKSTEN : null)
        ));

        setLocationTagsFor(locationService, CoreMetadata.LocationTags.CONSULT_NOTE_LOCATION, Arrays.asList(
            ZanmiLocations.MirebalaisLocations.ANTEPARTUM_WARD,
            ZanmiLocations.MirebalaisLocations.MENS_INTERNAL_MEDICINE,
            ZanmiLocations.MirebalaisLocations.MENS_INTERNAL_MEDICINE_A,
            ZanmiLocations.MirebalaisLocations.MENS_INTERNAL_MEDICINE_B,
            ZanmiLocations.MirebalaisLocations.OUTPATIENT_CLINIC,
            ZanmiLocations.MirebalaisLocations.SURGICAL_WARD,
            ZanmiLocations.MirebalaisLocations.POSTPARTUM_WARD,
            ZanmiLocations.MirebalaisLocations.COMMUNITY_HEALTH,
            ZanmiLocations.MirebalaisLocations.LABOR_AND_DELIVERY,
            ZanmiLocations.MirebalaisLocations.NICU,
            ZanmiLocations.MirebalaisLocations.PEDIATRICS,
            ZanmiLocations.MirebalaisLocations.PEDIATRICS_A,
            ZanmiLocations.MirebalaisLocations.PEDIATRICS_B,
            ZanmiLocations.MirebalaisLocations.WOMENS_INTERNAL_MEDICINE,
            ZanmiLocations.MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_A,
            ZanmiLocations.MirebalaisLocations.WOMENS_INTERNAL_MEDICINE_B,
            ZanmiLocations.MirebalaisLocations.WOMENS_CLINIC,
            ZanmiLocations.MirebalaisLocations.WOMENS_TRIAGE,
            ZanmiLocations.MirebalaisLocations.CHEMOTHERAPY,
            ZanmiLocations.MirebalaisLocations.DENTAL,
            ZanmiLocations.MirebalaisLocations.ISOLATION,
            ZanmiLocations.MirebalaisLocations.REHABILITATION,
            ZanmiLocations.MirebalaisLocations.EMERGENCY,
            ZanmiLocations.MirebalaisLocations.FAMILY_PLANNING,
            ZanmiLocations.MirebalaisLocations.POST_OP_GYN,
            (featureToggles.isFeatureEnabled("cdi") ? ZanmiLocations.MirebalaisLocations.CDI_KLINIK_EKSTEN : null)
        ));

        setLocationTagsFor(locationService, CoreMetadata.LocationTags.ED_NOTE_LOCATION, Arrays.asList(
            ZanmiLocations.MirebalaisLocations.EMERGENCY
        ));

        setLocationTagsFor(locationService, CoreMetadata.LocationTags.SURGERY_NOTE_LOCATION, Arrays.asList(
            ZanmiLocations.MirebalaisLocations.SURGICAL_WARD,
            ZanmiLocations.MirebalaisLocations.OPERATING_ROOMS,
            ZanmiLocations.MirebalaisLocations.POSTPARTUM_WARD,
            ZanmiLocations.MirebalaisLocations.POST_OP_GYN
        ));

        setLocationTagsFor(locationService, CoreMetadata.LocationTags.DISPENSING_LOCATION, Arrays.asList(
            ZanmiLocations.MirebalaisLocations.WOMENS_AND_CHILDRENS_PHARMACY,
            ZanmiLocations.MirebalaisLocations.OUTPATIENT_CLINIC_PHARMACY
        ));

        setLocationTagsFor(locationService, CoreMetadata.LocationTags.APPOINTMENT_LOCATION, Arrays.asList(
            ZanmiLocations.MirebalaisLocations.CHEMOTHERAPY,
            ZanmiLocations.MirebalaisLocations.OUTPATIENT_CLINIC,
            ZanmiLocations.MirebalaisLocations.WOMENS_CLINIC,
            ZanmiLocations.MirebalaisLocations.MAIN_LABORATORY,
            ZanmiLocations.MirebalaisLocations.WOMENS_OUTPATIENT_LABORATORY,
            ZanmiLocations.MirebalaisLocations.COMMUNITY_HEALTH,
            ZanmiLocations.MirebalaisLocations.DENTAL,
            ZanmiLocations.MirebalaisLocations.FAMILY_PLANNING,
            ZanmiLocations.MirebalaisLocations.WOMENS_AND_CHILDRENS_PHARMACY,
            ZanmiLocations.MirebalaisLocations.RADIOLOGY,
            ZanmiLocations.MirebalaisLocations.OUTPATIENT_CLINIC_PHARMACY,
            (featureToggles.isFeatureEnabled("cdi") ? ZanmiLocations.MirebalaisLocations.CDI_KLINIK_EKSTEN : null)
        ));


    }


    private static void setLocationTagsFor(LocationService service, String locationTagUuid, Collection<String> uuidsThatGetTag) {

        LocationTag tag = service.getLocationTagByUuid(locationTagUuid);

        for (Location candidate : service.getAllLocations()) {
            boolean expected = uuidsThatGetTag.contains(candidate.getUuid());
            boolean actual = candidate.hasTag(tag.getName());
            if (actual && !expected) {
                candidate.removeTag(tag);
                service.saveLocation(candidate);
            } else if (!actual && expected) {
                candidate.addTag(tag);
                service.saveLocation(candidate);
            }
        }
    }

}

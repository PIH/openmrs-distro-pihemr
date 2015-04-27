package org.openmrs.module.mirebalais.setup;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.pihcore.config.ConfigDescriptor;
import org.openmrs.module.mirebalaismetadata.constants.LocationTags;
import org.openmrs.module.mirebalaismetadata.constants.Locations;
import org.openmrs.module.mirebalaismetadata.descriptor.LocationDescriptor;
import org.openmrs.module.mirebalaismetadata.descriptor.LocationTagDescriptor;

import java.util.Arrays;
import java.util.Collection;

public class LocationTagSetup {

    public static void setupLocationTags(LocationService locationService, Config config) {

        if (config.getSite().equals(ConfigDescriptor.Site.LACOLLINE)) {
            setupLocationTagsForLacolline(locationService);
        }
        else if (config.getSite().equals(ConfigDescriptor.Site.MIREBALAIS)) {
            setupLocationTagsForMirebalais(locationService);
        }

    }

    private static void setupLocationTagsForLacolline(LocationService locationService) {
        setLocationTagsFor(locationService, LocationTags.LOGIN_LOCATION, Arrays.asList(Locations.LACOLLINE));
        setLocationTagsFor(locationService, LocationTags.CONSULT_NOTE_LOCATION, Arrays.asList(Locations.LACOLLINE));
        setLocationTagsFor(locationService, LocationTags.VITALS_LOCATION,  Arrays.asList(Locations.LACOLLINE));
        setLocationTagsFor(locationService, LocationTags.VITALS_LOCATION, Arrays.asList(Locations.LACOLLINE));
        setLocationTagsFor(locationService, LocationTags.CHECKIN_LOCATION, Arrays.asList(Locations.LACOLLINE));
        setLocationTagsFor(locationService, LocationTags.REGISTRATION_LOCATION, Arrays.asList(Locations.LACOLLINE));
        setLocationTagsFor(locationService, LocationTags.ADMISSION_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.TRANSFER_LOCAITON, null);
        setLocationTagsFor(locationService, LocationTags.ED_NOTE_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.SURGERY_NOTE_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.APPOINTMENT_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.DISPENSING_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.INPATIENTS_APP_LOCATION, null);
        setLocationTagsFor(locationService, LocationTags.ORDER_RADIOLOGY_STUDY_LOCATION, null);
    }

    private static void setupLocationTagsForMirebalais(LocationService locationService) {

        setLocationTagsFor(locationService, LocationTags.LOGIN_LOCATION, Arrays.asList(
            Locations.CLINIC_REGISTRATION,
            Locations.EMERGENCY_DEPARTMENT_RECEPTION,
            Locations.CENTRAL_ARCHIVES,
            Locations.OUTPATIENT_CLINIC,
            Locations.EMERGENCY,
            Locations.COMMUNITY_HEALTH,
            Locations.DENTAL,
            Locations.WOMENS_CLINIC,
            Locations.WOMENS_TRIAGE,
            Locations.LABOR_AND_DELIVERY,
            Locations.ANTEPARTUM_WARD,
            Locations.POSTPARTUM_WARD,
            Locations.POST_OP_GYN,
            Locations.SURGICAL_WARD,
            Locations.OPERATING_ROOMS,
            Locations.PRE_OP_PACU,
            Locations.MAIN_LABORATORY,
            Locations.WOMENS_OUTPATIENT_LABORATORY,
            Locations.RADIOLOGY,
            Locations.MENS_INTERNAL_MEDICINE,
            Locations.WOMENS_INTERNAL_MEDICINE,
            Locations.PEDIATRICS,
            Locations.ICU,
            Locations.NICU,
            Locations.ISOLATION,
            Locations.CHEMOTHERAPY,
            Locations.OUTPATIENT_CLINIC_PHARMACY,
            Locations.WOMENS_AND_CHILDRENS_PHARMACY,
            Locations.REHABILITATION,
            Locations.FAMILY_PLANNING,
            Locations.BLOOD_BANK,
            Locations.CDI_KLINIK_EKSTEN_JENERAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));


        setLocationTagsFor(locationService, LocationTags.ADMISSION_LOCATION, Arrays.asList(
            Locations.SURGICAL_WARD,
            Locations.ANTEPARTUM_WARD,
            Locations.LABOR_AND_DELIVERY,
            Locations.POSTPARTUM_WARD,
            Locations.PEDIATRICS,
            Locations.NICU,
            Locations.MENS_INTERNAL_MEDICINE,
            Locations.WOMENS_INTERNAL_MEDICINE,
            Locations.ISOLATION,
            Locations.REHABILITATION,
            Locations.POST_OP_GYN,
            Locations.ICU
        ));

        setLocationTagsFor(locationService, LocationTags.TRANSFER_LOCAITON, Arrays.asList(
            Locations.SURGICAL_WARD,
            Locations.ANTEPARTUM_WARD,
            Locations.LABOR_AND_DELIVERY,
            Locations.POSTPARTUM_WARD,
            Locations.EMERGENCY,
            Locations.COMMUNITY_HEALTH,
            Locations.OUTPATIENT_CLINIC,
            Locations.WOMENS_CLINIC,
            Locations.WOMENS_TRIAGE,
            Locations.PEDIATRICS,
            Locations.NICU,
            Locations.DENTAL,
            Locations.MENS_INTERNAL_MEDICINE,
            Locations.WOMENS_INTERNAL_MEDICINE,
            Locations.ISOLATION,
            Locations.REHABILITATION,
            Locations.POST_OP_GYN,
            Locations.ICU,
            Locations.CDI_KLINIK_EKSTEN_JENERAL
        ));

        setLocationTagsFor(locationService, LocationTags.CONSULT_NOTE_LOCATION, Arrays.asList(
            Locations.POSTPARTUM_WARD,
            Locations.WOMENS_CLINIC,
            Locations.PRE_OP_PACU,
            Locations.OUTPATIENT_CLINIC,
            Locations.ISOLATION,
            Locations.DENTAL,
            Locations.MENS_INTERNAL_MEDICINE,
            Locations.MENS_INTERNAL_MEDICINE_A,
            Locations.MENS_INTERNAL_MEDICINE_B,
            Locations.WOMENS_INTERNAL_MEDICINE,
            Locations.WOMENS_INTERNAL_MEDICINE_A,
            Locations.WOMENS_INTERNAL_MEDICINE_B,
            Locations.PEDIATRICS,
            Locations.PEDIATRICS_A,
            Locations.PEDIATRICS_B,
            Locations.SURGICAL_WARD,
            Locations.POST_OP_GYN,
            Locations.COMMUNITY_HEALTH,
            Locations.NICU,
            Locations.WOMENS_TRIAGE,
            Locations.ICU,
            Locations.LABOR_AND_DELIVERY,
            Locations.CHEMOTHERAPY,
            Locations.REHABILITATION,
            Locations.FAMILY_PLANNING,
            Locations.CDI_KLINIK_EKSTEN_JENERAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.ED_NOTE_LOCATION, Arrays.asList(
            Locations.EMERGENCY
        ));

        setLocationTagsFor(locationService, LocationTags.SURGERY_NOTE_LOCATION, Arrays.asList(
            Locations.SURGICAL_WARD,
            Locations.OPERATING_ROOMS,
            Locations.POSTPARTUM_WARD,
            Locations.POST_OP_GYN
        ));

        // TODO: update the following tags to remove unnecessary/unsupported CDI locations

        setLocationTagsFor(locationService, LocationTags.ADMISSION_NOTE_LOCATION, Arrays.asList(
            Locations.ANTEPARTUM_WARD,
            Locations.POSTPARTUM_WARD,
            Locations.PRE_OP_PACU,
            Locations.MENS_INTERNAL_MEDICINE,
            Locations.MENS_INTERNAL_MEDICINE_A,
            Locations.MENS_INTERNAL_MEDICINE_B,
            Locations.WOMENS_INTERNAL_MEDICINE,
            Locations.WOMENS_INTERNAL_MEDICINE_A,
            Locations.WOMENS_INTERNAL_MEDICINE_B,
            Locations.PEDIATRICS,
            Locations.PEDIATRICS_A,
            Locations.PEDIATRICS_B,
            Locations.SURGICAL_WARD,
            Locations.POST_OP_GYN,
            Locations.NICU,
            Locations.OPERATING_ROOMS,
            Locations.WOMENS_TRIAGE,
            Locations.ICU,
            Locations.LABOR_AND_DELIVERY,
            Locations.ISOLATION,
            Locations.REHABILITATION,
            Locations.CDI_KLINIK_EKSTEN_JENERAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));


        setLocationTagsFor(locationService, LocationTags.DISPENSING_LOCATION, Arrays.asList(
            Locations.WOMENS_AND_CHILDRENS_PHARMACY,
            Locations.OUTPATIENT_CLINIC_PHARMACY,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI
        ));

        setLocationTagsFor(locationService, LocationTags.APPOINTMENT_LOCATION, Arrays.asList(
            Locations.CHEMOTHERAPY,
            Locations.OUTPATIENT_CLINIC,
            Locations.WOMENS_CLINIC,
            Locations.MAIN_LABORATORY,
            Locations.WOMENS_OUTPATIENT_LABORATORY,
            Locations.COMMUNITY_HEALTH,
            Locations.DENTAL,
            Locations.FAMILY_PLANNING,
            Locations.WOMENS_AND_CHILDRENS_PHARMACY,
            Locations.RADIOLOGY,
            Locations.OUTPATIENT_CLINIC_PHARMACY,
            Locations.CDI_KLINIK_EKSTEN_JENERAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.VITALS_LOCATION, Arrays.asList(
            Locations.ANTEPARTUM_WARD,
            Locations.POSTPARTUM_WARD,
            Locations.WOMENS_CLINIC,
            Locations.EMERGENCY,
            Locations.PRE_OP_PACU,
            Locations.OUTPATIENT_CLINIC,
            Locations.DENTAL,
            Locations.MENS_INTERNAL_MEDICINE,
            Locations.MENS_INTERNAL_MEDICINE_A,
            Locations.MENS_INTERNAL_MEDICINE_B,
            Locations.WOMENS_INTERNAL_MEDICINE,
            Locations.WOMENS_INTERNAL_MEDICINE_A,
            Locations.WOMENS_INTERNAL_MEDICINE_B,
            Locations.PEDIATRICS,
            Locations.PEDIATRICS_A,
            Locations.PEDIATRICS_B,
            Locations.SURGICAL_WARD,
            Locations.POST_OP_GYN,
            Locations.COMMUNITY_HEALTH,
            Locations.NICU,
            Locations.OPERATING_ROOMS,
            Locations.WOMENS_TRIAGE,
            Locations.ICU,
            Locations.LABOR_AND_DELIVERY,
            Locations.CHEMOTHERAPY,
            Locations.REHABILITATION,
            Locations.CDI_KLINIK_EKSTEN_JENERAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.INPATIENTS_APP_LOCATION, Arrays.asList(
            Locations.ANTEPARTUM_WARD,
            Locations.MAIN_LABORATORY,
            Locations.POSTPARTUM_WARD,
            Locations.WOMENS_CLINIC,
            Locations.RADIOLOGY,
            Locations.EMERGENCY,
            Locations.PRE_OP_PACU,
            Locations.OUTPATIENT_CLINIC,
            Locations.ISOLATION,
            Locations.DENTAL,
            Locations.MENS_INTERNAL_MEDICINE,
            Locations.MENS_INTERNAL_MEDICINE_A,
            Locations.MENS_INTERNAL_MEDICINE_B,
            Locations.WOMENS_INTERNAL_MEDICINE,
            Locations.WOMENS_INTERNAL_MEDICINE_A,
            Locations.WOMENS_INTERNAL_MEDICINE_B,
            Locations.PEDIATRICS,
            Locations.PEDIATRICS_A,
            Locations.PEDIATRICS_B,
            Locations.SURGICAL_WARD,
            Locations.POST_OP_GYN,
            Locations.COMMUNITY_HEALTH,
            Locations.NICU,
            Locations.CLINIC_REGISTRATION,
            Locations.OPERATING_ROOMS,
            Locations.WOMENS_TRIAGE,
            Locations.ICU,
            Locations.LABOR_AND_DELIVERY,
            Locations.CENTRAL_ARCHIVES,
            Locations.WOMENS_OUTPATIENT_LABORATORY,
            Locations.EMERGENCY_DEPARTMENT_RECEPTION,
            Locations.CHEMOTHERAPY,
            Locations.BLOOD_BANK,
            Locations.REHABILITATION,
            Locations.FAMILY_PLANNING,
            Locations.CDI_KLINIK_EKSTEN_JENERAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.CHECKIN_LOCATION, Arrays.asList(
            Locations.WOMENS_CLINIC,
            Locations.EMERGENCY_DEPARTMENT_RECEPTION,
            Locations.PRE_OP_PACU,
            Locations.OUTPATIENT_CLINIC,
            Locations.DENTAL,
            Locations.SURGICAL_WARD,
            Locations.COMMUNITY_HEALTH,
            Locations.WOMENS_TRIAGE,
            Locations.CHEMOTHERAPY,
            Locations.POSTPARTUM_WARD,
            Locations.CDI_KLINIK_EKSTEN_JENERAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.REGISTRATION_LOCATION, Arrays.asList(
            Locations.COMMUNITY_HEALTH,
            Locations.CLINIC_REGISTRATION,
            Locations.CENTRAL_ARCHIVES,
            Locations.EMERGENCY_DEPARTMENT_RECEPTION,
            Locations.POSTPARTUM_WARD,
            Locations.CDI_KLINIK_EKSTEN_JENERAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.ED_REGISTRATION_LOCATION, Arrays.asList(
            Locations.EMERGENCY_DEPARTMENT_RECEPTION,
            Locations.PRE_OP_PACU,
            Locations.OUTPATIENT_CLINIC,
            Locations.DENTAL,
            Locations.SURGICAL_WARD,
            Locations.POST_OP_GYN,
            Locations.COMMUNITY_HEALTH,
            Locations.CDI_KLINIK_EKSTEN_JENERAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));

        setLocationTagsFor(locationService, LocationTags.ORDER_RADIOLOGY_STUDY_LOCATION, Arrays.asList(
            Locations.ANTEPARTUM_WARD,
            Locations.POSTPARTUM_WARD,
            Locations.WOMENS_CLINIC,
            Locations.PRE_OP_PACU,
            Locations.OUTPATIENT_CLINIC,
            Locations.RADIOLOGY,
            Locations.EMERGENCY,
            Locations.ISOLATION,
            Locations.DENTAL,
            Locations.MENS_INTERNAL_MEDICINE,
            Locations.MENS_INTERNAL_MEDICINE_A,
            Locations.MENS_INTERNAL_MEDICINE_B,
            Locations.WOMENS_INTERNAL_MEDICINE,
            Locations.WOMENS_INTERNAL_MEDICINE_A,
            Locations.WOMENS_INTERNAL_MEDICINE_B,
            Locations.PEDIATRICS,
            Locations.PEDIATRICS_A,
            Locations.PEDIATRICS_B,
            Locations.SURGICAL_WARD,
            Locations.POST_OP_GYN,
            Locations.COMMUNITY_HEALTH,
            Locations.NICU,
            Locations.OPERATING_ROOMS,
            Locations.WOMENS_TRIAGE,
            Locations.ICU,
            Locations.LABOR_AND_DELIVERY,
            Locations.CHEMOTHERAPY,
            Locations.REHABILITATION,
            Locations.FAMILY_PLANNING,
            Locations.CDI_KLINIK_EKSTEN_JENERAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_ACHIV_SANTRAL,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_BIWO_RANDEVOU,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_FAMASI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_LABORATWA,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_RADYOGRAFI,
            Locations.CDI_KLINIK_EKSTEN_JENERAL_SAL_PWOSEDI
        ));


    }

    private static void setLocationTagsFor(LocationService service, LocationTagDescriptor locationTag, Collection<LocationDescriptor> locationsThatGetTag) {

        LocationTag tag = service.getLocationTagByUuid(locationTag.uuid());

        for (Location candidate : service.getAllLocations()) {
            boolean expected = false;
            if (locationsThatGetTag != null) {
                for (LocationDescriptor d : locationsThatGetTag) {
                    if (d != null && d.uuid().equals(candidate.getUuid())) {
                        expected = true;
                    }
                }
            }
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

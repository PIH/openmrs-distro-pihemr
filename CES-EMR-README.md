# CES EMR

This is the implementer documentation for CES EMR, the EMR used by
PIH Mexico / Compañeros en Salud. Here is documented the configuration used
by CES EMR, with some contextual information about the decisions involved.

CES EMR is integrated into openmrs-module-mirebalais. Code supporting
it can be found distributed throughout
[this repository](https://github.com/PIH/openmrs-module-mirebalais).

I will refer periodically to the MoH requirements, which can be found
[here (in Spanish only)](http://dof.gob.mx/nota_detalle.php?codigo=5280847&fecha=30/11/2012).

## Context

The CES office is located in Jaltenango de la Paz, Chiapas, Mexico. It is
a small town in the poorest state in the country. Electricity and internet
these days (2019) are pretty reliable in the dry season (November-April),
somewhat less so during the wet season (May-October). During the wet season
there's also a lot of lightning.

CES runs 11 clinics in ten "communities." The closest is about a 40 minute
drive, the furthest about 4 hours. They all get very little and very
intermittent internet service over 3G. It's enough that WhatsApp messages
typically get across the wire within a day or so, provided they don't have
pictures or anything fancy. Once a month, the doctors come down to
Jaltenango for a weekend of workshops and meetings, called Curso. They bring
their EMR data with them on flash drives to be copied onto a drive in
Jaltenango.

CES also runs a birthing center called Casa Materna. It's located here in
Jaltenango, at the hospital across town. There is no EMR support for them
yet, though they would like it.

CES also runs the surgical center at the hospital. This program started
in Feb 2019. There is no OpenMRS support for them yet, though they would
like it.

CES has a program called Right to Healthcare (Referencías), which helps get
patients to higher levels of care when necessary. They keep track of
everything with Excel spreadsheets.

CES focuses on NCDs. Each NCD is considered its own "program." Some of these
programs, namely MCH and Mental, have staff that focus on them in particular.
The other NCD programs are mostly just cohorts of patients, for practical
purposes. Not every patient that ever comes in with diabetes will be part
of the diabetes program, however—patients that come in as one-offs, with no
intention of longitudinal care with CES clinics, will not be enrolled.

The NCD programs that CES runs are
- MCH
- Mental
- Diabetes
- Epilepsy
- Hypertension
- Malnutrition

CES also has a community health worker (Acompañantes) program. As of this
writing (March 2019) it is using CommCare, but it is presently undergoing
a sea change, so little can be said about it that will be true in 2020. As
regards the EMR, CHWs are connected to the patients they are assigned to
via Provider Relationships.

CES also has an active casefinding (Búsqueda Activa) program. They use CommCare.
Whether or not a patient was found through active casefinding is noted in
the EMR during patient registration.

Each community has a doctor. One doctor, the one for Plan de la Libertad, works
two clinics. Each doctor stays for one year. Some of them have nurses who do
simultaneous consults (mostly just taking vitals, but sometimes doing simple
consults, giving injections etc.) in the front room.

Each clinic has a
pharmacy attached, and drugs are given to patients for free as part of
consults. Drugs either come from MoH or our own supply.

CES has a few tens of thousands of patients, with records going back to 2013,
when it started using a MS Access based EMR.

The Ministry of Health has a plethora of forms they require doctors to submit
about their patients. All are seen as beaurocratic hinderence, and few obtain
quality data. There are two pieces of software that the MoH requires doctors to
use to submit patient data. One is called SINBA and one is called SIS. SINBA
can theoretically run on Linux or from a server, but no one in Chiapas has
figured out how. SIS is a Windows program. Niether has any sort of interop
system that might allow an organization with an EMR to avoid using their
software (without reverse-engineering the protocols used by that software,
at least).

## Configuration

### Registration

#### Address

Currently, the address is just a coded "Community" (cityVillage), a free-text
"Community (if other)", and a free-text "Address" (address1). This is what is
sufficient for CES internally.

We will need to make it more complicated to satisfy MoH requirements. See
[MEX-86: Address should have INEGI fields](https://tickets.pih-emr.org/browse/MEX-86).

#### Insurance

Under "Insurance" we include participation in a welfare program called Prospera 
in addition to the person's actual health insurance.

#### Attributes of the patient (that are stored as concepts)

There are a number of boolean (actually coded yes/no) questions we ask about
each patient. Some of these are required by MoH (though not in the requirements
document linked above).

### Forms

CES has two forms: one for vitals and one for the main consult. Ideally, nurses
will have computers that they can use to conduct simultaneous consults.

#### Vitals

The vitals form is more or less what you'd expect. It includes glucose because
the nurses screen glucose. It includes head circumference for children under 3.

#### Consult

The consult form is laid out in a SOAP format, which is what the doctors are used
to.

Subjective, at the top, is free text.

Objective is the meat of the form, which includes all the sections for each NCD
program. Ideally, for each NCD form section, it would either show the form
section or a corresponding screening section, depending on whether or not the
patient is enrolled in the program.

Analysis is the diagnosis section. Our diagnoses will be a curated subset of the
Spanish version of the WHO ICD-10 provided by MoH.

Plan is where drugs and treatment plans are specified. An entered drug represents a
dispensation from the clinic pharmacy. This data will be used to
produce the pharmacy report.

### Programs

Each NCD program has a corresponding OpenMRS program. The notion of "Enrollment"
corresponds between them. Each program has a dashboard displaying clinically useful
tables and graphs.

### Reports

We intend to use the Reporting UI module so that MEQ staff and volunteers can
configure reports.

A handful of reports need to be produced. These are documented in
[Google Drive: Reports](https://docs.google.com/spreadsheets/d/1KvKtUaiQ-itWtEfAcYGzgOXycdHQjLrH_wdpR-4VdjU/edit?usp=sharing).

There one cross-community report that CES requires of itself, one for PIH, and one 
for beneficiaries. There are a few per-community reports required by MoH (the ones
that are not yet produced using the MoH's software), as well as the pharmacy
report, which is used to track and resupply clinic stock.

## Deployment

As of March 2019, the plan is to deploy a laptop to each clinic. The laptops will
not actually be used by doctors; rather, they will function as servers, sitting
in a corner of the clinic serving OpenMRS.

The reasons for using laptops as servers (instead of MiniPCs) are

1. Built-in, high-capacity, long-lived UPS (i.e., the battery)
1. Built-in monitor and peripherals for when debugging is necessary
1. Easy to source
1. Less intimidating

Doctors and nurses will connect to these servers over wireless LAN from their
computers, which will either be the ones they own or ones CES provides. Devices
will connect to the server using zeroconf/Avahi/Bonjour.

Eventually there should be laptop-servers in the birthing and surgical centers as
well.

### "Sync"

The priorities for moving general clinical EMR data around are as follows:

1. That data can be carried on flash drives to Jaltenango once a month and
copied onto a central server
1. That data from different clinics on the central server be merged
1. That the final merged data is sent back to clinics
1. That data can be synced over the extremely slow internet during those rare
moments when it exists at all

Priorities around data from the other programs include that
1. Data from the birthing and surgical centers makes it back up to the patient's
home clinic as soon as possible
1. Data from the birthing and surgical centers, which are in Jaltenango, can
be sent over the internet to the office

Most of these we intend to address using the Sync 2.0 module.

The matter of getting
data urgently from birthing and surgical centers back up to community will be
addressed by sending textual information about the patient (encrypted) over
WhatsApp, by which means it is likely to arrive within a day or so. Text will
probably be copied back and forth between EMR forms and text files for this
purpose.

#### Mobile Health

Up until now, the CHW and Active Casefinding programs have used CommCare. This
has generated interest in being able to sync clinical data between CommCare and
the EMR. The priorities here are

- When a patient is assigned to a CHW in OpenMRS, it will appear in that CHW's CommCare
- Clinical data entered in CommCare gets merged into OpenMRS


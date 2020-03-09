
# Overview of the PIH-EMR

The *PIH-EMR* is a *distribution* of *OpenMRS*, an open-source medical record systems

A few links from the OpenMRS wiki that are worth reading:

* High-level overview of OpenMRS: https://wiki.openmrs.org/display/docs/Introduction+to+OpenMRS

* Overview of the OpenMRS data model: https://wiki.openmrs.org/display/docs/Data+Model

* Definition of an "OpenMRS distribution" (of which the PIH-EMR is one): https://wiki.openmrs.org/display/docs/OpenMRS+Distributions


Therep are 40+ OpenMRS modules that make up the PIH EMR, of which most are OpenMRS community modules, but we also have
several PIH-specific modules as well.  Of those there are four top-level modules that provide most of the PIH-specific configuration:

* mirebalais: https://github.com/PIH/openmrs-module-mirebalais

* mirebalaismetadata: https://github.com/PIH/openmrs-module-mirebalaismetadata

* mirebalaisreports: https://github.com/PIH/openmrs-module-mirebalaisreports

* pihcore: https://github.com/PIH/openmrs-module-pihcore


These modules do several things, but their main tasks are:

* Providing and setting up PIH-specific metadata (like the forms and concepts we use)

* Configuring exactly what modules are included in the PIH distribution
(see https://github.com/PIH/openmrs-module-mirebalais/blob/master/pom.xml#L53)

* Allow use to turn and off different functionality based on country and location

* Setting up PIH-specific reports


(Due to the organic way the PIH EMR developed, it's there's not always clear guidelines as to the distribution of
functionality between the the four modules, but generally Mirebalais metadata provides concepts, Mirebalais reports
provides reports, and Mirebalais and PIH Core provide other metadata and configuration. The mirebalais module runs at
the top of stack. For reference, "Mirebalais" refers to Hopital Universitaire Mirebalais where the PIH EMR was first installed.)

For deploying the PIH-EMR to our various staging and production servers, we use Puppet (https://puppet.com/)

Our Puppet configuration scripts can be found here: https://github.com/PIH/mirebalais-puppet

### Sites Supported

This repository/implementation supports several different sites.

- Haiti
  - Mirebalais Hospital / CDI configuration
  - Default Health Center configuration
  - Mental Health laptop configuration
  - HIV cloud system (coming soon!)

- Liberia
  - Pleebo Health Center
  - JJ Dossen Health Center

- Sierra Leone
  - Wellbody Health Center

- Mexico
  - See the [CES EMR documentation](CES-EMR-README.md)



# Communications and management

There are a few tools that we use extensively and that all PIH devs should have set up:

* Telegram for project-specific group chats (https://telegram.org/)

Please install Telegram on your development machine and then ask an existing developer to invite you to the appropriate groups.

* JIRA for managing project, bugs, sprints, etc: https://pihemr.atlassian.net/secure/Dashboard.jspa

Please request an account by asking another PIH developer or emailing medinfo@pih.org

# Setting up a Dev Environment

A development environment can be set up with the OpenMRS SDK, with some 
custom configuration steps, as written below.

Setup can also be done using the 
[PIH EMR Invoke file](https://github.com/PIH/pih-emr-invoke),
for which the instructions are in that README.

## Prerequisites

First, install git, mvn, and the OpenMRS SDK by following the "Installation" instructions here:

https://wiki.openmrs.org/display/docs/OpenMRS+SDK#OpenMRSSDK-Installation

The OpenMRS SDK uses the H2 database by default, but H2 doesn't work with some of the
modules we use, so we must use MySQL, which needs to be configured separately. To do this, you can either
install MySQL directly on your machine, or install mysql within a docker container.

If installing directly, install MySQL Community Server 5.6 following the instructions for your platform. It must be
version 5.6, other versions will not work.

An easier approach is likely to install Docker (https://www.docker.com/) and use the OpenMRS SDK to set up an instance of MySQL within a docker container.


## Setup

Epic for making setting up a dev enviorment easier: https://pihemr.atlassian.net/browse/UHM-4245


### Step 1: (If you are directly installing MySQL on your machine) Ensure that MySQL has a password set up for the root user.
- If you are able to run ```$ mysql -u root``` and access the MySQL Monitor without receiving an access denied error,
it means that there is no root password set and you have to set it following the instructions here: https://dev.mysql.com/doc/refman/5.6/en/resetting-permissions.html
- Once the root password has been set, you should be able to access the MySQL Monitor by running:  
  ```$ mysql -u root -p``` followed by entering the password when prompted.


### Step 2: Set up the environment
Set up the environment via the following command, choosing the serverId and dbName you want to use. Specify
the DB password for your root user as set in Step 2.

The **Application Data Directory** will be set up at `~/openmrs/[serverId]`.

The convention for dbNames are "openmrs_[some name]".

```
$ mvn openmrs-sdk:setup -DserverId=[serverId] -Ddistro=org.openmrs.module:mirebalais:1.3.0-SNAPSHOT
```

* When prompted, select the port you'd like to run tomcat on

* When prompted, set the port to debug on (standard is 1044)

* For database selection, select either the option to use locally-installed MySQL, or to use a MySQL docker container.
  Option "3. Use an existing docker container," does not seem to work at the time of this writing. Your
  `serverId` must only contain
  [MySQL Permitted Characters in Unquoted Identifiers](https://dev.mysql.com/doc/refman/8.0/en/identifiers.html).

* If you are connecting to a MySQL 5.6 instance running on your local machine, specify the URI and a username and password to connect to the DB

* Select the JDK to use (it must be 1.8)

### Step 3: Clone the configuration project for the distro you are working
The various configuration files that determine what applications and options are turned on on different servers are
found here. The configuration distro projects are as follows:

|Site|Repo  |
|---|---|
|CES|https://github.com/PIH/openmrs-config-ces|
|Liberia|https://github.com/PIH/openmrs-config-pihliberia|
|SES|https://github.com/PIH/openmrs-config-ses|
|Sierra Leone|https://github.com/PIH/openmrs-config-pihsl|
|ZL|https://github.com/PIH/openmrs-config-zl|

For instance, for the Liberia configuration the command is:

```
git clone https://github.com/PIH/openmrs-config-pihliberia.git
```

### Step 4: Compile the configuration project and install it
You'll need to use the
[OpenMRS Packager Maven plug-in](https://github.com/PIH/openmrs-packager-maven-plugin)
to assemble the configuration and install it in the application
data directory associated with the server you created in Step 2

Go into the top-level directory of the configuration project you checked out above and run the Maven Plugin.

Use the "serverId" you choose in Step 1

```
cd openmrs-config-liberia
mvn clean compile -DserverId=pihliberia
```


### Step 5: Start up the server

```
mvn openmrs-sdk:run -DserverId=[serverId]
```
It should run for several minutes (like potentially 15 to 30 minutes), setting up the database,
(you will likely have to go to http://localhost:8080/openmrs to trigger this).  

At the end you should have a running PIH EMR instance.

### Step 6: Tweak the configuration

At this point, the configuration will be set up with the 'default' configuration for your chosen distro.

You will likely want to update the configuration for a specific server within that distro.

A openmrs-runtime.properties file should have been created in `~/openmrs/[serverId]`.

You will need to add a line to this file specifying which config to use for this server.

For instance, if you want to set up the Mirebalais CI environment, add the following into the runtime properties:

```
pih.config=mirebalais,mirebalais-humci
```

Then re-run
```
mvn openmrs-sdk:run -DserverId=[serverId]
```

### Step 7: Create a local identifier source

**This is only required for some sites.**

After startup, login
- Enter "http://localhost:8080/openmrs/login.htm" into the Chrome web browser
  - Log in with the following details:
    - Username: admin
    - Password: Admin123
  - (The password is the default password, it is referenced in the openmrs-server.properties file within the ~/openmrs/[serverId] folder)
- Enter the legacy admin page "http://localhost:8080/openmrs/admin"
- Go to "Manage Patient Identifier Sources" under the header "Patients"

Check if there is an existing source for "ZL EMR ID" with type "Local Identifier Generator." 
If there isn't, you'll need to create a local identifier source to generate "fake" ZL EMR IDs:
- Add a new "Local Identifier Generator" for the "ZL EMR ID" with the following settings:
  - Name: ZL Identifier Generator
  - Base Character Set: ACDEFGHJKLMNPRTUVWXY1234567890
  - First Identifier Base: 1000
  - Prefix: Y
  - Suffix: (Leave Blank)
  - Max Length: 6
  - Min Length: 6
- Link the local generator to the Local Pool of Zl Identifiers
  - Click the Configure Action next to the local pool
  - Set "Pool Identifier Source" to "ZL Identifier Generator"
  - Change "When to fill" to "When you request an identifier"
  
## Set up the Single-SPA Frontend

To develop on Microfrontends, you'll need to do some set-up. Follow the instructions in the
[PIH SPA Frontend README](https://github.com/PIH/spa-frontend#pih-emr-spa-frontend).

# Updating the Configuration for your Distribution

 We are in the process of moving as much "configuration" out of the main PIH EMR code base and into distribution-specific files.  These files can be updated without updating the main PIH EMR code base.

To do this, you'll need to work with two key PIH repos.  First, the "openmrs-config-pihemr" repo:

https://github.com/PIH/openmrs-config-pihemr

This is the "parent" configuration, that contains all configuration shared across all PIH EMR distributions.

Then there is the specific "child" configuration for each distribution.  See Step 3 in "setting up a dev environment" above for the list of repos.

When you make changes to the "child" repo, you need to compile the changes and then "deploy" them to the SDK server you are working on.  This can be done with the following command:

```
mvn clean compile -DserverId=[serverId]
```

When you make changes to the "parent" repo, you need to compile that project and install it to your local Maven repo, and then compile and deploy the child repo.  So from the parent project you would run:

```
mvn clean install
```

And then once that is complete, compile and "deploy" the child project locally:

```
mvn clean compile -DserverId=[serverId]
```

Note if you make changes to metadata installed via Initializer, you will need to restart your server to pick up the changes.  However, HTML Forms should be available to be "hot" reloaded... once you run the mvn commands above, doing a "reload" of a page should reload the form with your changes.

You also can set up a "watch" on your child project, so that when you make changes to, say, an
HTML Form, the project is immediately compiled and deployed.  You do so using the "watch" flag:

```
mvn clean compile -DserverId=[serverId] -Dwatch
```

Note that there currently isn't "watch" support for the parent project.  You *can* watch the parent project, but the results will likely not be what you are looking for and should be avoided for now.

Also note that if you commit any changes to either the parent or child config property, our CI server should immediately push them out to the relevant staging servers and restart OpenMRS, so, all going well, your changes should be up on the staging servers within 10-15 minutes of pushing your changes.

# Updating the PIH EMR Code

Beyond updating the configuration for your specific distribution you will also want to update the core PIH EMR code, which you can do as follows:

If you are watching any modules, first execute "mvn openmrs-sdk:pull" to pull in any changes to these modules via git.

Then, from the base directory of the mirebalais project run the following two commands to update any changes to modules
you aren't watching:

```
$ git pull
$ mvn openmrs-sdk:deploy -Ddistro=api/src/main/resources/openmrs-distro.properties -U
```

Note that some of these steps will be repeated often enough that they warrant 
creating shortcuts. In particular, we have created a shell script shortcut to execute the two 
commands above, pihemrDeploy.sh) See [Making things easy](#making-things-easy).


To run the server: 
```
$ mvn openmrs-sdk:run
```


# Configuring functionality in a PIH EMR OpenMRS Instance


### Registration Summary Dashboard

The RegistrationApp seems to provide, by default, a single widget, which displays the information in the "demographics" section. It only will display patient attributes - concept/observation data added to the demographics section will always show a blank answer in the dashboard widget.

RegistrationApp is configured in `mirebalais/.../apps/patientregistration/`. `SectionsDefault` provides the default Registration application. Some of it is configurable using the site configuration JSON, [mirebalais-puppet/.../pih-config-<site>.json](https://github.com/PIH/mirebalais-puppet/tree/master/mirebalais-modules/openmrs/files/config). Parts of it can be overridden in the site-specific Sections file, e.g. `SectionsMexico.java`.
  
Person Attributes should be added to the section with id `demographics`. Other registration components should be added elsewhere. For these, you will also need to edit the corresponding registration form section, `pihcore/.../htmlforms/patientRegistration-<section>.xml`. This XML file is what is used by the registration dashboard to configure the "view" widget, as well as the "edit registration" forms.

## Diagnoses

In OpenMRS, the list of diagnoses to use is the set of all diagnosis concepts contained
in the concept sets of diagnoses contained in the set of sets of diagnoses named by
the global property `emr.concept.diagnosisSetOfSets`. Read that carefully.

To break it down a bit:
1. Create a concept on the Concepts server called something like "MySite primary care diagnosis set" or simply "MySite diagnosis".
1. Set this concept to be a ConvSet (with datatype NA), and check that it is a set.
1. Add all the diagnoses you want to it.
1. Create a concept on the Concepts server called something like "MySite diagnosis set of sets"
1. Set this concept to be a ConvSet (with datatype NA), and check that it is a set.
1. Add "MySite primary care diagnosis set" (or whatever you called the other concept) to it.
1. Export a new version of the MDS package "HUM Clinical Concepts", adding "MySite diagnosis set of sets" to it.
1. Update that MDS package in mirebalaismetadata.
1. Add to [pihcore/.../GlobalPropertiesBundle](https://github.com/PIH/openmrs-module-pihcore/blob/master/api/src/main/java/org/openmrs/module/pihcore/deploy/bundle/core/GlobalPropertiesBundle.java) a line resembling `public static final String LIBERIA_DIAGNOSIS_SET_OF_SETS = "ed97232b-1a09-4260-b06c-d193107c32a7";`, but with your site name and the UUID of your "MySite diagnosis set of sets" concept.
1. Add to your site's MetadataBundle file a line like `properties.put(EmrApiConstants.GP_DIAGNOSIS_SET_OF_SETS, GlobalPropertiesBundle.Concepts.LIBERIA_DIAGNOSIS_SET_OF_SETS);`, but with the constant that you just added to GlobalPropertiesBundle. See for example [pihcore/.../LiberiaMetadataBundle](https://github.com/PIH/openmrs-module-pihcore/blob/master/api/src/main/java/org/openmrs/module/pihcore/deploy/bundle/liberia/LiberiaMetadataBundle.java).

## Registration Form

The Registration form is produced by RegistrationApp based on the configuration specified in [mirebalais/apploader/apps.patientregistration/](https://github.com/PIH/openmrs-module-mirebalais/tree/master/api/src/main/java/org/openmrs/module/mirebalais/apploader/apps/patientregistration). This also generates some of the Edit Registration forms, but not all of them. RegistrationApp is able to provide View and Edit UI for sections that do not have concept questions. For sections with concept questions, you will need to create a .xml file (like patientRegistration-contact.xml) to define those views.

## Programs

### Enabling a Program

There are a number of programs that come with PIH EMR. Each one has its own component. To enable a program:
- Enable the component by adding it to `mirebalais-puppet/.../pih-config-mycountry.json`.
- Ensure the program bundle will be loaded -- check the `@Requires` section of `pihcore/.../MyCountryMetadataToInstallAfterConceptsBundle.java`. If there is no such bundle for your country, you will need to create one.
    - If you create a `MyCountryMetadataToInstallAfterConceptsBundle`, you will need to call it from `openmrs-module-mirebalaismetadata/.../MirebalaisMetadataActivator.java` in the function `installMetadataBundles`.
- Ensure you have the concepts required by the program. These are listed in `openmrs-module-pihcore/.../PihCoreConstants.java`. Please see the [MirebalaisMetadata README](https://github.com/PIH/openmrs-module-mirebalaismetadata/blob/master/README.md) for information about concept management.

### Creating a program

1. Create a concept for your program (see the [MirebalaisMetadata README](https://github.com/PIH/openmrs-module-mirebalaismetadata/blob/master/README.md)
1. Add your new concept, as well as an outcomes concept (the default is fine for most things) to `pihcore/.../PihCoreConstants.java`.
1. Create a program definition based on `pihcore/.../MentalHealthProgram.java`.
    1. Be sure to change the name, description, concepts, and UUID.
1. Create a program bundle based on `pihcore/.../MentalHealthProgramBundle.java`.
1. Add the bundle to your `mirebalaismetadata/.../MyCountryMetadataToInstallAfterConceptsBundle.java` file (see above, "Enabling a program")
1. Create a component for your program by adding a line to `pihcore/.../Components.java`.
1. Document it with the bundle name, your implementation name, and the MDS package name if it's not HUM NCD.
1. Add a block to CALF (`mirebalais/.../CustomAppLoaderFactory.java`) in the `enablePrograms` function. It should have two lines, one call to `supportedPrograms` and one call to `configureBasicProgramDashboard`.
1. Enable the component by adding it to `mirebalais-puppet/.../pih-config-mycountry.json`.


## Locales

Language specific settings are configured in these places:

1. Translation for concepts will be done via the OpenMRS Dictionary UI (do this locally for now, eventually this will be done on the staging server companero.pih-emr.org, and concepts will be packaged using [Metadata Sharing](https://drive.google.com/open?id=1W_83FHL5dB2i9740Zp_n7iMjzRaqSpUb0y-RoFZspo0))

2. OpenMRS settings, allowed locales example: [https://github.com/PIH/openmrs-module-pihcore/blob/master/api/src/main/java/org/openmrs/module/pihcore/deploy/bundle/liberia/LiberiaMetadataBundle.java#L28](https://github.com/PIH/openmrs-module-pihcore/blob/master/api/src/main/java/org/openmrs/module/pihcore/deploy/bundle/liberia/LiberiaMetadataBundle.java#L28)

3. Locale specific resource files, e.g.,

    1. [https://github.com/PIH/openmrs-module-pihcore/blob/master/api/src/main/resources/messages_fr.properties](https://github.com/PIH/openmrs-module-pihcore/blob/master/api/src/main/resources/messages_fr.properties)

    2. [https://github.com/PIH/openmrs-module-mirebalais/blob/master/api/src/main/resources/messages_ht.properties](https://github.com/PIH/openmrs-module-mirebalais/blob/master/api/src/main/resources/messages_ht.properties) 

## Localized String Management

Transifex is used for managing translations. To add new strings, add to the messages.properties file for that project ([mirebalais example](https://github.com/PIH/openmrs-module-mirebalais/blob/master/api/src/main/resources/messages.properties)). The localized strings are pulled from Transifex and committed into mirebalais_*.properties files.

Join the "PIH EMR" project on transifex: [https://www.transifex.com](https://www.transifex.com)

Using transifex for locale specific strings: [https://wiki.openmrs.org/display/docs/Localization+and+Languages](https://wiki.openmrs.org/display/docs/Localization+and+Languages)

Notes from Mark Goodrich:

However, we will start needing to create messages_es.properties files for Spanish translating and getting a translator working on them. I don't think any of the modules that PIH owns will have Spanish translations, though the OpenMRS ones may.
You can see a list of the modules that PIH owns and has in Transifex here (Dominic I just sent you a Transifex invite):
[https://www.transifex.com/pih/mirebalais/content/](https://www.transifex.com/pih/mirebalais/content/)


For each of these modules (with the exception of the legacy Patient Registration module) we should add the messages_es.properties. (Priority can be given to the main modules like PIH Core, Mirebalais Reports, Mirebalais... we won't need translations in Lab Tracking or ED Triage unless we plan on using that functionality in Mexico)

Steps to add a messages_es.properties:
1) Create the messages_es.properties file in the resources directory of the api project of the module:
[https://github.com/PIH/openmrs-module-mirebalais/tree/master/api/src/main/resources](https://github.com/PIH/openmrs-module-mirebalais/tree/master/api/src/main/resources)


2) Add the locale "es" to the config.xml of the module:
[https://github.com/PIH/openmrs-module-mirebalais/blob/master/omod/src/main/resources/config.xml#L138](https://github.com/PIH/openmrs-module-mirebalais/blob/master/omod/src/main/resources/config.xml#L138)


3) Add the locale "es" to the Transifex configuration file for the module:
[https://github.com/PIH/openmrs-module-mirebalais/blob/master/.tx/config](https://github.com/PIH/openmrs-module-mirebalais/blob/master/.tx/config)

Of course, after we do this, we need to start having translators edit translations and pull them into the projects.

Documentation can be found here:
https://wiki.openmrs.org/display/docs/Maintaining+OpenMRS+Module+Translations+via+Transifex
(In particular see the section 'Updating A Module With New Translations" and how to install the Transifex command line client).


## Making Things Easy

### Aliases

(You might be able to set the "debug" flag at project creation time now. 
`pihemrDeploy.sh` is a utility script created by Mark Goodrich.)

```
$ alias omrs-pull='mvn openmrs-sdk:pull'
$ alias omrs-deploy='cd /home/mgoodrich/openmrs/modules/mirebalais && ./pihemrDeploy.sh'
$ alias omrs-run='mvn openmrs-sdk:run -Ddebug'
```

So to do a daily update of the system, run:

```
$ omrs-pull
$ omrs-deploy
$ omrs-run
```

### PyInvoke

There's an [Invoke](http://www.pyinvoke.org/) file for doing local development of the PIH EMR
[here](https://github.com/PIH/pih-emr-invoke).
Feel free to make pull requests.

### Troubleshooting

#### I'm getting a NullPointerException from HeaderFragmentController immediately on navigating to the EMR

Something like
```
UI Framework Error
Root Error
java.lang.NullPointerException
	at org.openmrs.module.appui.fragment.controller.HeaderFragmentController.controller(HeaderFragmentController.java:47)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
...
```

What the error means, interpreted literally, is that the Mirebalais module is
overriding the header extension, but didn't successfully initialize the override.
There are two likely reasons:

1. Mirebalais Module is activating correctly, but the code is wrong, in that it's
   actually not setting up the header extension. Check the code in the [CALF](https://github.com/PIH/openmrs-module-mirebalais/blob/b1fef4a6d523ee21c250a5a4c3326018938badda/api/src/main/java/org/openmrs/module/mirebalais/apploader/CustomAppLoaderFactory.java#L350) to see if it's setting up a header extension for
   your site. This is likely the reason if you're setting up a new site.
2. Something went wrong at some point in server initialization before the header
   extension could be set. Scroll up in the logs and look for an exception.

#### I'm getting an error "viewProvider mirebalais does not have a view named home" immediately on navigating to the EMR

Something like
```
ERROR - PageController.handlePath(155) |2020-01-15 09:23:35,714| 
org.openmrs.ui.framework.UiFrameworkException: viewProvider mirebalais does not have a view named home
...
```

The error is itself meaningless. Look up in the logs for an exception trace to find out what went wrong.

#### The server isn't reflecting the changes I'm making in code

Make sure that the module you're working on hasn't come un-watched. Look at the `watched.projects` line of `openmrs-server.properties` in the App Data directory.

#### After fixing an error while developing on a metadata bundle, the metadata still isn't updating

When a module fails to start, the next time OpenMRS runs it will not try to start it.
On realizing this has happened, you may be inclined to start it from the Admin UI. 
However, there's a problem. Metadata bundles don't load when starting the mirebalais module via the admin
UI on an already-running server. Therefore you must always make sure that your modules are enabled prior 
to running the server, especially after running into problems during initialization.

This can be accomplished by logging in to mysql and running
```
update global_property set property_value='true' where property like '%started%';
```

or by running `invoke enable-modules` if you're using [PyInvoke](https://github.com/brandones/pih-emr-workspace/blob/master/tasks.py).

#### OpenMRS displays errors like this after starting up, where `Foo` is the name of the module I'm working on:
#### `Foo Module cannot be started because it requires the following module(s): Bar 1.2.3-SNAPSHOT`

As of this writing, we use a lot of snapshots. One thing that can happen is that when you `openmrs-sdk:run`, Maven might pull a new snapshot version of some module (here, Foo), but its dependencies may have updated, coming out of sync with the Foo POM you have locally. So the snapshot version of Foo expects BAR 1.2.3-SNAPSHOT, but your local Foo POM still requires Bar 1.2.3.

The thing to do is to pull the latest changes to Foo from master (merging/rebasing into your branch if you're in a branch), and then run `openmrs-sdk:deploy` (or `./pihEmrDeploy.sh` or `invoke deploy`), then try running again.

#### Error about `com.mycila` when building core

If, when building core, you see an error like...

```
[ERROR] Failed to execute goal com.mycila:license-maven-plugin:3.0:check (default) on project openmrs-test: Execution default of goal com.mycila:license-maven-plugin:3.0:check failed: Cannot read header document license-header.txt. Cause: Resource license-header.txt not found in file system, classpath or URL: no protocol: license-header.txt -> [Help 1]
```

... then try commenting out the mycila plugin in the main pom of the project


# Source Code


All source code for OpenMRS is stored on Github, in either the OpenMRS organizaiton (for core and community modules) or 
the PIH organization (for PIH-specific modules).

https://github.com/openmrs
https://github.com/PIH

The naming convention for a module repo is "openmrs-module-[module_name]".

# Getting Set Up with IntelliJ


At least with `openmrs-module-pihcore`, IntelliJ may want to identify some 
directories as modules that are not modules,
and are in fact subdirectories of actual modules. The problem is that these phony modules don't inherit Maven
dependency information from the parent modules, so IntelliJ will fail to resolve references to those dependencies.
To fix this, go to Project Structure -> Modules and remove those directories.

Other than that, this project is more or less plug-and-play in IntelliJ.

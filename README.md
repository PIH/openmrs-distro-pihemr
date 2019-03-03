
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

- Haiti / Mirebalais Hospital: This is sort of the "default implementation" here

- Haiti / CDI

- Liberia

- Mexico: See the [CES EMR documentation](CES-EMR-README.md)

- Sierra Leone


# Communications and management

There are a few tools that we use extensively and that all PIH devs should have set up:

* Telegram for project-specific group chats (https://telegram.org/)

Please install Telegram on your development machine and then ask an existing developer to invite you to the appropriate groups.

* JIRA for managing project, bugs, sprints, etc: https://tickets.pih-emr.org/secure/Dashboard.jspa

Please request an account by asking another PIH developer or emailing medinfo@pih.org

# Setting up a Dev Environment

The preferred method to set up a development environment is using the OpenMRS SDK, with some custom configuration steps.

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


### Step 1: Clone the "mirebalais-puppet" project
The various configuration files that determine what applications and options are turned on on different servers are
found here and later on in process you will need to tell OpenMRS where to find them.

```
$ git clone https://github.com/PIH/mirebalais-puppet.git
```

### Step 2: (If you are directly installing MySQL on your machine) Ensure that MySQL has a password set up for the root user.
- If you are able to run ```$ mysql -u root``` and access the MySQL Monitor without receiving an access denied error,
it means that there is no root password set and you have to set it following the instructions here: https://dev.mysql.com/doc/refman/5.6/en/resetting-permissions.html
- Once the root password has been set, you should be able to access the MySQL Monitor by running:  
  ```$ mysql -u root -p``` followed by entering the password when prompted.  

### Step 3: Set up the environment
Set up the environment via the following command, chhosing the serverId and dbName you want to use. Specify
the DB password for your root user as set in Step 2.
*Note that the environment will be set up the directory ~/openmrs/[serverId]*
*The convention for dbNames are "openmrs_[some name]"*

```
$ mvn openmrs-sdk:setup -DserverId=[serverId] -Ddistro=org.openmrs.module:mirebalais:1.2-SNAPSHOT
```

* When prompted, select the port you'd like to run tomcat on

* When prompted, set the port to debug on (standard is 1044)

* Select which database you'd like to use

* If you are connecting to a MySQL 5.6 instance running on your local machine, specifc the URI, and a username and password to connect to the DB

* Select the JDK to use

### Step 4: Start up the server

```
$ mvn openmrs-sdk:run -DserverId=[serverId]
```

It should run for several minutes, setting up the database, (you may have to go to http://localhost:8080/openmrs to trigger this) BUT, in the end, it will fail.  You should cancel the current run (Ctrl-C in the terminal window).

After it fails, notice that a openmrs-runtime.properties file should have been created in the ~/openmrs/[serverId]

You will need to add two lines to these file, one specifying which of our configs to use for this server, and another
referencing the location of the config files (which you checked out as part of the mirebalais-puppet project above).
For instance, if you want to set up the Mirebalais CI environment, and you checked out the mirebalais puppet project
into your home directory, add the following into the runtime properties:

- pih.config=mirebalais,mirebalais-humci
- pih.config.dir=/[path to]/mirebalais-puppet/mirebalais-modules/openmrs/files/config

Then rerun:

```
$ mvn openmrs-sdk:run -DserverId=[serverId]
```

Startup should take several minutes as it loads in all required metadata, etc, for the first time.

### Step 5: Create a local identifier source
After startup, login
- Enter "http://localhost:8080/openmrs/login.htm" into the Chrome web browser
  - Log in with the following details:
    - Username: admin
    - Password: Admin123
  - (The password is the default password, it is referenced in the openmrs-server.properties file within the ~/openmrs/[serverId] folder)
- Enter the legacy admin page "http://localhost:8080/openmrs/admin"
- Go to "Manage Patient Identifier Sources" under the header "Patients"

Check if there is an existing local identifier source. If there isn't, you'll
need to create a local identifier source to generate "fake" ZL EMR IDs:
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
  
# Configuring functionality in a PIH EMR OpenMRS Instance

## Address

Address configuration happens in a few places.

First there is the Address Hierarchy module configuration, which manages the address hierarchy data in MySQL. This is done by extending [pihcore/.../AddressBundle](https://github.com/PIH/openmrs-module-pihcore/blob/0c0eb626f7da4be65fc02e60f92775af952bad6c/api/src/main/java/org/openmrs/module/pihcore/deploy/bundle/AddressBundle.java), e.g. into [pihcore/.../LiberiaAddressBundle](https://github.com/PIH/openmrs-module-pihcore/blob/3d18f1fec0c42dc8623b83cec3f3bbac76bae6dd/api/src/main/java/org/openmrs/module/pihcore/deploy/bundle/liberia/LiberiaAddressBundle.java).

Some things are not encoded in the data, and need to be pulled from the config at runtime. Right now these are the shortcut field and the manual fields. These are configured in the addressConfig tree in your config JSON file that lives in mirebalais-puppet. These options are used by [mirebalais/.../PatientRegistrationApp](https://github.com/PIH/openmrs-module-mirebalais/blob/8a565656ff335cd28dcb310c0b1c4de3dcd4d62f/api/src/main/java/org/openmrs/module/mirebalais/apploader/apps/PatientRegistrationApp.java). If you don’t provide this configuration, this file provides defaults.

## Apps & Components

The configuration for which components are enabled is in [mirebalais-puppet/.../pih-config-*.json](https://github.com/PIH/mirebalais-puppet/tree/master/mirebalais-modules/openmrs/files/config). Components are defined in [pihcore/.../config/Components.java](https://github.com/PIH/openmrs-module-pihcore/blob/master/api/src/main/java/org/openmrs/module/pihcore/config/Components.java). Based on these component selections (and often some other criteria) CALF ([mirebalais/.../CustomAppLoaderFactory.java](https://github.com/PIH/openmrs-module-mirebalais/blob/master/api/src/main/java/org/openmrs/module/mirebalais/apploader/CustomAppLoaderFactory.java)) loads apps and forms. Apps are defined in [mirebalais/.../CustomAppLoaderConstants.java](https://github.com/PIH/openmrs-module-mirebalais/blob/master/api/src/main/java/org/openmrs/module/mirebalais/apploader/CustomAppLoaderConstants.java). 

### Registration Summary Dashboard

The RegistrationApp seems to provide, by default, a single widget, which displays the information in the "demographics" section. It only will display patient attributes - concept/observation data added to the demographics section will always show a blank answer in the dashboard widget.

## Forms

Forms live in [openmrs-module-pihcore/omod/src/main/webapp/resources/htmlforms](https://github.com/PIH/openmrs-module-pihcore/tree/master/omod/src/main/webapp/resources/htmlforms) and are edited in code. The xml files that represent forms are parsed by the HTML FormEntry Module. Check out it [HTML/DSL Reference](https://wiki.openmrs.org/display/docs/HTML+Form+Entry+Module+HTML+Reference). Note especially the use of Velocity Expressions, and the content of the default Velocity context.

See this [example of a check-in form](https://github.com/PIH/openmrs-module-pihcore/blob/master/omod/src/main/webapp/resources/htmlforms/haiti/checkin.xml). 

The application logic that specifies when to display forms, and which form files to use, is found in [CALF](https://github.com/PIH/openmrs-module-mirebalais/blob/master/api/src/main/java/org/openmrs/module/mirebalais/apploader/CustomAppLoaderFactory.java). This class is responsible for loading forms from code into the database. It doesn’t always succeed in doing this dynamically, however, when forms are being edited, so as a back-up forms are manually loaded in [mirebalais/setup/HtmlFormSetup](https://github.com/PIH/openmrs-module-mirebalais/blob/master/api/src/main/java/org/openmrs/module/mirebalais/setup/HtmlFormSetup.java).

Note that this application logic often depends both on which components are enabled (see "Country-specific settings" below) and which location tags are enabled at the active location, which are set in [openmrs-module-pihcore/api/src/main/java/org/openmrs/module/pihcore/setup/LocationTagSetup.java](https://github.com/PIH/openmrs-module-pihcore/blob/master/api/src/main/java/org/openmrs/module/pihcore/setup/LocationTagSetup.java).

To view changes to forms with page refreshes, you need to make sure the query string in the address bar contains `breadcrumbOverride=breadcrumbUiOverride`.

### Adding a New Type of  Form

To add a new type of form is to create a new encounter type. See [pihcore PR #10](https://github.com/PIH/openmrs-module-pihcore/pull/10/commits) and [mirebalais PR #16](https://github.com/PIH/openmrs-module-mirebalais/pull/16/commits/217d8c0cfe2a4f76ca9f78357f4931937d86e7e6) for an example of how to accomplish this, along with creating a corresponding new app.

### Registration Form

The Registration form is produced by RegistrationApp based on the configuration specified in [mirebalais/apploader/apps.patientregistration/](https://github.com/PIH/openmrs-module-mirebalais/tree/master/api/src/main/java/org/openmrs/module/mirebalais/apploader/apps/patientregistration). This also generates some of the Edit Registration forms, but not all of them. RegistrationApp is able to provide View and Edit UI for sections that do not have concept questions. For sections with concept questions, you will need to create a .xml file (like patientRegistration-contact.xml) to define those views.

## Country-specific settings

Global properties for a country installation are defined in the mirebalais-puppet repo, .e.g., these two are used for Haiti:

[https://github.com/PIH/mirebalais-puppet/blob/master/mirebalais-modules/openmrs/files/config/pih-config-mirebalais.json](https://github.com/PIH/mirebalais-puppet/blob/master/mirebalais-modules/openmrs/files/config/pih-config-mirebalais.json)

[https://github.com/PIH/mirebalais-puppet/blob/master/mirebalais-modules/openmrs/files/config/pih-config-mirebalais-humci.json](https://github.com/PIH/mirebalais-puppet/blob/master/mirebalais-modules/openmrs/files/config/pih-config-mirebalais-humci.json)

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



## Updating


Once you have installed the distribution, you should be able to update it with the following commands... 

If you are watching any modules, first execute "mvn openmrs-sdk:pull" to pull in any changes to these modules via git.

Then, from the base directory of the mirebalais project run the following two commands to update any changes to modules
you aren't watching:

```
$ git pull
$ mvn openmrs-sdk:deploy -Ddistro=api/src/main/resources/openmrs-distro.properties -U
```

(I have created a shell script shortcut to execute the two commands above, pihemrDeploy.sh)

To run the server: 
```
$ mvn openmrs-sdk:run
```

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
[here](https://github.com/brandones/pih-emr-workspace/blob/master/tasks.py).
Feel free to make pull requests.


# Temporary steps to deploying custom build of OpenMRS-Core

We are temporarily running off an unreleased version of OpenMRS Core based off the master branch.
Once version 2.2.0 is released, we will switch to that branch, but in the meantime we will need
to check out and manually build the build of OpenMRS core we want to use:

1) If you haven't done so already, clone OpenMRS-Core:
https://github.com/openmrs/openmrs-core.git

2) Make sure you are "watching" OpenMRS-Core; from the directory where you checked out core:
"mvn openmrs-sdk:watch"

3) Check out the following tag:
"git checkout tags/2.2.0-20181112.082045-243"

### Troubleshooting

If you see the following error when building core:

[ERROR] Failed to execute goal com.mycila:license-maven-plugin:3.0:check (default) on project openmrs-test: Execution default of goal com.mycila:license-maven-plugin:3.0:check failed: Cannot read header document license-header.txt. Cause: Resource license-header.txt not found in file system, classpath or URL: no protocol: license-header.txt -> [Help 1]

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

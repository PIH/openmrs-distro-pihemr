# openmrs-distro-pihemr
===============================

This project defines the "PIH EMR" distribution of OpenMRS, which is made up of a specific OpenMRS war, 
a specific set of modules, and a specific set of microfrontends.  The PIH EMR distribution is designed such that it
requires a specific distribution configuration to be installed with it.  Together, the PIH EMR distribution
and the configuration make up a particular installation.

# Overview of the PIH-EMR

The *PIH-EMR* is a *distribution* of *OpenMRS*, an open-source medical record system. 
A few links from the OpenMRS wiki that are worth reading:

* High-level overview of OpenMRS: https://wiki.openmrs.org/display/docs/Introduction+to+OpenMRS
* Overview of the OpenMRS data model: https://wiki.openmrs.org/display/docs/Data+Model
* Definition of an "OpenMRS distribution": https://wiki.openmrs.org/display/docs/OpenMRS+Distributions

Therep are 40+ OpenMRS modules that make up the PIH EMR, of which most are OpenMRS community modules, but also includes
some PIH-specific modules as well.  The [pihcore module](https://github.com/PIH/openmrs-module-pihcore) is the most 
significant of these, and is designed to be the primary "top-level" module that ensures each of the other required
modules are installed and running and configured correctly.  The pihcore module also ensures all of the appropriate
configurations are applied and the installation is setup correctly given the country, site, and/or environment type specified.

There are currently the following "configurations" of the PIH EMR:

* [openmrs-config-pihemr](https://github.com/PIH/openmrs-config-pihemr) (Shared Configuration, not used directly)
* [openmrs-config-zl](https://github.com/PIH/openmrs-config-zl)
* [openmrs-config-ces](https://github.com/PIH/openmrs-config-ces)
* [openmrs-config-ses](https://github.com/PIH/openmrs-config-ses)
* [openmrs-config-pihliberia](https://github.com/PIH/openmrs-config-pihliberia)
* [openmrs-config-pihsl](https://github.com/PIH/openmrs-config-pihsl)

Within each of these configurations, there are several variations that are enabled based on the "pih.config" runtime
property.  Specific examples of distinct implementations include:

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
  - See the [CES EMR documentation](https://pihemr.atlassian.net/wiki/spaces/PIHEMR/pages/296255489/CES+EMR)


For deploying the PIH-EMR to our various staging and production servers, we use Puppet (https://puppet.com/)
Our Puppet configuration scripts can be found here: https://github.com/PIH/mirebalais-puppet

# Communications and management

There are a few tools that we use extensively and that all PIH devs should have set up:

* Microsoft Teams (you will need an @pih.org email address)
* JIRA for managing project, bugs, sprints, etc: https://pihemr.atlassian.net/secure/Dashboard.jspa
  Please request an account by asking another PIH developer or emailing medinfo@pih.org

# Setting up a Dev Environment

A development environment can be set up with the OpenMRS SDK, with some custom configuration steps, as written below.

Setup can also be done using the[PIH EMR Invoke file](https://github.com/PIH/pih-emr-invoke), for which the instructions are in that README.

## Prerequisites

First, install git, mvn, and the OpenMRS SDK by following the "Installation" instructions here:

https://wiki.openmrs.org/display/docs/OpenMRS+SDK#OpenMRSSDK-Installation

The OpenMRS SDK uses the H2 database by default, but H2 doesn't work with some of the
modules we use, so we must use MySQL, which needs to be configured separately. To do this, you can either
install MySQL directly on your machine, or install mysql within a docker container.

If installing directly, install MySQL Community Server 5.6 following the instructions for your platform. It must be
version 5.6, other versions will not work.

An easier approach is likely to install Docker (https://www.docker.com/) and use the OpenMRS SDK to set up an instance of MySQL within a docker container.

Building the distribution file (`mvn clean install -Pdistribution`) requires npm 7. Make sure you have the latest npm installed.

## Setup

Epic for making setting up a dev environment easier: https://pihemr.atlassian.net/browse/UHM-4245

### Step 1: Ensure you have MySQL available

#### If you are directly installing MySQL on your machine 

Ensure that MySQL has a password set up for the root user

- If you are able to run ```$ mysql -u root``` and access the MySQL Monitor without receiving an access denied error,
it means that there is no root password set and you have to set it following the instructions here: 
https://dev.mysql.com/doc/refman/5.6/en/resetting-permissions.html

- Once the root password has been set, you should be able to access the MySQL Monitor by running:  
  ```$ mysql -u root -p``` followed by entering the password when prompted.

#### If you choose to install MySQL using Docker

You will need to ensure Docker is installed and running on your machine.
https://docs.docker.com/engine/install/ubuntu/

You should also ensure that you can run all Docker commands without requiring sudo or root.
https://docs.docker.com/engine/install/linux-postinstall/

- To use MySQL Option 2 in the SDK installation process, nothing further is required.
  
- To use MySQL Option 3 in the SDK installation process, you will need to create your own MySQL Docker container and instantiate a database into it
  
  * Create a container (example below creates a container named "mysql-mirebalais" that will be available on port 3308):
  
  ```shell script
    docker run --name mysql-mirebalais -d -p 3308:3306 \
            -e MYSQL_ROOT_PASSWORD=root \
            mysql:5.6 --character-set-server=utf8 --collation-server=utf8_general_ci --max_allowed_packet=1G
    ``` 
  
  * Get a bash shell in the container, and create an empty database to use
  
    ```shell script
       $ docker exec -it mysql-mirebalais bash
       root@f25c851762df:/# mysql -uroot -proot
       mysql> create database openmrs default charset utf8;
      ``` 
    
### Step 2: Set up the environment

Set up the environment via the following command, choosing the serverId and dbName you want to use. Specify
the DB password for your root user as set in Step 2.

The **Application Data Directory** will be set up at `~/openmrs/[serverId]`.

The convention for dbNames are "openmrs_[some name]".

```
$ mvn openmrs-sdk:setup -DserverId=[serverId] -Ddistro=org.openmrs.distro:pihemr:1.4.0-SNAPSHOT
```

* When prompted, select the "pih.config" value to use.  This determines the site-specific configuration to be applied to your server environment.  Common options are as follows:

  * Mirebalais Dev/Test environment:  `mirebalais,mirebalais-humci`
  * Haiti HIV Dev/Test environment:  `haiti,haiti-hiv,haiti-hiv-ci`
  * Haiti HSN Dev/Test environment:  `haiti,haiti-hsn,haiti-hsn-ci`
  * Haiti Other Dev/Test environment:  `haiti,haiti-<site>,haiti-ci`
  * Liberia Dev/Test environment:  `liberia,liberia-harper,liberia-harper-dev`
  * Sierra Leone KGH Dev/Test environment:  `sierraLeone,sierraLeone-kgh`
  * Mexico dev/test environment:  `mexico,mexico-demo`
  * Peru dev/test environment:  `peru`

* When prompted, select the port you'd like to run tomcat on

* When prompted, set the port to debug on (standard is 1044)

* For database selection, select either the option to use locally-installed MySQL, or to use a MySQL docker container.
  Your`serverId` must only contain [MySQL Permitted Characters in Unquoted Identifiers](https://dev.mysql.com/doc/refman/8.0/en/identifiers.html).

  * If you are connecting to a MySQL 5.6 instance running on your local machine:
    * Specify the URI and a username and password to connect to the DB

  * If you are connecting to a MySQL 5.6 instance running in an SDK-managed Docker container
    * Choose option 2

  * If you are connecting to a MySQL database in an existing Docker container (as described above):
    * Choose option 3
    * Container ID:  "mysql-mirebalais" or whatever you chose above
    * DB username:  root
    * DB password: root
    * URL: jdbc:mysql://localhost:3308/openmrs (if you used a different port above, will need to reflect that here)

* Select the JDK to use (it must be 1.8)

NOTE: It is possible to script this to be non-interactive.  Here is an example of creating a new server instance,
using an SDK-created Docker container for MySQL, running the server on port 8080, with debugging on port 1044:

```shell
$ mvn openmrs-sdk:setup \
    -DserverId=[serverId] \
    -Ddistro=org.openmrs.distro:pihemr:1.4.0-SNAPSHOT \
    -DjavaHome=/usr/lib/jvm/java-8-openjdk-amd64 \
    -Dpih.config=mirebalais,mirebalais-humci
    -DbatchAnswers="placeholder,8080,1044,MySQL 5.6 in SDK docker container (requires pre-installed Docker)"
```

### Step 3: Clone the configuration project for the distro you are working

The various configuration files that determine what applications and options are turned on on different servers are
found here. The configuration distro projects are as follows:

|Site|Repo  |
|---|---|
|PIH EMR "Parent" Config|https://github.com/PIH/openmrs-config-pihemr|
|CES|https://github.com/PIH/openmrs-config-ces|
|Liberia|https://github.com/PIH/openmrs-config-pihliberia|
|SES|https://github.com/PIH/openmrs-config-ses|
|Sierra Leone|https://github.com/PIH/openmrs-config-pihsl|
|ZL|https://github.com/PIH/openmrs-config-zl|

You want to clone both the "Parent" config and the specific configuration for the site you are working on:

For instance, for the Liberia configuration the command is:

```
git clone https://github.com/PIH/openmrs-config-pihemr
git clone https://github.com/PIH/openmrs-config-pihliberia.git
```

**IMPORTANT** Please check out the two projects under the same top-level directory so that the "install.sh"
script we will use in the next step can find the "Parent" config relative to the site-specific config.

### Step 4: Compile the configuration project and install it

Go into the top-level directory of the configuration project you checked out above and
run the install script. Use the `serverId` you choose in Step 1.

```
cd openmrs-config-liberia
./install.sh [serverId]
```

This uses the [OpenMRS Packager Maven plug-in](https://github.com/PIH/openmrs-packager-maven-plugin)
to assemble the configuration and install it in the application
data directory.

### Step 5: Set up the frontend

To use the OpenMRS 3.x frontend, clone the
[openmrs-frontend-pihemr](https://github.com/PIH/openmrs-frontend-pihemr)
repository into the same directory where `openmrs-disto-pihemr` and 
your config repositories are. Execute the following:

```
cd openmrs-frontend-pihemr
./install.sh [serverId]
```

This will put the frontend files in the `frontend/` directory of your server
directory, and create a symlink from `configuration/frontend` to
`frontend/site`, which will allow the frontend application to access it.

### Step 6: Startup the server

```
mvn openmrs-sdk:run -DserverId=[serverId]
```

In your browser, navigate to `http://localhost:8080/openmrs` in order to initiate the full startup process.

This is where the bulk of the installation occurs, and may take many minutes to complete (potentially 30-40 minutes).
When this is complete, you should have a running PIH EMR instance and you should be able to navigate to the 
application and see an appropriate login page for your chosen distribution and configuration.

By default you can log into this via:  admin/Admin123

### Step 7: Create an account that is a provider

In order to use most of the functions of the system (patient registration, visit note, etc), you must be a Provider.
By default, the "admin" user is not a Provider.  You'll need to log into the system as the "admin" user, navigate to the 
System Administration -> Manage Accounts page, and create a new user account for yourself.  Typically for a development
environment where you would have a single account for yourself, you'd use the following:

* Privilege Level: Full
* Capabilities:  SysAdmin Privileges
* Provider Type: General Admin

  
## Developing Microfrontends

The PIH EMR uses the [Frontend 3.0 framework](https://wiki.openmrs.org/display/projects/OpenMRS+3.0%3A+A+Frontend+Framework+that+enables+collaboration+and+better+User+Experience).
We have a few custom microfrontends:

- https://github.com/PIH/pih-esm-refapp-navbar for visual coherence with RefApp 2.x
- https://github.com/PIH/pih-esm-referrals-queue for the J9 program at Mirebalais Hospital
- https://github.com/PIH/pih-esm-pathology-app for the oncology program at PIH Rwanda (IMB)

Please see the [Frontend 3.0 Developer Documentation](https://openmrs.github.io/openmrs-esm-core/#/) for information about how
to work on them.

# Updating the Configuration for your Distribution

 We are in the process of moving as much "configuration" out of the main PIH EMR code base and into distribution-specific files.  These files can be updated without updating the main PIH EMR code base.

To do this, you'll need to work with the "parent" config rep and the repo for your specific site, mentioned 
in Step 3 of "setting up a dev environment".

https://github.com/PIH/openmrs-config-pihemr

This is the "parent" configuration, that contains all configuration shared across all PIH EMR distributions.

Then there is the specific "child" configuration for each distribution.  See Step 3 in "setting up a dev environment" above for the list of repos.

When you make changes to either the "parent" or "child" repo, you need to compile the changes and then "deploy" them to the SDK server you are working on.  This can be done with the following command shell script (which simply runs the
Maven packager plugin) found in the top-level of the various config projects:

```
./install.sh [serverId]
```

Note if you make changes to metadata installed via Initializer, you will need to restart your server to pick up the changes.  However, HTML Forms should be available to be "hot" reloaded... once you run the mvn commands above, doing a "reload" of a page should reload the form with your changes.

You also can set up a "watch" on both the parent and child project, so that when you make changes to, say, an
HTML Form, the project is immediately compiled and deployed.  You do so using a "watch" utility script found in the
top-level of the various config projects:

```
./watch.sh [serverId]
```

Also note that if you commit any changes to either the parent or child config property, our CI server should immediately push them out to the relevant staging servers and restart OpenMRS, so, all going well, your changes should be up on the staging servers within 10-15 minutes of pushing your changes.

# Updating the PIH EMR Code

Modules and code need to be kept in sync and up to date when developing.

You can `git pull` the latest changes in all watched modules using `mvn openmrs-sdk:pull`.

Then run `./pihemrDeploy.sh [serverId]` to update all modules that aren't watched.

See [Making things easy](#making-things-easy)

To run the server: 
```
$ mvn openmrs-sdk:run
```

### Creating a local identifier source in a Haiti instance

**This is only required for Haiti if you do not configure this appropriately via runtime properties at setup time**

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


# Configuring functionality in a PIH EMR OpenMRS Instance


### Registration Summary Dashboard

The RegistrationApp seems to provide, by default, a single widget, which displays the information in the "demographics" section. It only will display patient attributes - concept/observation data added to the demographics section will always show a blank answer in the dashboard widget.

RegistrationApp is configured in `pihcore/.../apps/patientregistration/`. `SectionsDefault` provides the default Registration application. Some of it is configurable using the site configuration JSON, [mirebalais-puppet/.../pih-config-<site>.json](https://github.com/PIH/mirebalais-puppet/tree/master/mirebalais-modules/openmrs/files/config). Parts of it can be overridden in the site-specific Sections file, e.g. `SectionsMexico.java`.
  
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
1. Update that MDS package in your config project.
1. Add a global property to the appropriate configuration project that configures `EmrApiConstants.GP_DIAGNOSIS_SET_OF_SETS`, with the uuid of the Concept Set of Sets from above.

## Registration Form

The Registration form is produced by RegistrationApp based on the configuration specified in [the CALF](https://github.com/PIH/openmrs-module-pihcore/tree/master/api/src/main/java/org/openmrs/module/pihcore/apploader/apps/patientregistration). 
This also generates some of the Edit Registration forms, but not all of them. RegistrationApp is able to provide View and Edit UI for sections that do not have concept questions. For sections with concept questions, you will need to create a .xml file (like patientRegistration-contact.xml) to define those views.

## Programs

### Enabling a Program

There are a number of programs that come with PIH EMR. Each one has its own component. To enable a program:
- Enable the component by adding it to the appropriate pih-config file
- Ensure you have the concepts required by the program in an appropriate concept package
- Ensure the program bundle will be loaded in your configuration (your config project includes the relevant program)

## Locales

See the [OpenMRS Wiki page on locales](https://wiki.openmrs.org/display/docs/Localization+and+Languages).
Allowed locales are configured in [global properties](https://github.com/PIH/openmrs-config-pihliberia/blob/880704d98f477ec75db9c29bc5566ac9c90a5aad/configuration/globalproperties/gp_pihliberia.xml#L10).
Text mostly comes either from concept names or from message strings.
Concept names and their translations should be managed on the [concepts server](http://concepts.pih-emr.org).

### Message string management

Message strings that are specific to the PIH EMR or which we define in the PIH EMR are kept in 
[config-pihemr](https://github.com/PIH/openmrs-config-pihemr/tree/master/configuration/messageproperties).

Some message strings are embedded into OpenMRS modules. See, for example, the
[English](https://github.com/openmrs/openmrs-module-coreapps/blob/master/api/src/main/resources/messages.properties)
and [Spanish](https://github.com/openmrs/openmrs-module-coreapps/blob/master/api/src/main/resources/messages_es.properties)
strings in the coreapps module.  Most of these modules are managed in the OpenMRS community via [Transifex](https://www.transifex.com/):
Ask someone from the PIH EMR team to grant you access to the PIH Transifex org.  In these cases, the english message should
be added manually, but none of the non-English messages should be added directly to the modules.  They should follow the transifex process.

## Making Things Easy

### Bash Aliases

```
$ alias omrs-pull='mvn openmrs-sdk:pull'
$ alias omrs-deploy='cd /home/mgoodrich/openmrs/modules/pihcore && ./pihemrDeploy.sh'
$ alias omrs-run='mvn openmrs-sdk:run -Ddebug'
```

So to do a daily update of the system, run:

```
$ omrs-pull
$ omrs-deploy [serverId]
$ omrs-run
```

### Fish Aliases

If using [Fish Shell](https://fishshell.com/) (which confers major quality-of-life improvements over Bash), you can effectively
alias `mvn openmrs-sdk:...` to `omrs ...` by creating a file `~/.config/fish/functions/omrs.fish` with these contents:

```sh
function omrs
    if test (count $argv) -lt 1 -o "$argv[1]" = "--help"
      echo "Usage: 'omrs <command> <options>' does 'mvn openmrs-sdk:<command> <options>'"    
      echo "  e.g. 'omrs run' runs 'mvn openmrs-sdk:run'"    
      return 1    
    end
    mvn openmrs-sdk:$argv[1] $argv[2..-1]    
end
```

You can then run `omrs run`, `omrs watch`, etc.

### PyInvoke

There's an [Invoke](http://www.pyinvoke.org/) file for doing local development of the PIH EMR
[here](https://github.com/PIH/pih-emr-invoke). It automates a lot of common PIH EMR pain points, including
optionally pulling, deploying, and enabling all modules before each run; providing an easy-to-read overview
of the project state before running (the git statuses of watched modules and config directories);
and simplifying server setup. 

## Troubleshooting

#### The login screen looks funny, and just gives me errors

If you're seeing a login screen that looks like this

![MicrosoftTeams-image](https://user-images.githubusercontent.com/1031876/98297904-0244f480-1f6a-11eb-9da5-b8a7841bca66.png)

it means that the pihcore module has failed to load. Look in the logs to find out why.   Try searching the log for `pihcore`.

#### OpenMRS displays errors like this after starting up, where `Foo` is the name of the module I'm working on:
#### `Foo Module cannot be started because it requires the following module(s): Bar 1.2.3-SNAPSHOT`

As of this writing, we use a lot of snapshots. One thing that can happen is that when
you `openmrs-sdk:run`, Maven might pull a new snapshot version of some module (here, Foo),
but its dependencies may have updated, coming out of sync with the Foo POM you have 
locally. So the snapshot version of Foo expects BAR 1.2.3-SNAPSHOT, but your local 
Foo POM still requires Bar 1.2.3.

To resolve
1. Pull the latest changes to Foo from master (merging/rebasing 
    into your branch if you're in a branch)
2. Run `./pihemrDeploy.sh` in `openmrs-module-pihcore`, or run `invoke deploy`
3. Re-run your server

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

What the error means, interpreted literally, is that the pihcore module is
overriding the header extension, but didn't successfully initialize the override.
There are two likely reasons:

1. The pihcore module is activating correctly, but the code is wrong, in that it's
   actually not setting up the header extension. Check the code in the CALF (see CustomAppLoaderFactory.java#L350) to see if it's setting up a header extension for
   your site. This is likely the reason if you're setting up a new site.
2. Something went wrong at some point in server initialization before the header
   extension could be set. Scroll up in the logs and look for an exception.

#### I'm getting an error "viewProvider pihcore does not have a view named home" immediately on navigating to the EMR

Something like
```
ERROR - PageController.handlePath(155) |2020-01-15 09:23:35,714| 
org.openmrs.ui.framework.UiFrameworkException: viewProvider pihcore does not have a view named home
...
```

The error is itself meaningless. Look up in the logs for an exception trace to find out what went wrong.

#### The server isn't reflecting the changes I'm making in code

Make sure that the module you're working on hasn't come un-watched. Look at the `watched.projects` line of `openmrs-server.properties` in the App Data directory.

#### After fixing an error while developing on a metadata bundle, the metadata still isn't updating

When a module fails to start, the next time OpenMRS runs it will not try to start it.
On realizing this has happened, you may be inclined to start it from the Admin UI. 
However, there's a problem. Metadata bundles don't load when starting the pihcore module via the admin
UI on an already-running server. Therefore you must always make sure that your modules are enabled prior 
to running the server, especially after running into problems during initialization.

This can be accomplished by logging in to mysql and running
```
update global_property set property_value='true' where property like '%started%';
```

or by running `invoke enable-modules` if you're using [PyInvoke](https://github.com/brandones/pih-emr-workspace/blob/master/tasks.py).

#### Error about `com.mycila` when building core

If, when building core, you see an error like...

```
[ERROR] Failed to execute goal com.mycila:license-maven-plugin:3.0:check (default) on project openmrs-test: Execution default of goal com.mycila:license-maven-plugin:3.0:check failed: Cannot read header document license-header.txt. Cause: Resource license-header.txt not found in file system, classpath or URL: no protocol: license-header.txt -> [Help 1]
```

... then try commenting out the mycila plugin in the main pom of the project


# Source Code


All source code for OpenMRS is stored on Github, in either the OpenMRS organization (for core and community modules) or 
the PIH organization (for PIH-specific modules).

https://github.com/openmrs
https://github.com/PIH

The naming convention for a module repo is "openmrs-module-[module_name]".

# Getting Set Up with IntelliJ

At least with `openmrs-module-pihcore`, IntelliJ may want to identify some directories as modules that are not modules,
and are in fact subdirectories of actual modules. The problem is that these phony modules don't inherit Maven
dependency information from the parent modules, so IntelliJ will fail to resolve references to those dependencies.
To fix this, go to Project Structure -> Modules and remove those directories.

Other than that, this project is more or less plug-and-play in IntelliJ.

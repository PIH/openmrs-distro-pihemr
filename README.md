
Setting up a Dev Environment
============================

We are going to set up a development environment using the OpenMRS SDK, with some custom steps to install the set of 40+ 
modules used by the PIH EMR.

Prerequisites
-------------

First, install git, mvn, and the OpenMRS SDK by following the "Installation" instructions here:

https://wiki.openmrs.org/display/docs/OpenMRS+SDK#OpenMRSSDK-Installation

Next, we need to install MySQL.  The OpenMRS SDK uses the H2 database by default, but H2 doesn't work with some of the 
modules we use, so we must use MySQL, which needs to be configured separately.  You should install MySQL Community 
Server 5.6 following the instructions for your platform.

Set up PIH EMR distibution
----------------------------

Set up the environment via the following command, chosing the serverId and dbName you want to use, and added
the DB password for your root user.  Note that the environment will be set up the directory ~/openmrs/[serverId]

mvn openmrs-sdk:setup -DserverId=[serverId] -Ddistro=org.openmrs.module:mirebalais-api:1.2-SNAPSHOT
    -DdbUri=jdbc:mysql://localhost:3306/[dbName] -DdbUser=root -DdbPassword=[password]

Then start up the server:

mvn openmrs-sdk:run -DserverId=mirebalais

It should run for several minutes, setting up the database, BUT, in the end, it will fail.

After it fails, notice that a openmrs-runtime.properties file should have been created in the ~/openmrs/[serverId]

You should add a line to these file specifying which of our configs to use for this server. For instance, to use
the Mirebalais configuration, add the following to the runtime properties:

pih.config=mirebalais-humci,mirebalais


Then try starting the server again:

mvn openmrs-sdk:run -DserverId=mirebalais

Updating
--------

Once you have installed the distribution, you should be able to update it with the following command *from the base 
directory of the mirebalais project*:

mvn openmrs-sdk:deploy -DserverId=serverId -Ddistro=src/main/resources/openmrs-distro.properties


Source Code
-----------

All source code for OpenMRS is stored on Github, in either the OpenMRS organizaiton (for core and community modules) or 
the PIH organization (for PIH-specific modules).

https://github.com/openmrs
https://github.com/PIH

The naming convention for a module repo is "openmrs-module-[module_name]".
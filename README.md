
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

Set up a development environment of a PIH EMR distibution
---------------------------------------------------------

Set up the environment via the following command, chosing the serverId and dbName you want to use, and added
the DB password for your root user.  Note that the environment will be set up the directory ~/openmrs/[serverId]

mvn openmrs-sdk:setup -DserverId=[serverId] -Ddistro=org.openmrs.module:mirebalais:1.2-SNAPSHOT
    -DdbUri=jdbc:mysql://localhost:3306/[dbName] -DdbUser=root -DdbPassword=[password]

Then start up the server:

mvn openmrs-sdk:run -DserverId=[serverId]

It should run for several minutes, setting up the database, (you may have to go to http://localhost:8080/openmrs to trigger this) BUT, in the end, it will fail.  You should cancel the current run (Ctrl-C in the terminal window).

After it fails, notice that a openmrs-runtime.properties file should have been created in the ~/openmrs/[serverId]

Add a line to these file specifying which of our configs to use for this server. For instance, to use
the Mirebalais configuration, add the following nto the runtime properties:

pih.config=mirebalais-humci,mirebalais

Then rerun:

mvn openmrs-sdk:run -DserverId=[serverId]

Startup should take several minutes as it loads in all required metadata, etc, for the first time.

After startup, there's one manual configuration you will have to do, create a local identifier source to generate "fake" ZL EMR IDs:

- In the legacy admin go to "Manage Patient Identifier Sources"
- Add a new "Local Identifier Generator" for the "ZL EMR ID" with the following settings:
- Name: ZL Identifier Generator
- Base Character Set: ACDEFGHJKLMNPRTUVWXY1234567890
- First Identifier Base: 1000
- Prefix: Y
- Max Length: 6
- Min Length: 6
- Link the local generator to the Local Pool of Zl Identifiers
- Click the Configure Action next to the local pool
- Set "Pool Identifier Source" to "ZL Identifier Generator"
- Change "When to fill" to "When you request an identifier"


Updating
--------

Once you have installed the distribution, you should be able to update it with the following commands... 

If you are watching any modules, first execute "mvn openmrs-sdk:pull" to pull in any changes to these modules via git

Then, from the base directory of the mirebalais project run the following two commands to update any changes to modules you aren't watching:

- git pull
- mvn openmrs-sdk:deploy -Ddistro=api/src/main/resources/openmrs-distro.properties

(I have created a shell script shortcut to execute the two commands above, pihemrDeploy.sh)

To run the server: 
- mvn openmrs-sdk:run


Handy aliases
-------------

(I think you can set the "debug" at project creation time now, so that might not be as necessary.  pihemrDeploy.sh is a utility script I created.)

- alias omrs-pull='mvn openmrs-sdk:pull'
- alias omrs-deploy='cd /home/mgoodrich/openmrs/modules/mirebalais && ./pihemrDeploy.sh'
- alias omrs-run='mvn openmrs-sdk:run -Ddebug'

So to do a daily update of the system I run:

- omrs-pull
- omrs-deploy
- omrs-run


Source Code
-----------

All source code for OpenMRS is stored on Github, in either the OpenMRS organizaiton (for core and community modules) or 
the PIH organization (for PIH-specific modules).

https://github.com/openmrs
https://github.com/PIH

The naming convention for a module repo is "openmrs-module-[module_name]".

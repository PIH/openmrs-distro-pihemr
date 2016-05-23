
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

Set up platform and modules
---------------------------

Next, set up the OpenMRS platform using the SDK:

mvn openmrs-sdk:setup-platform -DserverId=[serverId] -Dversion=[version] -DdbDriver=mysql

Where "serverId" will be the name of the subdirectory off $HOME/openmrs where the server will be created, and "version" 
should be set to the version of OpenMRS that the PIH EMR uses (currently 1.10.4 as of May 2016).

When prompted, accept the default db URI.  You'll also need to enter the username and password of a MySQL user that 
has the ability to create new databases.

Next, start up the new server (before installing any modules) in order to create your base database as well as the 
openmrs-runtime.properties file (which you will need to modify in a later step). Go into the subdirectory where the SDK 
installed the server and:

mvn openmrs-sdk:run 

Go to http://localhost:8080/openmrs and wait until it finishes creating database (ie., when the installation wizard 
completes and switches to the OpenMRS login page).

After this is complete, stop OpenMRS from running (you can just use Ctrl-C to kill the process).  We will now install 
the PIH EMR modules as well as set some configurations in the openmrs-runtime.properties file.

Next, check out the mirebalais module source code, navigate to the top-level directory of the module, and build the 
PIH EMR distribution:

mvn clean install -DskipTests -Pdistribution

Then, from the same directory, copy all the OpenMRS modules that the distribution built into the modules directory of 
your server:

cp distro/target/distro/* ~/openmrs/[serverId]/modules

Note: it is a good idea to run the two previous commands regularly, to make sure you are always using the same versions 
of each module that is currently being packaged as part of the PIH EMR.  (TODO: find some way to automate this?)

Now you will want to modify your openmrs-runtime.properties file (found in /openmrs/[serverId], but only after you have 
run OpenMRS at least once) to specify what configuration (TODO: document the different configurations and how they work)
of the PIH-EMR you want to run.  For example, to run the test version of UHM configuration add the following line:

pih.config=mirebalais,mirebalais-humci

Starting OpenMRS
----------------

### From the Comand Line

Make sure you've allocated extra memory to Maven. You can do this via setting the MAVEN_OPTS variable:  
(TODO–pointer to exactly how to set an env variable by platform)

MAVEN_OPTS="-Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m" 

Now start OpenMRS:

mvn openmrs-sdk:run

### From IDEA

Create a new Maven Run Configuration with the working directory set (in the Parameters tab) to you top-level SDK 
directory (you may have to enter this path manually, unfortunately, since IDEA doesn't seem to recognize it as a valid 
Maven project), the command line set (also in the Parameters tab) to "openmrs-sdk:run" and the VM options set (in the 
Runner tab) to -"Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m" 

Accessing OpenMRS
-----------------

OpenMRS will take several minutes to start the first time, but when it does you should be able to navigate to 
http://localhost:8080/openmrs to see the server.  The username/password pair should be admin/[password redacted]

Working on Modules
------------------

You should now have the full PIH EMR up and running, but if you are planning on doing actual development, you'll want 
to check out out the source code for the modules you are working on, and tie that into your build. **TO DO–continue to define this**
 
Source Code
-----------

All source code for OpenMRS is stored on Github, in either the OpenMRS organizaiton (for core and community modules) or 
the PIH organization (for PIH-specific modules).

https://github.com/openmrs
https://github.com/PIH

The naming convention for a module repo is "openmrs-module-[module_name]".
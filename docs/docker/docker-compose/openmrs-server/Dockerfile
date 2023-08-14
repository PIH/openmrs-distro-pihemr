FROM tomcat:7-jre8

ENV MYSQL_PORT="3306"
ENV OPENMRS_MEMORY_OPTS="-Xmx2048m -Xms1024m -XX:PermSize=256m -XX:MaxPermSize=512m -XX:NewSize=128m"
ENV OPENMRS_OTHER_OPTS="-server -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Djava.awt.headlesslib=true"
ENV OPENMRS_RUNTIME_PROPERTIES_FILE="/openmrs/openmrs-runtime.properties"

ADD openmrs-setenv.sh /usr/share/tomcat7/bin/setenv.sh

# These represent the folders created and used within the container
# Any webapps one wishes to run should be mounted into /openmrs/artifacts/webapps
# Any modules one wishes to run should be mounted into /openmrs/artifacts/modules
# The local clone of mirebalais-puppet/mirebalais-modules/openmrs/files/config should be mounted to /openmrs/artifacts/pih_config

WORKDIR /openmrs/artifacts/webapps
WORKDIR /openmrs/artifacts/modules
WORKDIR /openmrs/pih_config

VOLUME /openmrs/data

WORKDIR /openmrs

ADD openmrs-start.sh /openmrs/start.sh
ADD wait-for-it.sh /usr/bin/wait-for-it

# Open Ports and Run

EXPOSE 8080
CMD ["/openmrs/start.sh"]

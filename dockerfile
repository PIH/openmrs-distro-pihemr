# Use an official Maven image as the build image
FROM partnersinhealth/debian-build:latest AS build

#build args
ARG EMR_CONFIG=""
ARG EMR_FRONTEND=""

# Set the working directory in the container
WORKDIR /app

# Copy project files to the container
COPY ./pom.xml .
COPY ./openmrs-distro.properties .
COPY ./debian/ ./debian/
COPY ./assembly.xml .
COPY ./import_config.sh .

# Install application using Maven
RUN mvn -T 1C clean compile -Dmaven.test.skip -DskipTests -Dmaven.javadoc.skip=true
RUN ./import_config.sh ${EMR_CONFIG} ${EMR_FRONTEND}

# Use an official Tomcat image as the app image
FROM tomcat:9-jdk8
# Set the working directory in the container
WORKDIR /app
# Copy the built files from the build stage to the container
COPY --from=build /app/target/distro/web/openmrs.war /usr/local/tomcat/webapps/openmrs.war
COPY --from=build /app/target/distro/web/modules/ /app/.OpenMRS/modules/
COPY --from=build /app/target/distro/web/owa/ /app/.OpenMRS/owa/
COPY --from=build /app/frontend /app/.OpenMRS/frontend/
COPY --from=build /app/configuration/ /app/.OpenMRS/configuration/
COPY --from=build /app/configuration/frontend/ /app/.OpenMRS/frontend/site/

#Expose port 8080
EXPOSE 8080


#docker build . -t zlemr:latest --build-arg EMR_CONFIG=openmrs-config-zl --build-arg EMR_FRONTEND=openmrs-frontend-zl
#docker build . -t slemr:latest --build-arg EMR_CONFIG=openmrs-config-pihsl --build-arg EMR_FRONTEND=openmrs-frontend-pihemr
#docker build . -t cesemr:latest --build-arg EMR_CONFIG=openmrs-config-ces --build-arg EMR_FRONTEND=openmrs-frontend-pihemr
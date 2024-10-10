#The PIHEMR dockerfile is a multi-stage build using a PIH build container to construct a Tomcat app image
ARG BUILD_TAG="latest"
ARG APP_TAG="9-jdk8"
FROM partnersinhealth/debian-build:${BUILD_TAG} AS build

#Build arguments should be included for the config and frontend to be used in the app image.
ARG EMR_CONFIG=""
ARG EMR_FRONTEND=""

# Working directory for build image
WORKDIR /build

# Copy project files to the build container
COPY ./pom.xml .
COPY ./openmrs-distro.properties .
COPY ./debian/ ./debian/
COPY ./assembly.xml .
COPY ./import_config.sh .

# Compile distro using Maven
RUN mvn clean compile -Dmaven.test.skip -DskipTests -Dmaven.javadoc.skip=true
# Import Config and Frontend packages
RUN ./import_config.sh ${EMR_CONFIG} ${EMR_FRONTEND}

# Build PIHEMR application from a Tomcat image
FROM tomcat:${APP_TAG}

# Working directory for the app image
WORKDIR /app

# Copy the build target files from the build container to app image
COPY --from=build /build/target/distro/web/openmrs.war /usr/local/tomcat/webapps/openmrs.war
COPY --from=build /build/target/distro/web/modules/ /app/.OpenMRS/modules/
COPY --from=build /build/target/distro/web/owa/ /app/.OpenMRS/owa/
COPY --from=build /build/frontend /app/.OpenMRS/frontend/
COPY --from=build /build/configuration/ /app/.OpenMRS/configuration/
COPY --from=build /build/configuration/frontend/ /app/.OpenMRS/frontend/site/
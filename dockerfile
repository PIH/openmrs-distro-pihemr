#The PIHEMR dockerfile is a multi-stage build using a build container to construct an app image
ARG TAG="2.6.9"
FROM openmrs/openmrs-core:${TAG}-dev AS build

RUN yum install unzip -y

# Working directory for build image
WORKDIR /build

# Copy project files to the build container
COPY ./pom.xml .
COPY ./openmrs-distro.properties .
COPY ./debian/ ./debian/
COPY ./assembly.xml .

# Compile distro using Maven
RUN mvn clean compile -Dmaven.test.skip -DskipTests -Dmaven.javadoc.skip=true
# Import Config and Frontend packages
RUN ./debian/import_config.sh

# Build ZL application
FROM openmrs/openmrs-core:${TAG} AS zl

# Working directory for the app image
#WORKDIR /openmrs

# Copy the build target files from the build container to app image
COPY --from=build /build/target/distro/web/openmrs.war /openmrs/distribution/openmrs_core/

COPY --from=build /build/target/classes/openmrs-distro.properties /openmrs/distribution/openmrs-distro.properties
COPY --from=build /build/target/distro/web/modules/ /openmrs/distribution/openmrs_modules/
COPY --from=build /build/target/distro/web/owa/ /openmrs/distribution/openmrs_owas/
COPY --from=build /build/target/frontend_zl/*/ /openmrs/distribution/openmrs_spa/
COPY --from=build /build/target/configuration/frontend/ /openmrs/distribution/openmrs_spa/site/
COPY --from=build /build/target/configuration_zl/ /openmrs/distribution/openmrs_config/

# Build CES application
FROM openmrs/openmrs-core:${TAG} AS ces

# Working directory for the app image
#WORKDIR /openmrs

# Copy the build target files from the build container to app image
COPY --from=build /build/target/distro/web/openmrs.war /openmrs/distribution/openmrs_core/

COPY --from=build /build/target/classes/openmrs-distro.properties /openmrs/distribution/openmrs-distro.properties
COPY --from=build /build/target/distro/web/modules/ /openmrs/distribution/openmrs_modules/
COPY --from=build /build/target/distro/web/owa/ /openmrs/distribution/openmrs_owas/
COPY --from=build /build/target/frontend/*/ /openmrs/distribution/openmrs_spa/
COPY --from=build /build/target/configuration/frontend/ /openmrs/distribution/openmrs_spa/site/
COPY --from=build /build/target/configuration_ces/ /openmrs/distribution/openmrs_config/

# Build PIHSL application
FROM openmrs/openmrs-core:${TAG} AS pihsl

# Working directory for the app image
#WORKDIR /openmrs

# Copy the build target files from the build container to app image
COPY --from=build /build/target/distro/web/openmrs.war /openmrs/distribution/openmrs_core/

COPY --from=build /build/target/classes/openmrs-distro.properties /openmrs/distribution/openmrs-distro.properties
COPY --from=build /build/target/distro/web/modules/ /openmrs/distribution/openmrs_modules/
COPY --from=build /build/target/distro/web/owa/ /openmrs/distribution/openmrs_owas/
COPY --from=build /build/target/frontend/*/ /openmrs/distribution/openmrs_spa/
COPY --from=build /build/target/configuration/frontend/ /openmrs/distribution/openmrs_spa/site/
COPY --from=build /build/target/configuration_pihsl/ /openmrs/distribution/openmrs_config/

# Build PIHLIBERIA application
FROM openmrs/openmrs-core:${TAG} AS pihliberia

# Working directory for the app image
#WORKDIR /openmrs

# Copy the build target files from the build container to app image
COPY --from=build /build/target/distro/web/openmrs.war /openmrs/distribution/openmrs_core/

COPY --from=build /build/target/classes/openmrs-distro.properties /openmrs/distribution/openmrs-distro.properties
COPY --from=build /build/target/distro/web/modules/ /openmrs/distribution/openmrs_modules/
COPY --from=build /build/target/distro/web/owa/ /openmrs/distribution/openmrs_owas/
COPY --from=build /build/target/frontend/*/ /openmrs/distribution/openmrs_spa/
COPY --from=build /build/target/configuration/frontend/ /openmrs/distribution/openmrs_spa/site/
COPY --from=build /build/target/configuration_pihliberia/ /openmrs/distribution/openmrs_config/
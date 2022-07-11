#!/usr/bin/env sh

BUILD_NUMBER=$(date +%Y%m%d%H%M%S)

DISTRO_DIR=target/distro/web
TARGET_DIR=target/debian

perl -pi -e "s/pihemr \([^)]+\)/pihemr (1.0.${BUILD_NUMBER}-1)/" ${TARGET_DIR}/debian/changelog

cp ${DISTRO_DIR}/openmrs.war ${TARGET_DIR}/openmrs.war

mkdir -p ${TARGET_DIR}/home/tomcat7/.OpenMRS/modules/
cp ${DISTRO_DIR}/modules/* ${TARGET_DIR}/home/tomcat7/.OpenMRS/modules/

mkdir -p ${TARGET_DIR}/home/tomcat7/.OpenMRS/owa/
cp ${DISTRO_DIR}/owa/* ${TARGET_DIR}/home/tomcat7/.OpenMRS/owa/
rename 's/\.owa$/.zip/' ${TARGET_DIR}/home/tomcat7/.OpenMRS/owa/*.owa

cd ${TARGET_DIR} && debuild --no-tgz-check -i -us -uc -b

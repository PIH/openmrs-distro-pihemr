#!/bin/bash

#clean config files
rm -rf configuration
rm -rf frontend
rm *.zip

CONFIG=${1:-"openmrs-config-pihsl"}
FRONTEND=${2:-"openmrs-frontend-pihemr"}

echo "configuration is $CONFIG"
echo "frontend is $FRONTEND"

#Create dirs for infalting zip resources
mkdir ./frontend
mkdir ./configuration

#retrive config and frontent from maven

mvn org.apache.maven.plugins:maven-dependency-plugin:2.8:copy \
-Dartifact=org.pih.openmrs:"$CONFIG":LATEST:zip \
-DoutputDirectory=./ -Dmdep.useBaseVersion=true

mvn org.apache.maven.plugins:maven-dependency-plugin:2.8:copy \
-Dartifact=org.pih.openmrs:"$FRONTEND":LATEST:zip \
-DoutputDirectory=./ -Dmdep.useBaseVersion=true

#unzip maven resources

unzip -o "$CONFIG"* -d ./configuration/
unzip -o "$FRONTEND"* -d ./frontend/

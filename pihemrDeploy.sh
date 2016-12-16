#!/bin/bash

git pull
mvn openmrs-sdk:deploy -Ddistro=api/src/main/resources/openmrs-distro.properties
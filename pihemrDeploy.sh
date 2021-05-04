#!/bin/bash

git pull
mvn openmrs-sdk:deploy -Ddistro=distro/openmrs-distro.properties -U
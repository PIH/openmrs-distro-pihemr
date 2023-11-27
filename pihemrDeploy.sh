#!/bin/bash

usage () {
    echo -e "Usage: pihemrDeploy.sh [SERVER]\n"
    echo -e "Runs git pull, and then 'mvn openmrs-sdk:deploy' for SERVER, where a server is the "
    echo -e "name of an OpenMRS SDK instance at path '~/openmrs/[SERVER]'.\n"
    echo -e "Example: ./deploy.sh mirebalais\n"
}

if [ $# -eq 0 ]; then
    echo -e "Please provide the name of the server to deploy to as a command line argument.\n"
    usage
    exit 1
fi

git pull
mvn openmrs-sdk:deploy -Ddistro=openmrs-distro.properties -DserverId=$1

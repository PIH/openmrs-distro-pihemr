#!/bin/bash

TOMCAT_DIR="/usr/local/tomcat"
OPENMRS_DIR="/openmrs"
OPENMRS_DATA_DIR="$OPENMRS_DIR/data"
ARTIFACTS_DIR="$OPENMRS_DIR/artifacts"
PIH_CONFIG_DIR="$OPENMRS_DIR/pih_config"
RUNTIME_PROPERTIES_FILE="$OPENMRS_DIR/openmrs-runtime.properties"
SOURCE_WEBAPPS_DIR="$ARTIFACTS_DIR/webapps"
TARGET_WEBAPPS_DIR="$TOMCAT_DIR/webapps"
SOURCE_MODULES_DIR="$ARTIFACTS_DIR/modules"
TARGET_MODULES_DIR="$OPENMRS_DATA_DIR/modules"

# Set up runtime properties based on environment variables
echo "Creating runtime properties file"
if [ -f $RUNTIME_PROPERTIES_FILE ]; then
	echo "Deleting previous runtime properties file"
	rm $RUNTIME_PROPERTIES_FILE
fi
touch $RUNTIME_PROPERTIES_FILE
cat > $RUNTIME_PROPERTIES_FILE <<EOL
connection.url=jdbc:mysql://db:3306/openmrs?autoReconnect=true&sessionVariables=storage_engine=InnoDB&useUnicode=true&characterEncoding=UTF-8
connection.username=openmrs
connection.password=$MYSQL_PASSWORD
module.allow_web_admin=true
auto_update_database=false
application_data_directory=$OPENMRS_DATA_DIR
pih.config=$PIH_CONFIG
pih.config.dir=$PIH_CONFIG_DIR
EOL

cp $ARTIFACTS_DIR/feature_toggles.properties $OPENMRS_DIR

echo "Load in webapps"

rm -fR $TOMCAT_DIR/work/*
rm -fR $TOMCAT_DIR/temp/*
rm -fR $TARGET_WEBAPPS_DIR/*

for webapp_file in $SOURCE_WEBAPPS_DIR/*.*; do
	if [[ $webapp_file == *.tar.gz ]]; then
		tar -xvf $webapp_file -C $TARGET_WEBAPPS_DIR
	else
		cp $webapp_file $TARGET_WEBAPPS_DIR
	fi
done

echo "Load in modules"

rm -fR $TARGET_MODULES_DIR
mkdir $TARGET_MODULES_DIR

for module_file in $SOURCE_MODULES_DIR/*.*; do
	if [[ $module_file == *.zip ]]; then
		echo "Installing modules in $module_file"
		unzip -j $module_file -d $TARGET_MODULES_DIR
	else
		echo "Installing module $module_file"
		cp $module_file $TARGET_MODULES_DIR
	fi
done

# Wait for mysql to be available and start OpenMRS
echo "Starting OpenMRS..."
wait-for-it -t 0 db:3306 -- $CATALINA_HOME/bin/catalina.sh run
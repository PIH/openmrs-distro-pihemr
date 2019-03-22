#!/bin/bash

# This script is used rather than just using the built-in mysql image initialization
# As we need to source our sql file into a specific database specified by the configuration

INITIAL_DB_SQL_PATH="/initial-db.sql"

echo "Checking for initial database file at location $INITIAL_DB_SQL_PATH"
if [ -f $INITIAL_DB_SQL_PATH ]; then
	echo "Found initial database file, loading this in"
	mysql -u root -p$MYSQL_ROOT_PASSWORD $MYSQL_DATABASE -e "source $INITIAL_DB_SQL_PATH"
else
	echo "No initial database found, skipping this step"
fi

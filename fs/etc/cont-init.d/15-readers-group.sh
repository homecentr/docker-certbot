#!/usr/bin/with-contenv ash

source homecentr_create_group
source homecentr_set_s6_env_var
source homecentr_get_s6_env_var

EXEC_USER=$(cat /var/run/s6/container_environment/EXEC_USER)

if [ "$CERTS_GID" == "0" ] || [ "$CERTS_GID" == "" ]
then
  return
fi

# Make sure we really need to create a separate group
if [ "$PGID" != "$CERTS_GID" ]
then
  homecentr_create_group "$CERTS_GID" "ssl-readers"

  # Add executing user to the group
  addgroup "$EXEC_USER" ssl-readers
fi
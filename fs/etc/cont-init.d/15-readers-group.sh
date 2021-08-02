#!/usr/bin/with-contenv ash

EXEC_USER=$(cat /var/run/s6/container_environment/EXEC_USER)

if [ "$CERTS_GID" == "0" ] || [ "$CERTS_GID" == "" ]
then
  # User doesn't want special group ownership, the group owner of the files will be PGID, skip creating the group
  return
fi

if [ "$CERTS_GID" == "$PGID" ]
then
  CERTS_GID=$PGID
fi

# Check if the group already exists
cat /etc/group | grep ^ssl-readers: > /dev/null

if [ $? == 0 ]
then
  # Group already exists, delete it
  delgroup ssl-readers
fi

# Make sure we really need to create a separate group
if [ "$PGID" != "$CERTS_GID" ]
then
  addgroup -g $CERTS_GID ssl-readers
fi
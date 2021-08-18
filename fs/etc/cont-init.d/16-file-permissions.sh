#!/usr/bin/with-contenv ash

echo "Updating certs ownership to $PUID:${CERTS_GID:-$PGID}"
chown "$PUID" -R /data
chgrp "${CERTS_GID:-$PGID}" -R /data
chmod "0750" -R /data
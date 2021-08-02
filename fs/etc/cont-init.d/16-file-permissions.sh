#!/usr/bin/with-contenv ash

chown "$PUID" -R /data
chgrp "${CERTS_GID:-$PGID}" -R /data
chmod "0750" -R /data
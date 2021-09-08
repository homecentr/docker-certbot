#!/usr/bin/with-contenv ash

# Check if directory writable as PUID/PGID, this is why it's in a separate script because it's running PUID/PGID context
runas check-dirs-writable
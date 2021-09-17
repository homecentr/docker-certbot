#!/usr/bin/with-contenv ash

echo "Executing the certbot immediately to ensure the certificate exists..."
runas cron-tick-execute
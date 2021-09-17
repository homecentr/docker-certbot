#!/usr/bin/with-contenv ash

if [[ "$CERTBOT_ARGS" != *"--email"* ]]; then
  echo "The CERTBOT_ARGS variable must contain '--email' as certbot is executed in a non-interactive way."

  exit 1
fi
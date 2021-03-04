FROM certbot/certbot:v1.13.0 as certbot

FROM homecentr/cron-base:1.2.1
 
ARG CERTBOT_PIP_VERSION="1.6.0"

ENV CERTBOT_ARGS=""
ENV CRON_SCHEDULE="30 * * * *"
ENV S6_BEHAVIOUR_IF_STAGE2_FAILS=2

COPY --from=certbot /usr/local/bin/certbot /usr/local/bin/certbot

RUN apk add --no-cache \
      python3=3.8.5-r0 \ 
      # Required from Certbot version 1.5.0
      py3-six=1.15.0-r0 \
      py3-requests=2.23.0-r0 \
      py3-distro=1.5.0-r1	&& \
    apk add --no-cache --virtual deps \
      python3-dev=3.8.5-r0 \
      py3-pip=20.1.1-r0 \
      gcc=9.3.0-r2 \
      libffi-dev=3.3-r2 \
      openssl-dev=1.1.1g-r0	\
      musl-dev=1.1.24-r9 &&\
      pip3 install --upgrade pip==20.1.1 && \
      pip3 install \
        setuptools==47.3.1 \
        acme==${CERTBOT_PIP_VERSION} \
        certbot==${CERTBOT_PIP_VERSION} \
        certbot-dns-cloudflare==${CERTBOT_PIP_VERSION} \
        certbot-dns-cloudxns==${CERTBOT_PIP_VERSION} \
        certbot-dns-digitalocean==${CERTBOT_PIP_VERSION} \
        certbot-dns-dnsimple==${CERTBOT_PIP_VERSION} \
        certbot-dns-dnsmadeeasy==${CERTBOT_PIP_VERSION} \
        certbot-dns-google==${CERTBOT_PIP_VERSION} \
        certbot-dns-linode==${CERTBOT_PIP_VERSION} \
        certbot-dns-luadns==${CERTBOT_PIP_VERSION} \
        certbot-dns-nsone==${CERTBOT_PIP_VERSION} \
        certbot-dns-ovh==${CERTBOT_PIP_VERSION} \
        certbot-dns-rfc2136==${CERTBOT_PIP_VERSION} \
        certbot-dns-route53==${CERTBOT_PIP_VERSION} && \
      apk del deps

COPY ./fs/ /

VOLUME "/etc/letsencrypt"
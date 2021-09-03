FROM certbot/certbot:v1.18.0 as certbot

FROM homecentr/cron-base:2.0.5
 
ARG CERTBOT_PIP_VERSION="1.17.0"

ENV CERTBOT_ARGS=""
ENV CRON_SCHEDULE="30 * * * *"
ENV S6_BEHAVIOUR_IF_STAGE2_FAILS=2

COPY --from=certbot /usr/local/bin/certbot /usr/local/bin/certbot

RUN apk add --no-cache \
      python3=3.9.5-r1 \ 
      # Required from Certbot version 1.5.0
      py3-six=1.15.0-r1	\
      py3-requests=2.25.1-r4 \
      py3-distro=1.5.0-r3	\
      py3-wheel=0.36.2-r2 \
      py3-setuptools=52.0.0-r3 && \
    apk add --no-cache --virtual deps \
      python3-dev=3.9.5-r1 \
      py3-pip=20.3.4-r1 \
      gcc=10.3.1_git20210424-r2 \
      libffi-dev=3.3-r2 \
      openssl-dev=1.1.1l-r0	\
      musl-dev=1.2.2-r3	\
      cargo=1.52.1-r1	\
      && \
      pip3 install --no-cache-dir --upgrade pip==21.1.3 && \
      pip3 install --no-cache-dir \
        # setuptools==57.1.0 \
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

RUN mkdir /logs && chmod 0777 /logs

VOLUME "/etc/letsencrypt"
VOLUME "/data"
VOLUME "/logs"
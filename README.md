[![Project status](https://badgen.net/badge/project%20status/stable%20%26%20actively%20maintaned?color=green)](https://github.com/homecentr/docker-certbot/graphs/commit-activity) [![](https://badgen.net/github/label-issues/homecentr/docker-certbot/bug?label=open%20bugs&color=green)](https://github.com/homecentr/docker-certbot/labels/bug) [![](https://badgen.net/github/release/homecentr/docker-certbot)](https://hub.docker.com/repository/docker/homecentr/certbot)
[![](https://badgen.net/docker/pulls/homecentr/certbot)](https://hub.docker.com/repository/docker/homecentr/certbot) 
[![](https://badgen.net/docker/size/homecentr/certbot)](https://hub.docker.com/repository/docker/homecentr/certbot)

![CI/CD on master](https://github.com/homecentr/docker-certbot/workflows/CI/CD%20on%20master/badge.svg)


# HomeCentr - certbot

The image contains [Certbot](https://certbot.eff.org/) compliant with the HomeCenter docker images standard (S6 overlay, privilege drop etc.). All DNS plugins endorsed by Certbot are installed ([list](https://certbot.eff.org/docs/using.html#dns-plugins)).

This image is supposed to be used as a single purpose certificate manager. It does not include any reverse proxy. The proxy should be running in a separate container and read the certificates from a mounted volume.

## Usage

```yml
version: "3.7"
services:
  certbot
    build: .
    image: homecentr/certbot
    # Example uses Cloudflare dns verification, if you use a different provider, you need to adjust the arguments
    environment:
      CERTBOT_ARGS: "--email john@doe.com --dns-cloudflare --dns-cloudflare-credentials /secrets/cloudflare.ini"
    volumes:
      - cloudflare.ini:/secrets/cloudflare.ini
```

> If you are just testing/are not 100% sure the arguments are correct, add the `--dry-run` which will not actually make the request to Let's encrypt or `--staging` argument which will use [Let's encrypts staging servers](https://letsencrypt.org/docs/staging-environment/) instead of the production ones. The production servers have low rate limits and running too many unsuccessful requests could **block you out for a week**.

## Environment variables

| Name | Default value | Description |
|------|---------------|-------------|
| PUID | 7077 | UID of the user certbot be running as. |
| PGID | 7077 | GID of the user certbot be running as. |
| CERTBOT_ARGS | | Additional arguments passed to certbot's `certonly` command. The argument `--agree-tos` is passed automatically, but you have to provide the `--email` argument. |

## Exposed ports

This image does not expose any ports.

## Volumes

| Container path | Description |
|------------|---------------|
| /etc/letsencrypt | Contains the provisioned certificates. Please note that the "files" in the `/etc/letsencrypt/*` are just symlinks and therefore when mounting, you need to mount either the whole `/etc/letsencrypt/` directory or mount both `/etc/letsencrypt/live` and `/etc/letsencrypt/archive` on same relative levels. |

## Security
The container is regularly scanned for vulnerabilities and updated. Further info can be found in the [Security tab](https://github.com/homecentr/docker-certbot).

### Container user
The container supports privilege drop. Even though the container starts as root, it will use the permissions only to perform the initial set up. The certbots runs as UID/GID provided in the PUID and PGID environment variables.

:warning: Do not change the container user directly using the `user` Docker compose property or using the `--user` argument. This would break the privilege drop logic.
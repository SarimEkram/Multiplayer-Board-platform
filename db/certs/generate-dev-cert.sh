#!/bin/sh
# Generates a self-signed TLS certificate/key pair for the local dev PostgreSQL
# instance (docker-compose.yml). Run this once before `docker compose up`.
#
# Per spec FR-010: connections to the database MUST be encrypted in transit;
# at-rest encryption is explicitly out of scope, so this is the only TLS setup
# required for local development.

set -e
cd "$(dirname "$0")"

openssl req -new -x509 -days 3650 -nodes \
  -out server.crt \
  -keyout server.key \
  -subj "/CN=localhost"

chmod 600 server.key

echo "Generated db/certs/server.crt and db/certs/server.key"

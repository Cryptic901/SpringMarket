#!/bin/bash
helm uninstall auth -n auth

helm uninstall database -n database

helm uninstall ingress-nginx -n infra
helm uninstall infra -n infra

helm uninstall messaging -n messaging

helm uninstall observability -n observability

helm uninstall spring-market -n spring-market

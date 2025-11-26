#!/bin/bash
echo "-----------------------------------------"
echo "=== Scaling observability deployments ==="
kubectl scale deployments --all --replicas=0 -n observability

echo "=== Deleting observability services ==="
kubectl delete svc --all -n observability

echo "-----------------------------------------"
echo "=== Scaling spring-market deployments ==="
kubectl scale deployments --all --replicas=0 -n spring-market

echo "=== Deleting spring-market services ==="
kubectl delete svc --all -n spring-market

echo "-----------------------------------------"
echo "=== Scaling messaging deployments ==="
kubectl scale deployments --all --replicas=0 -n messaging

echo "=== Scaling messaging statefulsets ==="
kubectl scale statefulset --all --replicas=0 -n messaging

echo "=== Deleting messaging services ==="
kubectl delete svc --all -n messaging

echo "-----------------------------------------"
echo "=== Scaling keycloak deployments ==="
kubectl scale deployments --all --replicas=0 -n keycloak

echo "=== Deleting keycloak services ==="
kubectl delete svc --all -n keycloak

echo "-----------------------------------------"
echo "=== Scaling database statefulsets ==="
kubectl scale statefulset --all --replicas=0 -n database

echo "=== Deleting database services ==="
kubectl delete svc --all -n database
#!/bin/bash

helm upgrade --install auth k8s/auth/ \
      --namespace auth \
      --create-namespace

helm upgrade --install database k8s/database/ \
      --namespace database \
      --create-namespace

helm upgrade --install infra k8s/infra/ \
      --namespace infra \
      --create-namespace

helm upgrade --install messaging k8s/messaging/ \
      --namespace messaging \
      --create-namespace

helm upgrade --install observability k8s/observability/ \
      --namespace observability \
      --create-namespace

helm upgrade --install spring-market k8s/spring-market/ \
      --namespace spring-market \
      --create-namespace
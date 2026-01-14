#!/bin/bash
helm install auth k8s/auth/ \
      --namespace auth \
      --create-namespace

helm install database k8s/database/ \
      --namespace database \
      --create-namespace

helm install infra k8s/infra/ \
      --namespace infra \
      --create-namespace

helm install messaging k8s/messaging/ \
      --namespace messaging \
      --create-namespace

helm install observability k8s/observability/ \
      --namespace observability \
      --create-namespace

helm install spring-market k8s/spring-market/ \
      --namespace spring-market \
      --create-namespace
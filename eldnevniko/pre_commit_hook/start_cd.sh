#!/bin/bash

echo "Configuring context for kubectl..."
kubectl config use-context docker-desktop

echo "Starting port forwarding to ArgoCD server..."
kubectl port-forward svc/argocd-server -n argocd 8080:443

echo "Waiting for ArgoCd password..."
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
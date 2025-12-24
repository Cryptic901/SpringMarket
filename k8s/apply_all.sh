#!/bin/bash

set -e  # Остановить выполнение при ошибке

echo "🚀 Starting Kubernetes cluster setup..."

# Цвета для вывода
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Функция для вывода статуса
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Проверка наличия kubectl
if ! command -v kubectl &> /dev/null; then
    print_error "kubectl not found. Please install kubectl first."
    exit 1
fi

# Проверка наличия helm
if ! command -v helm &> /dev/null; then
    print_error "helm not found. Please install helm first."
    exit 1
fi

# Функция для применения манифестов
apply_manifests() {
    local name=$1
    local path=$2
    
    echo ""
    echo "📦 Deploying $name..."
    
    if [ -f "$path/namespace.yaml" ]; then
        kubectl apply -f "$path/namespace.yaml"
        print_status "Namespace created for $name"
    fi
    
    kubectl apply -f "$path" -R
    print_status "$name deployed"
}

# Функция для установки Helm chart
install_helm_chart() {
    local release_name=$1
    local repo_name=$2
    local repo_url=$3
    local chart_name=$4
    local namespace=$5
    local values_file=$6
    
    echo ""
    echo "📦 Installing $release_name via Helm..."
    
    # Проверка, установлен ли уже релиз
    if helm list -n "$namespace" 2>/dev/null | grep -q "^$release_name"; then
        print_warning "$release_name already installed, skipping..."
        return 0
    fi
    
    # Добавление репозитория, если его нет
    if ! helm repo list 2>/dev/null | grep -q "^$repo_name"; then
        print_status "Adding Helm repo: $repo_name"
        helm repo add "$repo_name" "$repo_url"
    else
        print_status "Helm repo $repo_name already exists"
    fi
    
    helm repo update
    
    # Установка с values файлом или без
    if [ -n "$values_file" ] && [ -f "$values_file" ]; then
        helm install "$release_name" "$chart_name" \
            -n "$namespace" \
            --create-namespace \
            --values "$values_file"
    else
        helm install "$release_name" "$chart_name" \
            -n "$namespace" \
            --create-namespace
    fi
    
    print_status "$release_name installed successfully"
}

# Основной процесс установки

# 1. Database
apply_manifests "Database" "k8s/database"

# 2. Messaging
apply_manifests "Messaging" "k8s/messaging"

# 3. Observability
apply_manifests "Observability" "k8s/observability"

# 4. Keycloak
apply_manifests "Keycloak" "k8s/keycloak"

# 5. Spring Market
apply_manifests "Spring Market" "k8s/spring-market"

# 6. Prometheus (Helm)
install_helm_chart \
    "prometheus" \
    "prometheus-community" \
    "https://prometheus-community.github.io/helm-charts" \
    "prometheus-community/kube-prometheus-stack" \
    "monitoring" \
    ""

# 7. Ingress NGINX (Helm)
install_helm_chart \
    "ingress-nginx" \
    "ingress-nginx" \
    "https://kubernetes.github.io/ingress-nginx" \
    "ingress-nginx/ingress-nginx" \
    "infra" \
    "infra/ingress-nginx-values.yaml"

echo ""
echo -e "${GREEN}✅ Kubernetes cluster setup completed successfully!${NC}"
echo ""
echo "🔍 Useful commands:"
echo "  kubectl get pods -A                    # Check all pods"
echo "  kubectl get svc -A                     # Check all services"
echo "  helm list -A                           # List all Helm releases"
echo "  minikube service list                  # List all services (minikube)"
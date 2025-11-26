kubectl apply -f k8s/database/namespace.yaml -R
kubectl apply -f k8s/database -R

kubectl apply -f k8s/messaging/namespace.yaml -R
kubectl apply -f k8s/messaging -R

kubectl apply -f k8s/observability/namespace.yaml -R
kubectl apply -f k8s/observability -R

kubectl apply -f k8s/keycloak/namespace.yaml -R
kubectl apply -f k8s/keycloak -R

kubectl apply -f k8s/spring-market/namespace.yaml -R
kubectl apply -f k8s/spring-market -R
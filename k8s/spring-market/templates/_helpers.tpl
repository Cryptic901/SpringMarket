{{/*
Генерирует checksum конфигов для конкретного сервиса
*/}}
{{- define "spring-market.config.checksum" -}}
{{- $serviceName := .serviceName -}}
{{- $root := .root -}}
{{- $configContent := "" -}}

{{- /* Список всех возможных конфиг-файлов */ -}}
{{- $configFiles := list
  (printf "%s.yaml" $serviceName)
  (printf "%s-cache.yaml" $serviceName)
  (printf "%s-kafka.yaml" $serviceName)
  (printf "%s-security.yaml" $serviceName)
  (printf "%s-jpa.yaml" $serviceName)
  (printf "%s-mongo.yaml" $serviceName)
  (printf "%s-feign.yaml" $serviceName)
-}}

{{- /* Собираем содержимое всех файлов */ -}}
{{- range $fileName := $configFiles }}
  {{- $filePath := printf "%s/config/%s/%s" $root.Template.BasePath $serviceName $fileName -}}
  {{- /* Пытаемся включить файл, игнорируем если не существует */ -}}
  {{- $content := "" -}}
  {{- if $root.Template.Files }}
    {{- $content = include $filePath $root | default "" -}}
  {{- end -}}
  {{- $configContent = printf "%s%s" $configContent $content -}}
{{- end -}}

{{- /* Возвращаем SHA256 хеш */ -}}
{{- $configContent | sha256sum -}}
{{- end -}}
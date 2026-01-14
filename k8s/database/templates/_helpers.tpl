{{- define "postgres.initdb" -}}
CREATE EXTENSION IF NOT EXISTS postgis;
{{- range $serviceName, $database := .Values.postgres.database }}
CREATE DATABASE {{ $database.name }};
CREATE USER {{ $database.username }} WITH PASSWORD '{{ $database.password }}';
GRANT ALL PRIVILEGES ON DATABASE {{ $database.name }} TO {{ $database.username }};
\c {{ $database.name }};
{{- if $database.schema }}
CREATE SCHEMA {{ $database.schema }};
GRANT ALL ON SCHEMA {{ $database.schema }} TO {{ $database.username }};
{{- end }}
GRANT ALL ON SCHEMA public TO {{ $database.username }};
{{- end }}
{{- end -}}
# 🐳 ReportaYA Backend - Docker Quick Start

## 🚀 Inicio Rápido

### Opción 1: Usando el Script Automatizado (Recomendado)

**PowerShell (Windows):**
```powershell
.\docker-build-push.ps1 -Version "v1.0.0"
```

**Bash (Linux/Mac/Git Bash):**
```bash
chmod +x docker-build-push.sh
./docker-build-push.sh v1.0.0
```

### Opción 2: Comandos Manuales

```bash
# 1. Login en Docker Hub
docker login

# 2. Construir la imagen
docker build -t headsrenzo/sprint1reportaya:v1.0.0 .

# 3. Crear tag 'latest'
docker tag headsrenzo/sprint1reportaya:v1.0.0 headsrenzo/sprint1reportaya:latest

# 4. Subir a Docker Hub
docker push headsrenzo/sprint1reportaya:v1.0.0
docker push headsrenzo/sprint1reportaya:latest
```

## 📋 Resumen de Pasos

1. **Login**: `docker login`
2. **Build**: `docker build -t headsrenzo/sprint1reportaya:v1.0.0 .`
3. **Tag**: `docker tag headsrenzo/sprint1reportaya:v1.0.0 headsrenzo/sprint1reportaya:latest`
4. **Push**: `docker push headsrenzo/sprint1reportaya:v1.0.0 && docker push headsrenzo/sprint1reportaya:latest`

## 🔗 Docker Hub

Tu imagen estará disponible en:
**https://hub.docker.com/r/headsrenzo/sprint1reportaya**

## 📦 Descargar y Ejecutar

```bash
docker pull headsrenzo/sprint1reportaya:latest
docker run -d -p 8080:8080 headsrenzo/sprint1reportaya:latest
```

## 📚 Documentación Completa

Ver [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md) para documentación detallada.

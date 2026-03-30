# ===========================================
# Generate-DevForge.ps1 - DevForge Backend (Maven)
# ===========================================

# Caminho raiz do projeto (diretório do script)
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $projectRoot

Write-Host "========================================"
Write-Host "Iniciando DevForge Backend..."
Write-Host "========================================"

# Verifica se existe pom.xml (Maven)
if (Test-Path "pom.xml") {
    Write-Host "Maven detectado. Iniciando Spring Boot..."
    mvn spring-boot:run
} else {
    Write-Host "Nenhum arquivo pom.xml detectado. Este projeto não parece ser Maven."
    exit 1
}

Write-Host "========================================"
Write-Host "DevForge Backend iniciado com sucesso!"
Write-Host "Acesse a API em http://localhost:8080"
Write-Host "========================================"
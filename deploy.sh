# deploy.ps1
Write-Host "🧹 Очистка старых контейнеров и данных..." -ForegroundColor Yellow

# Остановка контейнеров проекта
docker-compose down -v --remove-orphans

# Удаление образов проекта (игнорируем ошибки)
docker rmi easybuy-postgres easybuy-redis easybuy-pgadmin 2>$null

# Очистка неиспользуемых ресурсов
docker system prune -f

Write-Host "🚀 Создание новой инфраструктуры..." -ForegroundColor Green

# Создание и запуск контейнеров
docker-compose up -d --build

Write-Host "⏳ Ожидание готовности сервисов..." -ForegroundColor Cyan

# Ожидание готовности PostgreSQL
do {
    Write-Host "Ожидание PostgreSQL..." -ForegroundColor Yellow
    Start-Sleep -Seconds 2
    $pgReady = docker exec easybuy-postgres pg_isready -U postgres 2>$null
} while ($LASTEXITCODE -ne 0)

# Ожидание готовности Redis
do {
    Write-Host "Ожидание Redis..." -ForegroundColor Yellow
    Start-Sleep -Seconds 2
    $redisReady = docker exec easybuy-redis redis-cli ping 2>$null
} while ($LASTEXITCODE -ne 0)

Write-Host "✅ Инфраструктура готова!" -ForegroundColor Green
Write-Host "📊 Сервисы:" -ForegroundColor White
Write-Host "   PostgreSQL: localhost:5432" -ForegroundColor White
Write-Host "   Redis: localhost:6379" -ForegroundColor White
Write-Host "   pgAdmin: http://localhost:8080" -ForegroundColor White
Write-Host ""
Write-Host "🔐 Учетные данные:" -ForegroundColor White
Write-Host "   PostgreSQL: postgres/postgres" -ForegroundColor White
Write-Host "   pgAdmin: admin@admin.com/admin123" -ForegroundColor White
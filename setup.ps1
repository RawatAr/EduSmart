# EduSmart Platform - Automated Setup Script
# This script sets up and starts the EduSmart platform

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  EduSmart Platform - Automated Setup" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

$ErrorActionPreference = "Continue"

# Function to check if command exists
function Test-Command {
    param([string]$Command)
    $null = Get-Command $Command -ErrorAction SilentlyContinue
    return $?
}

# Step 1: Check Prerequisites
Write-Host "[1/7] Checking Prerequisites..." -ForegroundColor Yellow

if (Test-Command "java") {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "  ✓ Java installed: $javaVersion" -ForegroundColor Green
} else {
    Write-Host "  ✗ Java not found! Please install Java 17" -ForegroundColor Red
    exit 1
}

if (Test-Command "mvn") {
    $mvnVersion = mvn -version | Select-String "Apache Maven"
    Write-Host "  ✓ Maven installed: $mvnVersion" -ForegroundColor Green
} else {
    Write-Host "  ✗ Maven not found! Please install Maven" -ForegroundColor Red
    exit 1
}

if (Test-Command "docker") {
    $dockerVersion = docker --version
    Write-Host "  ✓ Docker installed: $dockerVersion" -ForegroundColor Green
} else {
    Write-Host "  ✗ Docker not found! Please install Docker Desktop" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Step 2: Clean Previous Setup
Write-Host "[2/7] Cleaning Previous Setup..." -ForegroundColor Yellow
Write-Host "  Stopping Docker containers..." -ForegroundColor Gray
docker-compose down 2>$null
Write-Host "  Cleaning Maven build..." -ForegroundColor Gray
mvn clean -q 2>$null
Write-Host "  ✓ Cleanup complete" -ForegroundColor Green
Write-Host ""

# Step 3: Start Database Services
Write-Host "[3/7] Starting Database Services..." -ForegroundColor Yellow
Write-Host "  Starting PostgreSQL and Redis..." -ForegroundColor Gray
docker-compose up -d

Write-Host "  Waiting for services to be ready (30 seconds)..." -ForegroundColor Gray
Start-Sleep -Seconds 30

$containers = docker ps --format "{{.Names}}"
if ($containers -match "edusmart-postgres" -and $containers -match "edusmart-redis") {
    Write-Host "  ✓ PostgreSQL running on port 5432" -ForegroundColor Green
    Write-Host "  ✓ Redis running on port 6380" -ForegroundColor Green
} else {
    Write-Host "  ✗ Services failed to start!" -ForegroundColor Red
    Write-Host "  Check logs: docker-compose logs" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# Step 4: Build Application
Write-Host "[4/7] Building Application..." -ForegroundColor Yellow
Write-Host "  Running Maven build (this may take a few minutes)..." -ForegroundColor Gray

$buildOutput = mvn clean install -DskipTests 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "  ✓ Build successful!" -ForegroundColor Green
} else {
    Write-Host "  ✗ Build failed!" -ForegroundColor Red
    Write-Host "  Check output above for errors" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Common fixes:" -ForegroundColor Yellow
    Write-Host "  1. Run: mvn clean install -U" -ForegroundColor Gray
    Write-Host "  2. Delete target folder and rebuild" -ForegroundColor Gray
    Write-Host "  3. Check TROUBLESHOOTING.md" -ForegroundColor Gray
    exit 1
}
Write-Host ""

# Step 5: Display Service Information
Write-Host "[5/7] Service Information" -ForegroundColor Yellow
Write-Host "  PostgreSQL:" -ForegroundColor Cyan
Write-Host "    Host: localhost" -ForegroundColor Gray
Write-Host "    Port: 5432" -ForegroundColor Gray
Write-Host "    Database: edusmart_db" -ForegroundColor Gray
Write-Host "    Username: postgres" -ForegroundColor Gray
Write-Host "    Password: postgres" -ForegroundColor Gray
Write-Host ""
Write-Host "  Redis:" -ForegroundColor Cyan
Write-Host "    Host: localhost" -ForegroundColor Gray
Write-Host "    Port: 6380" -ForegroundColor Gray
Write-Host ""

# Step 6: Ask to Start Application
Write-Host "[6/7] Ready to Start Application" -ForegroundColor Yellow
Write-Host ""
$start = Read-Host "Start the application now? (Y/N)"

if ($start -eq "Y" -or $start -eq "y") {
    Write-Host ""
    Write-Host "  Starting Spring Boot application..." -ForegroundColor Gray
    Write-Host "  This will open in a new window..." -ForegroundColor Gray
    Write-Host ""
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; mvn spring-boot:run"
    
    Write-Host "  ✓ Application starting in new window" -ForegroundColor Green
    Write-Host "  Watch for 'Started EduSmartApplication' message" -ForegroundColor Gray
    Write-Host ""
    Write-Host "  Waiting for application to start (60 seconds)..." -ForegroundColor Gray
    Start-Sleep -Seconds 60
    
    # Test if application is running
    try {
        $health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -ErrorAction SilentlyContinue
        if ($health.status -eq "UP") {
            Write-Host "  ✓ Application is running!" -ForegroundColor Green
        }
    } catch {
        Write-Host "  ⚠ Application may still be starting..." -ForegroundColor Yellow
        Write-Host "  Check the application window for status" -ForegroundColor Gray
    }
} else {
    Write-Host ""
    Write-Host "  To start manually later, run:" -ForegroundColor Gray
    Write-Host "  mvn spring-boot:run" -ForegroundColor White
}

Write-Host ""

# Step 7: Next Steps
Write-Host "[7/7] Setup Complete!" -ForegroundColor Green
Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  Next Steps" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "1. Test the API:" -ForegroundColor Yellow
Write-Host "   .\test-api.ps1" -ForegroundColor White
Write-Host ""

Write-Host "2. Access the application:" -ForegroundColor Yellow
Write-Host "   http://localhost:8080" -ForegroundColor White
Write-Host ""

Write-Host "3. View health status:" -ForegroundColor Yellow
Write-Host "   http://localhost:8080/actuator/health" -ForegroundColor White
Write-Host ""

Write-Host "4. API Documentation:" -ForegroundColor Yellow
Write-Host "   See README.md for all endpoints" -ForegroundColor White
Write-Host ""

Write-Host "5. Database access:" -ForegroundColor Yellow
Write-Host "   docker exec -it edusmart-postgres psql -U postgres -d edusmart_db" -ForegroundColor White
Write-Host ""

Write-Host "6. View logs:" -ForegroundColor Yellow
Write-Host "   docker-compose logs -f" -ForegroundColor White
Write-Host ""

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  Useful Commands" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Stop services:    docker-compose down" -ForegroundColor Gray
Write-Host "  Start services:   docker-compose up -d" -ForegroundColor Gray
Write-Host "  View containers:  docker ps" -ForegroundColor Gray
Write-Host "  Rebuild app:      mvn clean install" -ForegroundColor Gray
Write-Host "  Run app:          mvn spring-boot:run" -ForegroundColor Gray
Write-Host ""

Write-Host "For troubleshooting, see TROUBLESHOOTING.md" -ForegroundColor Yellow
Write-Host ""
Write-Host "🚀 Ready to build awesome features!" -ForegroundColor Green
Write-Host ""

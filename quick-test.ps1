# Quick API Test

Write-Host "`n=== Testing EduSmart API ===" -ForegroundColor Cyan

# Test 1: Health Check
Write-Host "`n1. Health Check..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get
    Write-Host "   Status: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "   Failed: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# Test 2: Register User
Write-Host "`n2. User Registration..." -ForegroundColor Yellow
$registerBody = @{
    firstName = "John"
    lastName = "Doe"
    username = "johndoe123"
    email = "john123@example.com"
    password = "password123"
    role = "STUDENT"
} | ConvertTo-Json

try {
    $register = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -Body $registerBody -ContentType "application/json"
    Write-Host "   ✓ Registered!" -ForegroundColor Green
    Write-Host "   User: $($register.username)" -ForegroundColor Gray
    Write-Host "   Token: $($register.accessToken.Substring(0,30))..." -ForegroundColor Gray
} catch {
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Yellow
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "   Response: $responseBody" -ForegroundColor Gray
    }
}

# Test 3: Login
Write-Host "`n3. User Login..." -ForegroundColor Yellow
$loginBody = @{
    emailOrUsername = "john123@example.com"
    password = "password123"
} | ConvertTo-Json

try {
    $login = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    Write-Host "   ✓ Logged in!" -ForegroundColor Green
    Write-Host "   User: $($login.username)" -ForegroundColor Gray
    Write-Host "   Role: $($login.role)" -ForegroundColor Gray
    Write-Host "   Token: $($login.accessToken.Substring(0,30))..." -ForegroundColor Gray
} catch {
    Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host "`n=== Tests Complete ===" -ForegroundColor Cyan
Write-Host ""

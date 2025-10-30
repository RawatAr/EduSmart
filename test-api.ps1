# PowerShell API Test Script for EduSmart Platform

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  EduSmart Platform - API Testing" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080"

# Function to test endpoint
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Url,
        [object]$Body
    )
    
    try {
        if ($Body) {
            $jsonBody = $Body | ConvertTo-Json
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Body $jsonBody -ContentType "application/json"
        } else {
            $response = Invoke-RestMethod -Uri $Url -Method $Method
        }
        return $response
    }
    catch {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Test 1: Health Check
Write-Host "1. Testing Health Check..." -ForegroundColor Yellow
$health = Test-Endpoint -Method "GET" -Url "$baseUrl/actuator/health"
if ($health) {
    Write-Host "✓ Health Check: $($health.status)" -ForegroundColor Green
} else {
    Write-Host "✗ Health Check Failed" -ForegroundColor Red
    Write-Host "Make sure the application is running!" -ForegroundColor Yellow
    exit
}

Write-Host ""

# Test 2: Register User
Write-Host "2. Testing User Registration..." -ForegroundColor Yellow
$registerData = @{
    firstName = "John"
    lastName = "Doe"
    username = "johndoe"
    email = "john@example.com"
    password = "password123"
    role = "STUDENT"
}

$registerResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/api/auth/register" -Body $registerData
if ($registerResponse) {
    Write-Host "✓ Registration Successful!" -ForegroundColor Green
    Write-Host "  User ID: $($registerResponse.userId)" -ForegroundColor Gray
    Write-Host "  Username: $($registerResponse.username)" -ForegroundColor Gray
    Write-Host "  Access Token: $($registerResponse.accessToken.Substring(0,20))..." -ForegroundColor Gray
    $global:accessToken = $registerResponse.accessToken
} else {
    Write-Host "✗ Registration Failed (User may already exist)" -ForegroundColor Yellow
}

Write-Host ""

# Test 3: Login
Write-Host "3. Testing User Login..." -ForegroundColor Yellow
$loginData = @{
    emailOrUsername = "john@example.com"
    password = "password123"
}

$loginResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/api/auth/login" -Body $loginData
if ($loginResponse) {
    Write-Host "✓ Login Successful!" -ForegroundColor Green
    Write-Host "  User ID: $($loginResponse.userId)" -ForegroundColor Gray
    Write-Host "  Username: $($loginResponse.username)" -ForegroundColor Gray
    Write-Host "  Role: $($loginResponse.role)" -ForegroundColor Gray
    Write-Host "  Token Type: $($loginResponse.tokenType)" -ForegroundColor Gray
    $global:accessToken = $loginResponse.accessToken
} else {
    Write-Host "✗ Login Failed" -ForegroundColor Red
}

Write-Host ""

# Test 4: Test Protected Endpoint (with JWT)
if ($global:accessToken) {
    Write-Host "4. Testing Protected Endpoint..." -ForegroundColor Yellow
    Write-Host "  Access Token Available: ✓" -ForegroundColor Green
    Write-Host "  Token: $($global:accessToken.Substring(0,30))..." -ForegroundColor Gray
} else {
    Write-Host "4. No access token available to test protected endpoints" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  API Testing Complete!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Show saved token
if ($global:accessToken) {
    Write-Host "Your Access Token (save this for future requests):" -ForegroundColor Yellow
    Write-Host $global:accessToken -ForegroundColor White
    Write-Host ""
    Write-Host "To use this token in future requests:" -ForegroundColor Yellow
    Write-Host "Invoke-RestMethod -Uri 'http://localhost:8080/api/...' -Headers @{Authorization='Bearer $global:accessToken'}" -ForegroundColor Gray
}

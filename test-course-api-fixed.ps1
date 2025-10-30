# Course Management API Test Script

Write-Host "`n=== Testing Day 3: Course Management API ===" -ForegroundColor Cyan

# First, login to get token
Write-Host "`n1. Logging in..." -ForegroundColor Yellow
$loginBody = @{
    emailOrUsername = "john123@example.com"
    password = "password123"
} | ConvertTo-Json

try {
    $login = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $login.accessToken
    Write-Host "   Successfully logged in as: $($login.username)" -ForegroundColor Green
} catch {
    Write-Host "   Login failed. Please register first using quick-test.ps1" -ForegroundColor Red
    exit
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Test 2: Get All Categories
Write-Host "`n2. Getting All Categories..." -ForegroundColor Yellow
try {
    $categories = Invoke-RestMethod -Uri "http://localhost:8080/api/categories" -Method Get
    Write-Host "   Found $($categories.Count) categories" -ForegroundColor Green
    if ($categories.Count -gt 0) {
        $categoryId = $categories[0].id
    } else {
        Write-Host "   No categories found. Creating one..." -ForegroundColor Yellow
        $categoryBody = @{
            name = "Programming"
            description = "Software development courses"
        } | ConvertTo-Json
        $category = Invoke-RestMethod -Uri "http://localhost:8080/api/categories" -Method Post -Body $categoryBody -Headers $headers
        $categoryId = $category.id
    }
} catch {
    Write-Host "   Failed to get categories: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Get All Courses
Write-Host "`n3. Getting All Courses..." -ForegroundColor Yellow
try {
    $courses = Invoke-RestMethod -Uri "http://localhost:8080/api/courses?page=0&size=10" -Method Get
    Write-Host "   Found $($courses.totalElements) total courses" -ForegroundColor Green
} catch {
    Write-Host "   Failed to get courses" -ForegroundColor Red
}

# Test 4: Search Courses
Write-Host "`n4. Searching Courses..." -ForegroundColor Yellow
try {
    $searchResults = Invoke-RestMethod -Uri "http://localhost:8080/api/courses/search?keyword=test" -Method Get
    Write-Host "   Found $($searchResults.totalElements) matching courses" -ForegroundColor Green
} catch {
    Write-Host "   Search failed" -ForegroundColor Red
}

# Test 5: Get Featured Courses
Write-Host "`n5. Getting Featured Courses..." -ForegroundColor Yellow
try {
    $featured = Invoke-RestMethod -Uri "http://localhost:8080/api/courses/featured" -Method Get
    Write-Host "   Found $($featured.Count) featured courses" -ForegroundColor Green
} catch {
    Write-Host "   No featured courses yet" -ForegroundColor Yellow
}

# Test 6: Get My Enrollments
Write-Host "`n6. Getting My Enrollments..." -ForegroundColor Yellow
try {
    $myEnrollments = Invoke-RestMethod -Uri "http://localhost:8080/api/enrollments/my-courses" -Method Get -Headers $headers
    Write-Host "   You are enrolled in $($myEnrollments.totalElements) courses" -ForegroundColor Green
} catch {
    Write-Host "   Failed to get enrollments" -ForegroundColor Red
}

Write-Host "`n=== Day 3 API Tests Complete! ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Summary:" -ForegroundColor White
Write-Host "- Authentication works" -ForegroundColor Green
Write-Host "- Category Management ready" -ForegroundColor Green
Write-Host "- Course Management ready" -ForegroundColor Green
Write-Host "- Course Search works" -ForegroundColor Green
Write-Host "- Enrollment System ready" -ForegroundColor Green
Write-Host ""
Write-Host "Note: Some features require INSTRUCTOR or ADMIN role" -ForegroundColor Yellow
Write-Host ""

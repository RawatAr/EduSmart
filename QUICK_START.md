# 🚀 EduSmart - Quick Start Guide

## Getting Started in 5 Minutes

This guide will help you get EduSmart running on your local machine quickly.

---

## ✅ Prerequisites Check

Before starting, make sure you have:

```bash
# Check Java version (need 17 or higher)
java -version

# Check Maven (need 3.6+)
mvn -version

# Check if PostgreSQL is installed
psql --version

# Check if Redis is installed
redis-cli --version
```

---

## 🐳 Option 1: Using Docker (Easiest - Recommended)

### Step 1: Start Services
```bash
# Navigate to project directory
cd EduSmart

# Start PostgreSQL and Redis
docker-compose up -d postgres redis

# Wait for services to be ready (about 10 seconds)
```

### Step 2: Run Application
```bash
# Run with Maven
mvn spring-boot:run

# OR build and run JAR
mvn clean package -DskipTests
java -jar target/edusmart-0.0.1-SNAPSHOT.jar
```

### Step 3: Access Application
- **Home**: http://localhost:8080
- **Login**: http://localhost:8080/login
- **Register**: http://localhost:8080/register
- **API Docs**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

---

## 💻 Option 2: Manual Setup

### Step 1: Set Up PostgreSQL

```bash
# Start PostgreSQL (if not running)
# On Windows: Start from Services
# On Mac: brew services start postgresql
# On Linux: sudo systemctl start postgresql

# Create database and user
psql -U postgres
```

```sql
CREATE DATABASE edusmart_db;
CREATE USER edusmart_user WITH PASSWORD 'edusmart_password';
GRANT ALL PRIVILEGES ON DATABASE edusmart_db TO edusmart_user;
\q
```

### Step 2: Set Up Redis

```bash
# Using Docker (easiest)
docker run -d -p 6379:6379 --name redis redis:7-alpine

# OR install Redis locally
# Windows: Download from https://github.com/microsoftarchive/redis/releases
# Mac: brew install redis && brew services start redis
# Linux: sudo apt-get install redis-server && sudo systemctl start redis
```

### Step 3: Configure Application

Edit `src/main/resources/application.properties`:

```properties
# Update if your database credentials are different
spring.datasource.url=jdbc:postgresql://localhost:5432/edusmart_db
spring.datasource.username=edusmart_user
spring.datasource.password=edusmart_password

# Update if your Redis host is different
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Update JWT secret (use a secure random string)
application.security.jwt.secret-key=your-256-bit-secret-key-here

# Choose active profile
spring.profiles.active=dev
```

### Step 4: Run Application

```bash
# Clean and build
mvn clean install

# Run
mvn spring-boot:run

# OR run the JAR
java -jar target/edusmart-0.0.1-SNAPSHOT.jar
```

---

## 🎯 Quick Test

### 1. Check if everything is running:

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Should return:
# {"status":"UP"}
```

### 2. Test the Homepage:
- Open browser: http://localhost:8080
- You should see the modern EduSmart homepage

### 3. Test Login:
- Go to: http://localhost:8080/login
- You should see the beautiful login page

### 4. Test Registration:
- Go to: http://localhost:8080/register
- You should see the registration page with password strength indicator

---

## 🔑 Default Test Accounts

After running the application, you can create test accounts or use these if seeded:

### Admin Account
- **Username**: admin
- **Password**: admin123
- **Role**: ADMIN

### Instructor Account
- **Username**: instructor
- **Password**: instructor123
- **Role**: INSTRUCTOR

### Student Account
- **Username**: student
- **Password**: student123
- **Role**: STUDENT

> **Note**: These are for testing only. In production, create accounts through the registration page.

---

## 🐛 Common Issues & Solutions

### Issue 1: Port Already in Use

**Error**: `Port 8080 is already in use`

**Solution**:
```bash
# Find process using port 8080
# Windows:
netstat -ano | findstr :8080

# Mac/Linux:
lsof -i :8080

# Kill the process or change the port in application.properties
server.port=8081
```

### Issue 2: Database Connection Failed

**Error**: `Connection to localhost:5432 refused`

**Solution**:
```bash
# Check if PostgreSQL is running
# Windows: Check Services
# Mac/Linux:
sudo systemctl status postgresql

# If not running, start it
sudo systemctl start postgresql
```

### Issue 3: Redis Connection Failed

**Error**: `Unable to connect to Redis`

**Solution**:
```bash
# Check if Redis is running
redis-cli ping
# Should return: PONG

# If not running:
# Docker:
docker start redis

# Local:
redis-server
```

### Issue 4: Maven Build Fails

**Error**: `Failed to execute goal`

**Solution**:
```bash
# Clean Maven repository
mvn dependency:purge-local-repository

# Clean and rebuild
mvn clean install -U

# Skip tests if they fail
mvn clean install -DskipTests
```

### Issue 5: Template Not Found

**Error**: `Error resolving template [index]`

**Solution**:
```bash
# Make sure you're running from the EduSmart directory
cd /path/to/EduSmart

# Rebuild the project
mvn clean package

# Check if templates exist
ls src/main/resources/templates/
```

---

## 📱 Testing Different Features

### 1. Test Course Catalog
```bash
# Access homepage
http://localhost:8080

# Should show:
- Hero section with stats
- Course search and filters
- Course cards
- Features section
```

### 2. Test Authentication
```bash
# Register new user
http://localhost:8080/register

# Login
http://localhost:8080/login

# Access profile
http://localhost:8080/profile
```

### 3. Test Dashboards
```bash
# Student dashboard
http://localhost:8080/dashboard/student

# Instructor dashboard
http://localhost:8080/dashboard/instructor
```

### 4. Test API Endpoints
```bash
# Get all courses (using curl)
curl http://localhost:8080/api/courses

# Register user (API)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"Test123!","role":"STUDENT"}'
```

---

## 🎨 Customization Quick Tips

### Change Primary Color
Edit `src/main/resources/static/css/custom.css`:
```css
:root {
    --primary-color: #YOUR_COLOR;
    --primary-dark: #YOUR_DARK_COLOR;
}
```

### Change Application Name
Edit `src/main/resources/templates/layout.html`:
```html
<div class="brand-name fw-bold text-primary">Your App Name</div>
```

### Change Port
Edit `src/main/resources/application.properties`:
```properties
server.port=YOUR_PORT
```

---

## 📊 Monitoring & Debugging

### View Logs
```bash
# Console logs show in terminal

# Or check log files
tail -f logs/spring-boot-application.log
```

### Access Actuator Endpoints
```bash
# Health check
http://localhost:8080/actuator/health

# Application info
http://localhost:8080/actuator/info

# Metrics
http://localhost:8080/actuator/metrics

# All actuator endpoints
http://localhost:8080/actuator
```

### Debug Mode
```bash
# Run with debug logging
mvn spring-boot:run -Dspring-boot.run.arguments=--logging.level.org.springframework=DEBUG

# Or in application.properties
logging.level.com.edusmart=DEBUG
```

---

## 🚀 Next Steps

Once everything is running:

1. ✅ **Explore the UI**: Navigate through all pages
2. ✅ **Create Test Data**: Register users, create courses
3. ✅ **Test Features**: Try enrollment, progress tracking
4. ✅ **Check API Docs**: http://localhost:8080/swagger-ui.html
5. ✅ **Review Code**: Understand the architecture
6. ✅ **Customize**: Make it your own!

---

## 📚 Additional Resources

- **Full Documentation**: See `README.md`
- **Recent Changes**: See `FIXES_AND_IMPROVEMENTS.md`
- **Architecture**: See `README.md` - Architecture section
- **API Documentation**: http://localhost:8080/swagger-ui.html (when running)

---

## 🆘 Need Help?

If you encounter any issues:

1. **Check the console output** for error messages
2. **Review the logs** in `/logs` directory
3. **Verify all prerequisites** are installed correctly
4. **Check if all services are running** (PostgreSQL, Redis)
5. **Try the Docker setup** if manual setup fails

---

## ✅ Success Checklist

- [ ] Java 17+ installed
- [ ] Maven installed
- [ ] PostgreSQL running
- [ ] Redis running
- [ ] Application starts without errors
- [ ] Can access http://localhost:8080
- [ ] Can see login page
- [ ] Can register new user
- [ ] Can create and view courses
- [ ] Can access dashboards

If all checkboxes are checked, you're ready to develop! 🎉

---

**Happy Coding!** 🚀

For questions or issues, check the `FIXES_AND_IMPROVEMENTS.md` file or create an issue on GitHub.

# 🚀 EduSmart Deployment Guide

Complete guide for deploying EduSmart LMS to production.

---

## 📋 Pre-Deployment Checklist

### ✅ Required
- [ ] Java 17+ installed
- [ ] PostgreSQL 15+ running
- [ ] Redis 7+ running (optional for caching)
- [ ] `.env` file configured with production values
- [ ] SMTP credentials configured for emails
- [ ] JWT secret key generated (256-bit minimum)
- [ ] Database created and accessible
- [ ] Application tested locally

### 🔒 Security Checklist
- [ ] `.env` file NOT committed to Git
- [ ] Strong JWT secret key generated
- [ ] Production database password set
- [ ] Gmail App Password configured
- [ ] CORS origins restricted to your domain
- [ ] HTTPS enabled (production only)
- [ ] Firewall rules configured

---

## 🛠 Environment Configuration

### 1. Create `.env` File

Copy `.env.template` to `.env` and configure:

```bash
cp .env.template .env
```

### 2. Configure Environment Variables

#### Database Configuration
```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=edusmart
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password_here
```

#### JWT Configuration
Generate a secure JWT secret:
```bash
# Linux/Mac
openssl rand -base64 32

# Windows (PowerShell)
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

```properties
JWT_SECRET=your_generated_secret_here
JWT_EXPIRATION=86400000
```

#### Email Configuration (Gmail)
1. Enable 2-Factor Authentication in your Google Account
2. Generate App Password: https://myaccount.google.com/apppasswords
3. Use the 16-character password:

```properties
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your_16_char_app_password_no_spaces
MAIL_FROM=noreply@edusmart.com
```

#### Redis Configuration (Optional)
```properties
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
```

---

## 🐳 Docker Deployment (Recommended)

### 1. Using Docker Compose

```bash
# Start all services
docker-compose up -d

# Check logs
docker-compose logs -f

# Stop services
docker-compose down

# Fresh start (delete data)
docker-compose down -v && docker-compose up -d
```

### 2. Build Docker Image

```bash
# Build application
mvn clean package -DskipTests

# Build Docker image
docker build -t edusmart:latest .

# Run container
docker run -d \
  --name edusmart-app \
  -p 8080:8080 \
  --env-file .env \
  edusmart:latest
```

---

## 💻 Manual Deployment

### 1. Build Application

```bash
# Clean and build
mvn clean package -DskipTests

# JAR file will be in target/
ls target/edusmart-platform-*.jar
```

### 2. Run Application

```bash
# Using Maven
mvn spring-boot:run

# Using JAR file
java -jar target/edusmart-platform-1.0.0.jar

# With custom profile
java -jar target/edusmart-platform-1.0.0.jar --spring.profiles.active=prod
```

### 3. Run as Background Service

#### Linux (systemd)
Create `/etc/systemd/system/edusmart.service`:

```ini
[Unit]
Description=EduSmart LMS Application
After=syslog.target network.target

[Service]
User=edusmart
WorkingDirectory=/opt/edusmart
ExecStart=/usr/bin/java -jar /opt/edusmart/edusmart-platform-1.0.0.jar
SuccessExitStatus=143
StandardOutput=journal
StandardError=journal
SyslogIdentifier=edusmart

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable edusmart
sudo systemctl start edusmart
sudo systemctl status edusmart
```

#### Windows Service
Use [NSSM](https://nssm.cc/) or [WinSW](https://github.com/winsw/winsw)

---

## ☁️ Cloud Deployment

### AWS Elastic Beanstalk

1. **Install AWS CLI & EB CLI**
```bash
pip install awsebcli
```

2. **Initialize EB**
```bash
eb init -p java-17 edusmart
```

3. **Create Environment**
```bash
eb create edusmart-prod
```

4. **Deploy**
```bash
mvn clean package
eb deploy
```

### Heroku

1. **Install Heroku CLI**
```bash
# Install from: https://devcenter.heroku.com/articles/heroku-cli
```

2. **Create Heroku App**
```bash
heroku create edusmart-lms
```

3. **Add PostgreSQL**
```bash
heroku addons:create heroku-postgresql:hobby-dev
```

4. **Set Environment Variables**
```bash
heroku config:set JWT_SECRET=your_secret
heroku config:set MAIL_USERNAME=your_email
heroku config:set MAIL_PASSWORD=your_app_password
```

5. **Deploy**
```bash
git push heroku main
```

### Google Cloud Run

1. **Build Container**
```bash
gcloud builds submit --tag gcr.io/PROJECT_ID/edusmart
```

2. **Deploy**
```bash
gcloud run deploy edusmart \
  --image gcr.io/PROJECT_ID/edusmart \
  --platform managed \
  --allow-unauthenticated
```

---

## 🗄️ Database Setup

### PostgreSQL

#### 1. Create Database
```sql
CREATE DATABASE edusmart;
CREATE USER edusmart_user WITH ENCRYPTED PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE edusmart TO edusmart_user;
```

#### 2. Run Migrations
The application will automatically create tables on first run if configured:
```properties
spring.jpa.hibernate.ddl-auto=update
```

**For production, use:**
```properties
spring.jpa.hibernate.ddl-auto=validate
```
And use Flyway/Liquibase for migrations.

### Redis (Optional but Recommended)

#### Install Redis
```bash
# Ubuntu/Debian
sudo apt-get install redis-server

# macOS
brew install redis

# Start Redis
redis-server

# Test connection
redis-cli ping
```

#### Enable Redis Caching
In `application.properties`:
```properties
spring.cache.type=redis
```

---

## 🌐 Nginx Configuration

### Reverse Proxy Setup

Create `/etc/nginx/sites-available/edusmart`:

```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;

    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # WebSocket support
    location /ws {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}
```

Enable site:
```bash
sudo ln -s /etc/nginx/sites-available/edusmart /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### SSL Certificate (Let's Encrypt)

```bash
sudo apt-get install certbot python3-certbot-nginx
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com
```

---

## 📊 Monitoring & Logging

### Application Logs

```bash
# View logs
tail -f logs/spring.log

# With Docker
docker logs -f edusmart-app
```

### Health Check Endpoints

```bash
# Application health
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics
```

### Monitoring Tools
- **Prometheus + Grafana** for metrics
- **ELK Stack** for log aggregation
- **Spring Boot Admin** for monitoring

---

## 🔧 Production Configuration

### application-prod.properties

```properties
# Server
server.port=8080
server.compression.enabled=true
server.http2.enabled=true

# Database
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Caching
spring.cache.type=redis

# Logging
logging.level.root=WARN
logging.level.com.edusmart=INFO
logging.file.name=logs/edusmart.log

# Security
spring.security.require-ssl=true

# Actuator (Restrict access)
management.endpoints.web.exposure.include=health,metrics,info
```

---

## 🧪 Smoke Testing

After deployment, verify these endpoints:

```bash
# 1. Health Check
curl https://yourdomain.com/actuator/health

# 2. Homepage
curl https://yourdomain.com/

# 3. API
curl https://yourdomain.com/api/courses

# 4. WebSocket
# Test with browser console:
# var socket = new SockJS('https://yourdomain.com/ws');
# var stompClient = Stomp.over(socket);
```

---

## 🔄 Continuous Deployment

### GitHub Actions

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
    
    - name: Deploy to Server
      uses: appleboy/scp-action@master
      with:
        host: ${{ secrets.SERVER_HOST }}
        username: ${{ secrets.SERVER_USER }}
        key: ${{ secrets.SSH_KEY }}
        source: "target/*.jar"
        target: "/opt/edusmart"
    
    - name: Restart Service
      uses: appleboy/ssh-action@master
      with:
        host: ${{ secrets.SERVER_HOST }}
        username: ${{ secrets.SERVER_USER }}
        key: ${{ secrets.SSH_KEY }}
        script: |
          sudo systemctl restart edusmart
          sleep 10
          sudo systemctl status edusmart
```

---

## 🐛 Troubleshooting

### Application Won't Start

1. **Check logs**
```bash
cat logs/spring.log
```

2. **Verify database connection**
```bash
psql -h localhost -U postgres -d edusmart
```

3. **Check port availability**
```bash
netstat -tulpn | grep 8080
```

### Redis Connection Issues

```bash
# Test Redis
redis-cli ping

# Check if running
systemctl status redis
```

### Email Not Sending

1. Verify Gmail App Password (no spaces)
2. Check 2FA is enabled
3. Review logs for SMTP errors

---

## 📝 Post-Deployment

- [ ] Create admin account
- [ ] Test all major features
- [ ] Monitor logs for errors
- [ ] Set up backups
- [ ] Configure monitoring alerts
- [ ] Update DNS records
- [ ] Test SSL certificate
- [ ] Performance testing

---

## 🔐 Security Best Practices

1. **Never commit `.env` file**
2. **Use strong passwords (16+ chars)**
3. **Rotate JWT secrets regularly**
4. **Enable HTTPS in production**
5. **Restrict Actuator endpoints**
6. **Keep dependencies updated**
7. **Regular security audits**
8. **Database backups**

---

## 📞 Support

For deployment issues:
- Check logs first
- Review this guide
- Create GitHub issue with logs
- Contact: support@edusmart.com

---

**🎉 Congratulations on deploying EduSmart!**

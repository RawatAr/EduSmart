# EduSmart - Interactive Learning Management System

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](https://www.docker.com/)

EduSmart is a comprehensive, enterprise-grade Learning Management System (LMS) built with modern technologies. It provides an interactive learning experience with real-time features, advanced security, and performance optimization.

## 🚀 Features

### Core Learning Features
- **Course Management**: Create, organize, and manage educational content
- **Interactive Assessments**: Quizzes and assignments with automatic grading
- **Progress Tracking**: Visual progress indicators and completion analytics
- **Discussion Forums**: Real-time communication between students and instructors

### Advanced Technical Features
- **Real-time Notifications**: WebSocket and Server-Sent Events for instant updates
- **Smart Caching**: Redis-based caching for improved performance
- **Advanced Security**: JWT authentication with role-based access control
- **File Management**: Robust upload/download system for course materials
- **Analytics Dashboard**: Comprehensive learning analytics and reporting

## 🏗️ Architecture

### Technology Stack

**Backend:**
- **Framework**: Spring Boot 3.2.5
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Security**: Spring Security 6 with JWT
- **Real-time**: WebSocket with STOMP
- **Documentation**: OpenAPI/Swagger

**Frontend:**
- **Templates**: Thymeleaf
- **Styling**: Bootstrap 5
- **JavaScript**: ES6+ with WebSocket client
- **Responsive**: Mobile-first design

### System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Browser   │    │   Nginx Proxy   │    │  Spring Boot    │
│                 │    │                 │    │   Application   │
│ - Thymeleaf     │◄──►│ - Load Balance  │◄──►│                 │
│ - Bootstrap     │    │ - SSL/TLS       │    │ - REST APIs     │
│ - JavaScript    │    │ - Static Files  │    │ - WebSocket     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
┌─────────────────┐    ┌─────────────────┐             │
│   PostgreSQL    │    │     Redis       │◄────────────┘
│                 │    │                 │
│ - User Data     │    │ - Sessions      │
│ - Course Data   │    │ - Cache         │
│ - Analytics     │    │ - WebSocket     │
└─────────────────┘    └─────────────────┘
```

## 📋 Prerequisites

- **Java**: 17 or higher
- **Maven**: 3.6+ or Docker
- **PostgreSQL**: 15+ (or Docker)
- **Redis**: 7+ (or Docker)
- **Docker**: Optional, for containerized deployment

## 🚀 Quick Start

### Using Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/edusmart.git
   cd edusmart
   ```

2. **Set environment variables**
   ```bash
   export JWT_SECRET_KEY="your-256-bit-secret-key-here"
   ```

3. **Start the application**
   ```bash
   docker-compose up -d
   ```

4. **Access the application**
   - Application: http://localhost:8080
   - Health Check: http://localhost:8080/actuator/health
   - API Docs: http://localhost:8080/swagger-ui.html

### Manual Setup

1. **Database Setup**
   ```sql
   CREATE DATABASE edusmart_db;
   CREATE USER edusmart_user WITH PASSWORD 'edusmart_password';
   GRANT ALL PRIVILEGES ON DATABASE edusmart_db TO edusmart_user;
   ```

2. **Redis Setup**
   ```bash
   # Using Docker
   docker run -d -p 6379:6379 redis:7-alpine

   # Or install Redis locally
   ```

3. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## 🔧 Configuration

### Application Properties

The application supports multiple environments:

- **Development**: `application-dev.properties`
- **Production**: `application-prod.properties`
- **Actuator**: `application-actuator.properties`

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `JWT_SECRET_KEY` | JWT signing key (256-bit) | Generated |
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/edusmart_db` |
| `SPRING_DATA_REDIS_HOST` | Redis host | `localhost` |
| `FILE_UPLOAD_DIR` | File upload directory | `./uploads` |

## 📚 API Documentation

### Authentication Endpoints

```http
POST /api/auth/register
POST /api/auth/authenticate
```

### Course Management

```http
GET    /api/courses
GET    /api/courses/{id}
POST   /api/courses/instructor/{instructorId}/category/{categoryId}
PUT    /api/courses/{id}/category/{categoryId}
DELETE /api/courses/{id}
```

### Real-time Features

- **WebSocket Endpoint**: `/ws`
- **Progress Updates**: `/topic/course/{courseId}/progress`
- **Discussion**: `/topic/course/{courseId}/discussion`

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify -Dspring.profiles.active=test
```

### Security Tests
```bash
mvn test -Dtest=SecurityIntegrationTest
```

## 📊 Monitoring

### Health Checks
- **Application Health**: `/actuator/health`
- **Database Health**: `/actuator/health/db`
- **Redis Health**: `/actuator/health/redis`

### Metrics
- **Prometheus**: `/actuator/prometheus`
- **Metrics**: `/actuator/metrics`
- **JVM Info**: `/actuator/info`

## 🚀 Deployment

### Production Deployment

1. **Build the application**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Create deployment package**
   ```bash
   docker build -t edusmart:latest .
   ```

3. **Deploy with Docker Compose**
   ```bash
   docker-compose -f docker-compose.prod.yml up -d
   ```

### Cloud Deployment

The application is ready for deployment on:
- **AWS**: ECS, EKS, or Elastic Beanstalk
- **Google Cloud**: Cloud Run or GKE
- **Azure**: Container Instances or AKS
- **Heroku**: Direct deployment

## 🔒 Security

### Authentication & Authorization
- JWT-based authentication
- Role-based access control (Student, Instructor, Admin)
- Password encryption with BCrypt
- Session management with Redis

### Security Headers
- CORS configuration
- CSRF protection
- Content Security Policy
- HTTPS enforcement in production

## 📈 Performance

### Caching Strategy
- **Redis Cache**: Course data, user sessions
- **Cache Warming**: Popular courses pre-loaded
- **TTL Configuration**: Intelligent cache expiration

### Database Optimization
- **Connection Pooling**: HikariCP configuration
- **Query Optimization**: Custom repository methods
- **Indexing**: Strategic database indexes

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot Team for the excellent framework
- Bootstrap Team for the responsive UI components
- PostgreSQL and Redis communities
- All contributors and users

## 📞 Support

For support and questions:
- **Issues**: [GitHub Issues](https://github.com/your-username/edusmart/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-username/edusmart/discussions)
- **Email**: support@edusmart.com

---

**EduSmart** - Empowering Education Through Technology 🚀
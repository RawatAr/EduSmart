# EduSmart - Interactive Learning Management System

> A comprehensive Smart E-Learning Platform built with Spring Boot 3.x, featuring real-time notifications, advanced security, performance optimization, and interactive learning experiences.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Redis](https://img.shields.io/badge/Redis-7-red)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Deployment](#deployment)

---

## 🎯 Overview

EduSmart is an advanced Learning Management System that incorporates industry-relevant features similar to modern platforms like Coursera, Udemy, and Khan Academy. The platform enables interactive learning experiences with real-time progress tracking, smart notifications, and comprehensive analytics.

### Key Differentiators
- **Real-time Communication**: WebSocket & Server-Sent Events for instant updates
- **Performance Optimized**: Redis caching for frequently accessed data
- **Enterprise Security**: JWT authentication with role-based access control
- **Interactive Learning**: Automated assessment grading and progress tracking
- **Modern Architecture**: Clean layered architecture with Spring Boot 3.x

---

## ✨ Features

### Core Learning Features
- ✅ **Course Management**: Create, organize, and manage educational content
- ✅ **Interactive Assessments**: Quizzes and assignments with automated grading
- ✅ **Progress Tracking**: Visual progress indicators and completion analytics
- ✅ **Discussion Forums**: Real-time communication between students and instructors
- ✅ **Certificate System**: Automated certificate generation upon course completion
- ✅ **File Management**: Robust upload/download system for course materials

### Advanced Technical Features
- 🔔 **Smart Notifications**: Real-time updates using WebSocket & SSE
- 📊 **Performance Analytics**: Detailed learning analytics and reporting
- 🔐 **Secure Authentication**: JWT-based security with Spring Security 6
- ⚡ **Optimized Performance**: Redis caching with strategic cache warming
- 💬 **Real-time Collaboration**: Live discussions and instant messaging
- 📈 **Monitoring**: Spring Boot Actuator for health checks and metrics

---

## 🛠 Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security 6 with JWT
- **Data**: Spring Data JPA with Hibernate
- **Caching**: Redis with Spring Data Redis
- **Real-time**: WebSocket + STOMP, Server-Sent Events
- **Monitoring**: Spring Boot Actuator

### Database & Storage
- **Primary Database**: PostgreSQL 15
- **Cache**: Redis 7
- **File Storage**: Local file system with structured organization

### Frontend
- **Template Engine**: Thymeleaf
- **UI Framework**: Bootstrap 5
- **JavaScript**: ES6+ with modern features
- **Real-time Client**: WebSocket client, SSE EventSource

### DevOps
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven
- **Version Control**: Git

---

## 🏗 Architecture

### Layered Architecture
```
├── Controller Layer    (REST endpoints & web controllers)
├── Service Layer       (Business logic & transactions)
├── Repository Layer    (Data access & persistence)
├── Entity Layer        (JPA entities & relationships)
├── DTO Layer          (Data transfer objects)
├── Security Layer     (Authentication & authorization)
└── Config Layer       (Application configuration)
```

### Key Design Patterns
- **Repository Pattern**: Data access abstraction
- **Service Pattern**: Business logic encapsulation
- **DTO Pattern**: Data transfer optimization
- **Singleton Pattern**: Spring bean management
- **Factory Pattern**: Object creation strategies

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15 (via Docker)
- Redis 7 (via Docker)

### Installation Steps

#### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/edusmart.git
cd edusmart
```

#### 2. Start Docker Services
```bash
# Start PostgreSQL and Redis
docker-compose up -d

# Verify containers are running
docker ps
```

#### 3. Configure Environment Variables

**IMPORTANT**: Never commit `.env` file to Git!

Create `.env` file from template:
```bash
cp .env.template .env
```

Configure your `.env` file:
```properties
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=edusmart
DB_USERNAME=postgres
DB_PASSWORD=your_password

# JWT Secret (Generate: openssl rand -base64 32)
JWT_SECRET=your_256_bit_secret_here
JWT_EXPIRATION=86400000

# Email (Gmail App Password - no spaces!)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your16charapppassword
MAIL_FROM=noreply@edusmart.com

# Redis (Optional)
REDIS_HOST=localhost
REDIS_PORT=6379
```

**Note**: See `DEPLOYMENT_GUIDE.md` for detailed configuration instructions.

#### 4. Build & Run
```bash
# Clean and build
mvn clean install

# Run application
mvn spring-boot:run
```

#### 5. Access the Application
- **Homepage**: http://localhost:8080
- **Login**: http://localhost:8080/login
- **API Docs**: http://localhost:8080/actuator

### Sample Credentials

**Students** (Password: `Student@123`):
- alice@edusmart.com
- bob@edusmart.com
- charlie@edusmart.com
- diana@edusmart.com
- eva@edusmart.com

**Instructors** (Password: `Instructor@123`):
- david@edusmart.com
- emily@edusmart.com
- frank@edusmart.com
- grace@edusmart.com
- henry@edusmart.com

---

## 📡 API Documentation

### Authentication Endpoints
```
POST   /api/auth/register     - Register new user
POST   /api/auth/login        - Login and get JWT token
POST   /api/auth/refresh      - Refresh JWT token
GET    /api/auth/profile      - Get user profile
```

### Course Endpoints
```
GET    /api/courses           - Get all courses
GET    /api/courses/{id}      - Get course by ID
POST   /api/courses           - Create course (Instructor)
PUT    /api/courses/{id}      - Update course (Instructor)
DELETE /api/courses/{id}      - Delete course (Instructor)
POST   /api/courses/{id}/enroll - Enroll in course (Student)
```

### Assessment Endpoints
```
GET    /api/assessments/{courseId}     - Get course assessments
POST   /api/assessments                - Create assessment (Instructor)
POST   /api/assessments/{id}/submit    - Submit assessment (Student)
GET    /api/assessments/{id}/results   - Get assessment results
```

### Notification Endpoints
```
GET    /api/notifications              - Get user notifications
PUT    /api/notifications/{id}/read    - Mark notification as read
DELETE /api/notifications/{id}         - Delete notification
WebSocket: /ws/notifications            - Real-time notifications
```

---

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage
```bash
mvn jacoco:report
# View report at target/site/jacoco/index.html
```

### Manual Testing
See `LOGIN_CREDENTIALS.txt` for test accounts with different roles.

---

## 🐳 Docker Commands

### Start Services
```bash
docker-compose up -d
```

### Stop Services
```bash
docker-compose down
```

### Fresh Start (Delete Data)
```bash
docker-compose down -v
docker-compose up -d
```

### View Logs
```bash
docker logs edusmart-postgres
docker logs edusmart-redis
```

### Database Access
```bash
# Connect to PostgreSQL
docker exec -it edusmart-postgres psql -U postgres -d edusmart_db

# Connect to Redis
docker exec -it edusmart-redis redis-cli
```

---

## 📊 Monitoring

### Spring Boot Actuator Endpoints
```
GET /actuator/health        - Application health
GET /actuator/metrics       - Application metrics
GET /actuator/env           - Environment properties
GET /actuator/beans         - Spring beans
```

---

## 🔒 Security Features

- **JWT Authentication**: Stateless token-based authentication
- **Password Encryption**: BCrypt hashing algorithm
- **Role-Based Access**: Student, Instructor, Admin roles
- **CORS Configuration**: Configured for frontend integration
- **SQL Injection Prevention**: Parameterized queries with JPA
- **XSS Protection**: Content Security Policy headers

---

## ⚡ Performance Optimization

### Caching Strategy
```java
@Cacheable(value = "courses", key = "#categoryId")
public List<Course> getCoursesByCategory(Long categoryId)

@CacheEvict(value = "courses", allEntries = true)
public Course updateCourse(Course course)
```

### Database Optimization
- Connection pooling with HikariCP
- Lazy loading for entity relationships
- Query optimization with JPA criteria
- Database indexing on frequently queried fields

---

## 📁 Project Structure
```
edusmart/
├── src/
│   ├── main/
│   │   ├── java/com/edusmart/
│   │   │   ├── config/          # Application configuration
│   │   │   ├── controller/      # REST & Web controllers
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── exception/       # Custom exceptions
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── security/        # Security configuration
│   │   │   ├── service/         # Business logic
│   │   │   └── util/            # Utility classes
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf templates
│   │       ├── static/          # CSS, JS, images
│   │       └── application.properties
│   └── test/                    # Unit & integration tests
├── docker-compose.yml           # Docker configuration
├── pom.xml                      # Maven dependencies
└── README.md                    # This file
```

---

## 🎓 Learning Outcomes

### Technical Skills Demonstrated
- Spring Boot ecosystem mastery with advanced features
- Real-time application development with WebSocket/SSE
- Security best practices with JWT and Spring Security
- Performance optimization with caching strategies
- Modern web development with responsive design
- Docker containerization and deployment

### Software Architecture Concepts
- Layered architecture with separation of concerns
- RESTful API design with proper HTTP methods
- Database optimization with efficient queries
- Real-time system design for notifications
- Microservices-ready modular architecture

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Authors

- Your Name - Initial work

---

## 🙏 Acknowledgments

- Spring Boot documentation and community
- PostgreSQL and Redis communities
- Bootstrap and Thymeleaf teams
- All contributors and testers

---

## 📧 Contact

For questions or support, please contact:
- Email: support@edusmart.com
- GitHub Issues: https://github.com/yourusername/edusmart/issues

---

**Made with ❤️ using Spring Boot**

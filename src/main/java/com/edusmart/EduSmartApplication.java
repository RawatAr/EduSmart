package com.edusmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for EduSmart - Smart E-Learning Platform
 * 
 * Features:
 * - Real-time notifications with WebSocket
 * - Redis caching for performance optimization
 * - JWT-based authentication
 * - Role-based access control
 * - Interactive learning management
 * 
 * @author EduSmart Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class EduSmartApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduSmartApplication.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════╗
            ║                                                           ║
            ║     ███████╗██████╗ ██╗   ██╗███████╗███╗   ███╗ █████╗ ║
            ║     ██╔════╝██╔══██╗██║   ██║██╔════╝████╗ ████║██╔══██╗║
            ║     █████╗  ██║  ██║██║   ██║███████╗██╔████╔██║███████║║
            ║     ██╔══╝  ██║  ██║██║   ██║╚════██║██║╚██╔╝██║██╔══██║║
            ║     ███████╗██████╔╝╚██████╔╝███████║██║ ╚═╝ ██║██║  ██║║
            ║     ╚══════╝╚═════╝  ╚═════╝ ╚══════╝╚═╝     ╚═╝╚═╝  ╚═╝║
            ║                                                           ║
            ║          Smart E-Learning Platform v1.0.0                ║
            ║                                                           ║
            ║   Application started successfully!                      ║
            ║   Access: http://localhost:8080                          ║
            ║   Actuator: http://localhost:8080/actuator               ║
            ║                                                           ║
            ╚═══════════════════════════════════════════════════════════╝
            """);
    }
}

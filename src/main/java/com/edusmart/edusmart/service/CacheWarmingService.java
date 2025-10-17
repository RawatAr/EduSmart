package com.edusmart.edusmart.service;

import com.edusmart.edusmart.model.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheWarmingService {

    private final CourseService courseService;

    // Schedule to run every 30 minutes
    @Scheduled(fixedRate = 1800000) // 30 minutes in milliseconds
    public void warmUpCourseCache() {
        log.info("Starting cache warming for courses...");
        List<Course> allCourses = courseService.getAllCourses(); // This will populate the 'courses' cache
        log.info("Warmed up {} courses into cache.", allCourses.size());

        // Example: Warm up individual course caches for popular courses
        // For a real application, you'd have logic to identify "popular" courses
        allCourses.stream().limit(5).forEach(course -> {
            courseService.getCourseById(course.getId()); // This will populate individual 'course' caches
            log.debug("Warmed up course with ID: {}", course.getId());
        });
        log.info("Cache warming for courses completed.");
    }

    // You can add more cache warming methods for other entities as needed
}
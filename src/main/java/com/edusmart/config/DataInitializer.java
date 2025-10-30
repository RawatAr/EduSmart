package com.edusmart.config;

import com.edusmart.entity.Category;
import com.edusmart.entity.Course;
import com.edusmart.entity.CourseLevel;
import com.edusmart.entity.User;
import com.edusmart.entity.enums.Role;
import com.edusmart.repository.CategoryRepository;
import com.edusmart.repository.CourseRepository;
import com.edusmart.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing sample data...");
        
        // Check if users already exist
        if (userRepository.count() > 0) {
            logger.info("Users already exist. Skipping user initialization.");
        } else {
            // Create 5 sample students
            createUser("alice", "alice@edusmart.com", "Alice", "Johnson", Role.STUDENT, "Student@123");
            createUser("bob", "bob@edusmart.com", "Bob", "Smith", Role.STUDENT, "Student@123");
            createUser("charlie", "charlie@edusmart.com", "Charlie", "Brown", Role.STUDENT, "Student@123");
            createUser("diana", "diana@edusmart.com", "Diana", "Williams", Role.STUDENT, "Student@123");
            createUser("eva", "eva@edusmart.com", "Eva", "Martinez", Role.STUDENT, "Student@123");
            
            // Create 5 sample instructors
            createUser("prof_david", "david@edusmart.com", "David", "Miller", Role.INSTRUCTOR, "Instructor@123");
            createUser("prof_emily", "emily@edusmart.com", "Emily", "Davis", Role.INSTRUCTOR, "Instructor@123");
            createUser("prof_frank", "frank@edusmart.com", "Frank", "Wilson", Role.INSTRUCTOR, "Instructor@123");
            createUser("prof_grace", "grace@edusmart.com", "Grace", "Taylor", Role.INSTRUCTOR, "Instructor@123");
            createUser("prof_henry", "henry@edusmart.com", "Henry", "Anderson", Role.INSTRUCTOR, "Instructor@123");
            
            logger.info("Created 5 students and 5 instructors");
        }
        
        // Check if courses already exist
        if (courseRepository.count() > 0) {
            logger.info("Courses already exist. Skipping course initialization.");
            return;
        }
        
        // Get first instructor for creating courses
        User instructor = userRepository.findByRole(Role.INSTRUCTOR).stream().findFirst()
            .orElseThrow(() -> new RuntimeException("No instructor found"));
        
        // Create categories
        Category webDev = createCategory("Web Development", "Learn web development technologies");
        Category dataSci = createCategory("Data Science", "Master data science and analytics");
        Category design = createCategory("Design", "Learn design principles and tools");
        Category business = createCategory("Business", "Business and management courses");
        Category programming = createCategory("Programming", "Learn programming languages");
        Category marketing = createCategory("Marketing", "Digital marketing and SEO");
        
        // Create sample courses
        createCourse(instructor, webDev,
            "Complete Web Development Bootcamp 2024",
            "Master web development with HTML, CSS, JavaScript, React, Node.js, and more!",
            new BigDecimal("3999.00"),
            CourseLevel.BEGINNER,
            "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=400",
            45);
        
        createCourse(instructor, programming,
            "Python for Data Science and Machine Learning",
            "Learn Python, NumPy, Pandas, Matplotlib, Seaborn, Machine Learning, and more!",
            new BigDecimal("4499.00"),
            CourseLevel.INTERMEDIATE,
            "https://images.unsplash.com/photo-1526379095098-d400fd0bf935?w=400",
            60);
        
        createCourse(instructor, design,
            "Complete UI/UX Design Course 2024",
            "Master Figma, Adobe XD, UI/UX principles, wireframing, prototyping, and more!",
            new BigDecimal("3499.00"),
            CourseLevel.BEGINNER,
            "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400",
            35);
        
        createCourse(instructor, dataSci,
            "Data Science Masterclass",
            "Complete data science course covering statistics, ML, deep learning, and AI!",
            new BigDecimal("5999.00"),
            CourseLevel.ADVANCED,
            "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=400",
            80);
        
        createCourse(instructor, business,
            "Digital Marketing Mastery 2024",
            "Learn SEO, social media marketing, content marketing, PPC, and email marketing!",
            new BigDecimal("2999.00"),
            CourseLevel.BEGINNER,
            "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=400",
            30);
        
        createCourse(instructor, programming,
            "React & Next.js - The Complete Guide",
            "Master React, Next.js, TypeScript, and build modern web applications!",
            new BigDecimal("4299.00"),
            CourseLevel.INTERMEDIATE,
            "https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=400",
            50);
        
        createCourse(instructor, webDev,
            "Full Stack JavaScript Development",
            "Learn MongoDB, Express, React, Node.js (MERN Stack) and build full stack apps!",
            new BigDecimal("4999.00"),
            CourseLevel.INTERMEDIATE,
            "https://images.unsplash.com/photo-1587620962725-abab7fe55159?w=400",
            65);
        
        createCourse(instructor, programming,
            "Java Programming Masterclass",
            "Complete Java course from basics to advanced, including Spring Boot!",
            new BigDecimal("3799.00"),
            CourseLevel.BEGINNER,
            "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400",
            70);
        
        createCourse(instructor, design,
            "Graphic Design Complete Course",
            "Master Photoshop, Illustrator, branding, logo design, and visual communication!",
            new BigDecimal("3299.00"),
            CourseLevel.BEGINNER,
            "https://images.unsplash.com/photo-1626785774573-4b799315345d?w=400",
            40);
        
        createCourse(instructor, marketing,
            "Social Media Marketing Complete Course",
            "Master Facebook, Instagram, Twitter, LinkedIn, TikTok marketing strategies!",
            new BigDecimal("2799.00"),
            CourseLevel.BEGINNER,
            "https://images.unsplash.com/photo-1611926653458-09294b3142bf?w=400",
            25);
        
        createCourse(instructor, dataSci,
            "Deep Learning & Neural Networks",
            "Master deep learning, CNNs, RNNs, GANs, transformers using TensorFlow & PyTorch!",
            new BigDecimal("6499.00"),
            CourseLevel.ADVANCED,
            "https://images.unsplash.com/photo-1677442136019-21780ecad995?w=400",
            90);
        
        createCourse(instructor, business,
            "Project Management Professional (PMP)",
            "Complete PMP certification preparation course with practice exams!",
            new BigDecimal("4799.00"),
            CourseLevel.INTERMEDIATE,
            "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400",
            55);
        
        logger.info("Sample data initialization completed successfully!");
    }
    
    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }
    
    private void createCourse(User instructor, Category category, String title, String description,
                             BigDecimal price, CourseLevel level, String thumbnailUrl, int duration) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setShortDescription(description);
        course.setInstructor(instructor);
        course.setCategory(category);
        course.setPrice(price);
        course.setLevel(level);
        course.setIsPublished(true);
        course.setThumbnailUrl(thumbnailUrl);
        course.setDurationHours(duration);
        course.setRating(BigDecimal.valueOf(4.5 + (Math.random() * 0.5))); // Random rating between 4.5 and 5.0
        course.setAverageRating(4.5 + (Math.random() * 0.5));
        course.setEnrollmentCount(100 + (int)(Math.random() * 1000)); // Random enrollments
        courseRepository.save(course);
        logger.info("Created course: {}", title);
    }
    
    private User createUser(String username, String email, String firstName, String lastName, 
                           Role role, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(password));
        user.setIsActive(true);
        user.setEmailVerified(true);
        userRepository.save(user);
        logger.info("Created {} user: {} ({})", role, username, email);
        return user;
    }
}

package com.edusmart.service;

import com.edusmart.dto.course.CategoryDTO;
import com.edusmart.entity.Category;
import com.edusmart.exception.BadRequestException;
import com.edusmart.exception.ResourceNotFoundException;
import com.edusmart.repository.CategoryRepository;
import com.edusmart.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing categories
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    
    /**
     * Create a new category
     */
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        log.info("Creating category: {}", categoryDTO.getName());
        
        // Check if category name already exists
        if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new BadRequestException("Category with name '" + categoryDTO.getName() + "' already exists");
        }
        
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .iconUrl(categoryDTO.getIconUrl())
                .build();
        
        category = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", category.getId());
        
        return mapToDTO(category);
    }
    
    /**
     * Update existing category
     */
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        log.info("Updating category: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        // Check if new name conflicts with existing category
        if (!category.getName().equals(categoryDTO.getName())) {
            categoryRepository.findByName(categoryDTO.getName()).ifPresent(existing -> {
                throw new BadRequestException("Category with name '" + categoryDTO.getName() + "' already exists");
            });
        }
        
        category.setName(categoryDTO.getName());
        if (categoryDTO.getDescription() != null) {
            category.setDescription(categoryDTO.getDescription());
        }
        if (categoryDTO.getIconUrl() != null) {
            category.setIconUrl(categoryDTO.getIconUrl());
        }
        
        category = categoryRepository.save(category);
        log.info("Category updated successfully: {}", categoryId);
        
        return mapToDTO(category);
    }
    
    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return mapToDTO(category);
    }
    
    /**
     * Get all categories
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete category
     */
    public void deleteCategory(Long categoryId) {
        log.info("Deleting category: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        // Check if category has courses
        int courseCount = courseRepository.countByCategory(category);
        if (courseCount > 0) {
            throw new BadRequestException("Cannot delete category with " + courseCount + " courses. Please reassign or delete courses first.");
        }
        
        categoryRepository.delete(category);
        log.info("Category deleted successfully: {}", categoryId);
    }
    
    /**
     * Map Category entity to CategoryDTO
     */
    private CategoryDTO mapToDTO(Category category) {
        int courseCount = courseRepository.countByCategory(category);
        
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .iconUrl(category.getIconUrl())
                .courseCount(courseCount)
                .build();
    }
}

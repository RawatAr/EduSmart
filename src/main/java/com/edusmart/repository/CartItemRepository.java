package com.edusmart.repository;

import com.edusmart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for CartItem entity
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByCartId(Long cartId);
    
    Optional<CartItem> findByCartIdAndCourseId(Long cartId, Long courseId);
    
    boolean existsByCartIdAndCourseId(Long cartId, Long courseId);
    
    void deleteByCartIdAndCourseId(Long cartId, Long courseId);
}

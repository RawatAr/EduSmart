package com.edusmart.repository;

import com.edusmart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Cart entity
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    Optional<Cart> findByStudentId(Long studentId);
    
    boolean existsByStudentId(Long studentId);
}

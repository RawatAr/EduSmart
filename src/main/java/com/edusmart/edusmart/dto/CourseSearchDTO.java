package com.edusmart.edusmart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchDTO {

    private String query; // General search query
    private UUID categoryId; // Filter by category
    private UUID instructorId; // Filter by instructor
    private String sortBy; // title, createdAt, enrollmentCount, averageRating
    private String sortDirection; // ASC, DESC
    private Integer page; // Page number (0-based)
    private Integer size; // Page size

    // Default values
    public Integer getPage() {
        return page != null ? page : 0;
    }

    public Integer getSize() {
        return size != null ? size : 10;
    }

    public String getSortBy() {
        return sortBy != null ? sortBy : "createdAt";
    }

    public String getSortDirection() {
        return sortDirection != null ? sortDirection : "DESC";
    }
}
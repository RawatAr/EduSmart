package com.edusmart.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for email request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequestDTO {
    
    private String to;
    private String subject;
    private String template;
    private Map<String, Object> variables;
    private String attachmentPath;
}

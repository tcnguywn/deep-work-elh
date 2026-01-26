package com.tcnw.ELH_note_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class BlockDTO {
    private UUID id;
    private UUID parentId;
    private String type;            // "page", "to_do", "heading"...
    private Map<String, Object> properties; // JSON data
    private Double sortOrder;
    private boolean hasChildren;    // Để hiển thị mũi tên toggle trên UI
    private LocalDateTime updatedAt;
}
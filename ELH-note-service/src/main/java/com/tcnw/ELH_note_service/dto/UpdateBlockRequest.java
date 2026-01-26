package com.tcnw.ELH_note_service.dto;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class UpdateBlockRequest {
    private Map<String, Object> properties;
    private UUID parentId;
    private Double sortOrder;
}
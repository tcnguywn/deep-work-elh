package com.tcnw.ELH_note_service.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class CreateBlockRequest {
    private UUID parentId;          // Null nếu tạo ở root
    private String type;            // Bắt buộc (VD: "page")
    private Map<String, Object> properties; // Bắt buộc (VD: {"title": "New Page"})
    private UUID prevBlockId;       // (Optional) ID của block đứng trước (để chèn vào giữa)
}
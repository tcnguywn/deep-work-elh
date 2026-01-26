package com.tcnw.ELH_note_service.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "blocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "parent_id")
    private UUID parentId; // Lưu ID cha để query cho nhanh

    @Column(nullable = false)
    private String type; // "page", "folder", "paragraph", "heading_1", "to_do"...

    // --- SỨC MẠNH CỦA JSONB ---
    // Map này sẽ lưu mọi thuộc tính động: title, checked, color, url...
    // Khi lưu vào DB nó là JSON, khi lấy ra Java nó là Map
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> properties;

    @Column(name = "sort_order")
    private Double sortOrder;

    @Column(name = "is_archived")
    private boolean archived = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // (Optional) Nếu muốn dùng JPA Relationship để lazy load con
    // Nhưng với Notion-like, thường ta query bằng Repository theo parentId sẽ hiệu quả hơn
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    // private Block parentBlock;
}
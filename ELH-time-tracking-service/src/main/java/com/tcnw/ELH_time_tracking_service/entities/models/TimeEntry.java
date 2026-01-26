package com.tcnw.ELH_time_tracking_service.entities.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "project_id")
    private Long projectId; // Dùng để vẽ biểu đồ theo Project

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "duration_seconds", nullable = false)
    private Long durationSeconds;

    @Column(name = "is_manual")
    private boolean manual; // True: Nhập tay, False: Sync từ Pomodoro

    @Column(name = "is_deep_work")
    private boolean deepWork; // True: Tính là Deep Work

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
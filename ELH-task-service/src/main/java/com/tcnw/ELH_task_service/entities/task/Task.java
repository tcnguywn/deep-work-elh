package com.tcnw.ELH_task_service.entities.task;

import com.tcnw.ELH_task_service.entities.BaseEntity;
import com.tcnw.ELH_task_service.entities.project.Project;
import com.tcnw.ELH_task_service.entities.tag.Tag;
import com.tcnw.ELH_task_service.entities.task.constant.TaskPriority;
import com.tcnw.ELH_task_service.entities.task.constant.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "estimated_time_minutes")
    private Integer estimatedTimeMinutes;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "task_tags",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;
}
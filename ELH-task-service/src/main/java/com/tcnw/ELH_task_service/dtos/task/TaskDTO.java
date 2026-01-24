package com.tcnw.ELH_task_service.dtos.task;

import com.tcnw.ELH_task_service.dtos.ProjectDTO;
import com.tcnw.ELH_task_service.dtos.TagDTO;
import com.tcnw.ELH_task_service.entities.task.constant.TaskPriority;
import com.tcnw.ELH_task_service.entities.task.constant.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class TaskDTO {
    private Long id;

    @NotBlank(message = "Tiêu đề nhiệm vụ không được để trống")
    private String title;

    private String description;

    private TaskPriority priority; // LOW, MEDIUM, HIGH
    private TaskStatus status;     // TODO, IN_PROGRESS, DONE

    private LocalDateTime dueDate;
    private Integer estimatedTimeMinutes;
    private LocalDateTime completedAt;

    private Long projectId;
    private Set<Long> tagIds;

    // --- Output fields (Dùng khi trả response về Client) ---
    private ProjectDTO project;
    private List<TagDTO> tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
package com.tcnw.ELH_task_service.dtos.task;

import com.tcnw.ELH_task_service.entities.task.constant.TaskPriority;
import com.tcnw.ELH_task_service.entities.task.constant.TaskStatus;
import lombok.Data;

import java.util.List;

@Data
public class TaskFilterRequest {
    private String search;
    private List<TaskStatus> statuses;
    private List<TaskPriority> priorities;
    private Long projectId;             // Filter theo dự án
    private List<Long> tagIds;          // Filter theo tag

    private String fromDate;
    private String toDate;
}
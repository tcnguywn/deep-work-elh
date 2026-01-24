package com.tcnw.ELH_task_service.services.task;


import com.tcnw.ELH_task_service.dtos.task.CalendarEventDTO;
import com.tcnw.ELH_task_service.dtos.task.TaskDTO;
import com.tcnw.ELH_task_service.dtos.task.TaskFilterRequest;
import com.tcnw.ELH_task_service.entities.project.Project;
import com.tcnw.ELH_task_service.entities.tag.Tag;
import com.tcnw.ELH_task_service.entities.task.Task;
import com.tcnw.ELH_task_service.entities.task.constant.TaskStatus;
import com.tcnw.ELH_task_service.repositories.project.ProjectRepository;
import com.tcnw.ELH_task_service.repositories.tag.TagRepository;
import com.tcnw.ELH_task_service.repositories.task.TaskRepository;
import com.tcnw.ELH_task_service.repositories.task.spec.TaskSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    public Page<TaskDTO> getAllTasks(String userId, Pageable pageable) {
        return taskRepository.findAllByUserId(userId, pageable)
                .map(task -> modelMapper.map(task, TaskDTO.class));
    }

    public TaskDTO getTaskById(String userId, Long taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        return modelMapper.map(task, TaskDTO.class);
    }

    @Transactional
    public TaskDTO createTask(String userId, TaskDTO dto) {
        Task task = modelMapper.map(dto, Task.class);
        task.setUserId(userId);

        if (dto.getProjectId() != null) {
            Project project = projectRepository.findByIdAndUserId(dto.getProjectId(), userId)
                    .orElseThrow(() -> new EntityNotFoundException("Project not found"));
            task.setProject(project);
        }

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllByIdInAndUserId(dto.getTagIds(), userId);
            task.setTags(tags);
        } else {
            task.setTags(new ArrayList<>());
        }

        if (task.getStatus() == null) task.setStatus(TaskStatus.TODO);

        Task savedTask = taskRepository.save(task);
        return modelMapper.map(savedTask, TaskDTO.class);
    }

    @Transactional
    public TaskDTO updateTask(String userId, Long taskId, TaskDTO dto) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        // Update các trường cơ bản
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        task.setEstimatedTimeMinutes(dto.getEstimatedTimeMinutes());

        // Update Status & Completed At
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
            if (dto.getStatus() == TaskStatus.DONE && task.getCompletedAt() == null) {
                task.setCompletedAt(LocalDateTime.now());
            } else if (dto.getStatus() != TaskStatus.DONE) {
                task.setCompletedAt(null);
            }
        }

        // Update Project Relation
        if (dto.getProjectId() != null) {
            // Nếu có gửi projectId -> tìm và gán
            if (task.getProject() == null || !task.getProject().getId().equals(dto.getProjectId())) {
                Project project = projectRepository.findByIdAndUserId(dto.getProjectId(), userId)
                        .orElseThrow(() -> new EntityNotFoundException("Project not found"));
                task.setProject(project);
            }
        } else {
            // Nếu gửi null -> Gỡ task khỏi project
            task.setProject(null);
        }

        // Update Tags Relation
        if (dto.getTagIds() != null) {
            List<Tag> tags = tagRepository.findAllByIdInAndUserId(dto.getTagIds(), userId);
            task.setTags(tags);
        }

        Task updatedTask = taskRepository.save(task);
        return modelMapper.map(updatedTask, TaskDTO.class);
    }

    @Transactional
    public void deleteTask(String userId, Long taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        taskRepository.delete(task);
    }


    public List<CalendarEventDTO> getTasksForCalendar(String userId, LocalDateTime start, LocalDateTime end) {
        // 1. Query Database (Hàm này đã viết ở bước trước)
        List<Task> tasks = taskRepository.findAllByUserIdAndDueDateBetween(userId, start, end);

        // 2. Map sang DTO chuẩn hiển thị Calendar
        return tasks.stream().map(task -> {
            // Tính toán thời gian kết thúc
            LocalDateTime endTime = task.getDueDate();
            if (task.getEstimatedTimeMinutes() != null && task.getEstimatedTimeMinutes() > 0) {
                endTime = task.getDueDate().plusMinutes(task.getEstimatedTimeMinutes());
            } else {
                // Nếu không có thời gian ước lượng, mặc định là sự kiện 1 tiếng hoặc hiển thị dạng dot
                endTime = task.getDueDate().plusHours(1);
            }

            // Lấy màu: Ưu tiên màu dự án, nếu không có thì mặc định xanh
            String eventColor = (task.getProject() != null && task.getProject().getColor() != null)
                    ? task.getProject().getColor()
                    : "#3788d8";

            return CalendarEventDTO.builder()
                    .id(task.getId())
                    .title(task.getTitle())
                    .start(task.getDueDate())
                    .end(endTime)
                    .color(eventColor)
                    .allDay(false) // Tạm thời để false, nếu muốn làm tính năng "Task cả ngày" thì check logic ở đây
                    .description(task.getDescription())
                    .status(task.getStatus().name())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateTaskDueDate(String userId, Long taskId, LocalDateTime newDate) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        task.setDueDate(newDate);
        taskRepository.save(task);
    }

    public Page<TaskDTO> getTasks(String userId, TaskFilterRequest filterRequest, Pageable pageable) {

        Specification<Task> spec = TaskSpecification.getFilterSpec(userId, filterRequest);

        Page<Task> tasksPage = taskRepository.findAll(spec, pageable);

        return tasksPage.map(task -> modelMapper.map(task, TaskDTO.class));
    }
}
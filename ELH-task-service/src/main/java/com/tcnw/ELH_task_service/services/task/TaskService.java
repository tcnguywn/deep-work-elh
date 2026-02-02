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
import org.springframework.util.StringUtils; // Import thêm cái này

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

        // Fix nhỏ: Mặc định spentTime là 0 để tránh null pointer khi tính toán sau này
        if (task.getSpentTimeMinutes() == null) task.setSpentTimeMinutes(0);

        Task savedTask = taskRepository.save(task);
        return modelMapper.map(savedTask, TaskDTO.class);
    }

    @Transactional
    public TaskDTO updateTask(String userId, Long taskId, TaskDTO dto) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        if (StringUtils.hasText(dto.getTitle())) {
            task.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }

        if (dto.getPriority() != null) {
            task.setPriority(dto.getPriority());
        }

        if (dto.getDueDate() != null) {
            task.setDueDate(dto.getDueDate());
        }

        if (dto.getEstimatedTimeMinutes() != null) {
            task.setEstimatedTimeMinutes(dto.getEstimatedTimeMinutes());
        }

        // --- LOGIC TRẠNG THÁI & KÉO THẢ ---
        if (dto.getStatus() != null && dto.getStatus() != task.getStatus()) {
            task.setStatus(dto.getStatus());

            // Tự động set ngày hoàn thành khi kéo sang DONE
            if (dto.getStatus() == TaskStatus.DONE) {
                task.setCompletedAt(LocalDateTime.now());
            }
            // Nếu kéo ngược từ DONE về TODO/IN_PROGRESS thì xóa ngày hoàn thành
            else if (task.getCompletedAt() != null) {
                task.setCompletedAt(null);
            }
        }

        // --- RELATIONSHIPS ---

        if (dto.getProjectId() != null) {
            if (dto.getProjectId() == 0) {
                task.setProject(null);
            } else if (task.getProject() == null || !task.getProject().getId().equals(dto.getProjectId())) {
                Project project = projectRepository.findByIdAndUserId(dto.getProjectId(), userId)
                        .orElseThrow(() -> new EntityNotFoundException("Project not found"));
                task.setProject(project);
            }
        }

        // Update Tags: Chỉ update nếu list tagIds được gửi lên (kể cả list rỗng để xóa hết tag)
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
        List<Task> tasks = taskRepository.findAllByUserIdAndDueDateBetween(userId, start, end);
        return tasks.stream().map(task -> {
            LocalDateTime endTime = task.getDueDate();
            if (task.getEstimatedTimeMinutes() != null && task.getEstimatedTimeMinutes() > 0) {
                endTime = task.getDueDate().plusMinutes(task.getEstimatedTimeMinutes());
            } else {
                endTime = task.getDueDate().plusHours(1);
            }
            String eventColor = (task.getProject() != null && task.getProject().getColor() != null)
                    ? task.getProject().getColor()
                    : "#3788d8";

            return CalendarEventDTO.builder()
                    .id(task.getId())
                    .title(task.getTitle())
                    .start(task.getDueDate())
                    .end(endTime)
                    .color(eventColor)
                    .allDay(false)
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

    @Transactional
    public void updateTaskTime(String userId, Long taskId, int minutesToAdd) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        int currentSpent = task.getSpentTimeMinutes() == null ? 0 : task.getSpentTimeMinutes();
        task.setSpentTimeMinutes(currentSpent + minutesToAdd);

        if (task.getStatus() == TaskStatus.TODO) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }
        taskRepository.save(task);
    }
}
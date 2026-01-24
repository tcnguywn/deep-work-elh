package com.tcnw.ELH_task_service.controllers;

import com.tcnw.ELH_task_service.dtos.task.CalendarEventDTO;
import com.tcnw.ELH_task_service.dtos.task.TaskDTO;
import com.tcnw.ELH_task_service.dtos.task.TaskFilterRequest;
import com.tcnw.ELH_task_service.services.task.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/task-service/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
//
//    @GetMapping
//    public ResponseEntity<Page<TaskDTO>> getAllTasks(
//            @AuthenticationPrincipal Jwt jwt,
//            @PageableDefault(size = 20, sort = "priority", direction = Sort.Direction.DESC) Pageable pageable
//    ) {
//        String userId = jwt.getSubject();
//        return ResponseEntity.ok(taskService.getAllTasks(userId, pageable));
//    }

    @GetMapping
    public ResponseEntity<Page<TaskDTO>> getTasks(
            @AuthenticationPrincipal Jwt jwt,

            @ModelAttribute TaskFilterRequest filterRequest,

            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(taskService.getTasks(userId, filterRequest, pageable));
    }

    @GetMapping("/calendar-events")
    public ResponseEntity<List<CalendarEventDTO>> getCalendarEvents(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(taskService.getTasksForCalendar(userId, start, end));
    }


    //Kéo thả lịch
    @PatchMapping("/{id}/move")
    public ResponseEntity<Void> moveTask(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody Map<String, String> payload // Nhận {"newDate": "2026-02-23T10:00:00"}
    ) {
        String userId = jwt.getSubject();
        LocalDateTime newDate = LocalDateTime.parse(payload.get("newDate"));

        taskService.updateTaskDueDate(userId, id, newDate);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(taskService.getTaskById(userId, id));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid TaskDTO taskDTO
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(userId, taskDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody @Valid TaskDTO taskDTO
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(taskService.updateTask(userId, id, taskDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        String userId = jwt.getSubject();
        taskService.deleteTask(userId, id);
        return ResponseEntity.noContent().build();
    }
}
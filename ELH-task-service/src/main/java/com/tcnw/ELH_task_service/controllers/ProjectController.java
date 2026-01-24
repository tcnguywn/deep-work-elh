package com.tcnw.ELH_task_service.controllers;

import com.tcnw.ELH_task_service.dtos.ProjectDTO;
import com.tcnw.ELH_task_service.services.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task-service/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<Page<ProjectDTO>> getProjects(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(projectService.getProjects(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(projectService.getProjectById(userId, id));
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid ProjectDTO projectDTO
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(userId, projectDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody @Valid ProjectDTO projectDTO
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(projectService.updateProject(userId, id, projectDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        String userId = jwt.getSubject();
        projectService.deleteProject(userId, id);
        return ResponseEntity.noContent().build();
    }
}
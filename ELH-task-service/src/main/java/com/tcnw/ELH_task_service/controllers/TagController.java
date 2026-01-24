package com.tcnw.ELH_task_service.controllers;

import com.tcnw.ELH_task_service.dtos.TagDTO;
import com.tcnw.ELH_task_service.services.tag.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task-service/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(tagService.getAllTags(userId));
    }

    @PostMapping
    public ResponseEntity<TagDTO> createTag(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid TagDTO tagDTO
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tagService.createTag(userId, tagDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        String userId = jwt.getSubject();
        tagService.deleteTag(userId, id);
        return ResponseEntity.noContent().build();
    }
}
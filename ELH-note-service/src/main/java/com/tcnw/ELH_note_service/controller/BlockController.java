package com.tcnw.ELH_note_service.controller;

import com.tcnw.ELH_note_service.dto.BlockDTO;
import com.tcnw.ELH_note_service.dto.CreateBlockRequest;
import com.tcnw.ELH_note_service.dto.UpdateBlockRequest;
import com.tcnw.ELH_note_service.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/note-service/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    // 1. Lấy danh sách root (để vẽ Sidebar)
    @GetMapping("/sidebar")
    public ResponseEntity<List<BlockDTO>> getSidebar(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(blockService.getRootBlocks(userId));
    }

    // 2. Lấy chi tiết nội dung trang (hoặc mở rộng folder)
    @GetMapping("/{id}/children")
    public ResponseEntity<List<BlockDTO>> getBlockChildren(@PathVariable UUID id) {
        // API này public hoặc check permission nếu cần
        return ResponseEntity.ok(blockService.getChildren(id));
    }

    // 3. Tạo block mới (Page, Todo, Text...)
    @PostMapping
    public ResponseEntity<BlockDTO> createBlock(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateBlockRequest request) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(blockService.createBlock(userId, request));
    }

    // 4. Update block (Rename, Move, Reorder, Check todo...)
    @PatchMapping("/{id}")
    public ResponseEntity<BlockDTO> updateBlock(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @RequestBody UpdateBlockRequest request) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(blockService.updateBlock(userId, id, request));
    }

    // 5. Xóa block (Vào thùng rác)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlock(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {
        String userId = jwt.getSubject();
        blockService.deleteBlock(userId, id);
        return ResponseEntity.noContent().build();
    }
}
package com.tcnw.ELH_time_tracking_service.controllers;

import com.tcnw.ELH_time_tracking_service.dto.StartRequest;
import com.tcnw.ELH_time_tracking_service.dto.StopSessionResponse;
import com.tcnw.ELH_time_tracking_service.entities.enums.PomodoroMode;
import com.tcnw.ELH_time_tracking_service.entities.enums.TimerStyle;
import com.tcnw.ELH_time_tracking_service.entities.models.PomodoroSession;
import com.tcnw.ELH_time_tracking_service.services.PomodoroService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pomodoro")
@RequiredArgsConstructor
public class PomodoroController {

    private final PomodoroService pomodoroService;

    // 1. Bắt đầu phiên (Start)
    // Body: { "taskId": 101, "mode": "WORK", "style": "STOPWATCH" }
    @PostMapping("/start")
    public ResponseEntity<PomodoroSession> startTimer(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody StartRequest request) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(
                pomodoroService.startSession(userId, request.getTaskId(), request.getMode(), request.getStyle())
        );
    }

    // 2. Dừng phiên (Stop) -> Trả về thời gian nghỉ kiếm được
    @PostMapping("/{id}/stop")
    public ResponseEntity<StopSessionResponse> stopTimer(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(pomodoroService.stopSession(userId, id));
    }

}
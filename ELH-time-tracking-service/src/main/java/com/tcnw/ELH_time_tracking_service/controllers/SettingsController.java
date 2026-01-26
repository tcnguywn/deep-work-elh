package com.tcnw.ELH_time_tracking_service.controllers;

import com.tcnw.ELH_time_tracking_service.entities.models.PomodoroSettings;
import com.tcnw.ELH_time_tracking_service.repositories.PomodoroSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final PomodoroSettingsRepository settingsRepository;

    // 1. Lấy cài đặt hiện tại
    @GetMapping
    public ResponseEntity<PomodoroSettings> getSettings(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        PomodoroSettings settings = settingsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Nếu chưa có thì trả về mặc định
                    return PomodoroSettings.builder()
                            .userId(userId)
                            .workDurationMinutes(25)
                            .shortBreakMinutes(5)
                            .flowmodoroDenominator(5)
                            .build();
                });
        return ResponseEntity.ok(settings);
    }

    // 2. Cập nhật cài đặt
    @PutMapping
    public ResponseEntity<PomodoroSettings> updateSettings(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody PomodoroSettings newSettings) {
        String userId = jwt.getSubject();

        PomodoroSettings current = settingsRepository.findByUserId(userId)
                .orElse(PomodoroSettings.builder().userId(userId).build());

        // Update các trường
        current.setWorkDurationMinutes(newSettings.getWorkDurationMinutes());
        current.setShortBreakMinutes(newSettings.getShortBreakMinutes());
        current.setLongBreakMinutes(newSettings.getLongBreakMinutes());
        current.setFlowmodoroDenominator(newSettings.getFlowmodoroDenominator());
        current.setAutoStartBreak(newSettings.getAutoStartBreak());

        return ResponseEntity.ok(settingsRepository.save(current));
    }
}
package com.tcnw.ELH_time_tracking_service.controllers;

import com.tcnw.ELH_time_tracking_service.dto.DashboardStats;
import com.tcnw.ELH_time_tracking_service.entities.models.TimeEntry;
import com.tcnw.ELH_time_tracking_service.services.TimeEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    // 1. Xem thống kê Dashboard (Tổng giờ, Deep Work, Active Days)
    // VD: GET /api/v1/time-entries/stats?from=2026-01-20&to=2026-01-26
    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        String userId = jwt.getSubject();

        // Convert LocalDate -> LocalDateTime (Đầu ngày và Cuối ngày)
        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        return ResponseEntity.ok(timeEntryService.getDashboardStats(userId, start, end));
    }

    // 2. Lấy lịch sử hoạt động gần đây (Recent Activity)
    @GetMapping("/recent")
    public ResponseEntity<List<TimeEntry>> getRecentEntries(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(timeEntryService.getRecentEntries(userId));
    }

    // 3. Log thời gian thủ công (Manual Entry)
    @PostMapping
    public ResponseEntity<TimeEntry> logManual(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody TimeEntry entryRequest) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(timeEntryService.logManualEntry(userId, entryRequest));
    }
}
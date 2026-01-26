package com.tcnw.ELH_time_tracking_service.services;

import com.tcnw.ELH_time_tracking_service.dto.DashboardStats;
import com.tcnw.ELH_time_tracking_service.entities.models.TimeEntry;
import com.tcnw.ELH_time_tracking_service.repositories.TimeEntryRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeEntryService {
    private final TimeEntryRepository timeEntryRepository;



    // 1. TẠO LOG THỦ CÔNG (MANUAL)
    public TimeEntry logManualEntry(String userId, TimeEntry entryRequest) {
        // Tính toán duration nếu chưa có
        if (entryRequest.getDurationSeconds() == null || entryRequest.getDurationSeconds() == 0) {
            long seconds = java.time.Duration.between(entryRequest.getStartTime(), entryRequest.getEndTime()).getSeconds();
            entryRequest.setDurationSeconds(seconds);
        }

        entryRequest.setUserId(userId);
        entryRequest.setManual(true); // Đánh dấu là nhập tay

        return timeEntryRepository.save(entryRequest);
    }

    // 2. LẤY SỐ LIỆU DASHBOARD
    public DashboardStats getDashboardStats(String userId, LocalDateTime start, LocalDateTime end) {
        Long totalSeconds = timeEntryRepository.getTotalDurationByRange(userId, start, end);
        Long deepWorkSeconds = timeEntryRepository.getDeepWorkDurationByRange(userId, start, end);
        Long activeDays = timeEntryRepository.countActiveDays(userId, start, end);

        return DashboardStats.builder()
                .totalDurationSeconds(totalSeconds != null ? totalSeconds : 0)
                .deepWorkSeconds(deepWorkSeconds != null ? deepWorkSeconds : 0)
                .activeDays(activeDays != null ? activeDays : 0)
                .build();
    }

    // 3. LẤY LỊCH SỬ (RECENT ACTIVITY)
    public List<TimeEntry> getRecentEntries(String userId) {
        // Lấy lịch sử 7 ngày qua
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return timeEntryRepository.findAllByUserIdAndStartTimeBetweenOrderByStartTimeDesc(
                userId, sevenDaysAgo, LocalDateTime.now()
        );
    }
}
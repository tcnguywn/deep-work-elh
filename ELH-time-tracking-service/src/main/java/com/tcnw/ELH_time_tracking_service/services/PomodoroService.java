package com.tcnw.ELH_time_tracking_service.services;

import com.tcnw.ELH_time_tracking_service.dto.StopSessionResponse;
import com.tcnw.ELH_time_tracking_service.entities.enums.PomodoroMode;
import com.tcnw.ELH_time_tracking_service.entities.enums.PomodoroStatus;
import com.tcnw.ELH_time_tracking_service.entities.enums.TimerStyle;
import com.tcnw.ELH_time_tracking_service.entities.models.PomodoroSession;
import com.tcnw.ELH_time_tracking_service.entities.models.PomodoroSettings;
import com.tcnw.ELH_time_tracking_service.entities.models.TimeEntry;
import com.tcnw.ELH_time_tracking_service.repositories.PomodoroSessionRepository;
import com.tcnw.ELH_time_tracking_service.repositories.PomodoroSettingsRepository;
import com.tcnw.ELH_time_tracking_service.repositories.TimeEntryRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PomodoroService {
    private final PomodoroSessionRepository sessionRepository;
    private final PomodoroSettingsRepository settingsRepository;
    private final TimeEntryRepository timeEntryRepository;


    // 1. BẮT ĐẦU PHIÊN (START)
    @Transactional
    public PomodoroSession startSession(String userId, Long taskId, PomodoroMode mode, TimerStyle style) {
        // Dừng các phiên cũ đang chạy (nếu có) để tránh xung đột
        sessionRepository.findByUserIdAndStatus(userId, PomodoroStatus.RUNNING)
                .ifPresent(s -> stopSession(userId, s.getId()));

        PomodoroSession session = PomodoroSession.builder()
                .userId(userId)
                .taskId(taskId)
                .startTime(LocalDateTime.now())
                .status(PomodoroStatus.RUNNING)
                .mode(mode != null ? mode : PomodoroMode.WORK)
                .style(style != null ? style : TimerStyle.COUNTDOWN) // Mặc định là Pomodoro
                .durationSeconds(0L)
                .build();

        return sessionRepository.save(session);
    }

    @Transactional
    public StopSessionResponse stopSession(String userId, Long sessionId) {
        PomodoroSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getStatus().equals(PomodoroStatus.RUNNING) && !session.getStatus().equals(PomodoroStatus.PAUSED)) {
            throw new RuntimeException("Session is already stopped");
        }

        // Chốt thời gian
        LocalDateTime now = LocalDateTime.now();
        session.setEndTime(now);
        long workedSeconds = Duration.between(session.getStartTime(), now).getSeconds();
        session.setDurationSeconds(workedSeconds);

        // Cập nhật trạng thái
        // Flowmodoro luôn là COMPLETED khi dừng. Pomodoro thì tùy (nếu chạy đủ giờ)
        // Ở đây ta tạm set là COMPLETED để đơn giản hóa logic MVP
        session.setStatus(PomodoroStatus.COMPLETED);

        PomodoroSession savedSession = sessionRepository.save(session);

        // Đồng bộ sang TimeEntry (để vẽ biểu đồ) nếu là chế độ làm việc
        if (session.getMode() == PomodoroMode.WORK) {
            syncToTimeEntry(savedSession);
        }

        // TÍNH TOÁN THỜI GIAN NGHỈ (Logic cốt lõi Flowmodoro)
        long breakSeconds = calculateBreakTime(userId, session);

        return StopSessionResponse.builder()
                .session(savedSession)
                .earnedBreakSeconds(breakSeconds)
                .build();
    }

    // Helper: Đồng bộ dữ liệu sang bảng Nhật Ký
    private void syncToTimeEntry(PomodoroSession session) {
        TimeEntry timeEntry = TimeEntry.builder()
                .userId(session.getUserId())
                .taskId(session.getTaskId())
                // .projectId(...) // TODO: Gọi Task Service lấy Project ID sau
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .durationSeconds(session.getDurationSeconds())
                .description(session.getStyle() + " Session") // "COUNTDOWN Session" hoặc "STOPWATCH Session"
                .manual(false)
                .deepWork(true) // Mặc định tính là Deep Work
                .build();
        timeEntryRepository.save(timeEntry);
    }

    // Helper: Tính giờ nghỉ dựa trên cấu hình User
    private long calculateBreakTime(String userId, PomodoroSession session) {
        // Lấy setting (nếu chưa có tạo mặc định)
        PomodoroSettings settings = settingsRepository.findByUserId(userId)
                .orElse(PomodoroSettings.builder().workDurationMinutes(25).shortBreakMinutes(5).flowmodoroDenominator(5).build());

        if (session.getMode() != PomodoroMode.WORK) return 0;

        if (session.getStyle() == TimerStyle.STOPWATCH) {
            // --- FLOWMODORO: Nghỉ = Làm / 5 ---
            return session.getDurationSeconds() / settings.getFlowmodoroDenominator();
        } else {
            // --- POMODORO: Nghỉ cố định 5p hoặc 15p ---
            return settings.getShortBreakMinutes() * 60L;
        }
    }
}
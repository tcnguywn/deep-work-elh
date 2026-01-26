package com.tcnw.ELH_time_tracking_service.entities.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pomodoro_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PomodoroSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    // --- Cấu hình Pomodoro ---
    @Column(name = "work_duration_minutes")
    private Integer workDurationMinutes; // Mặc định 25

    @Column(name = "short_break_minutes")
    private Integer shortBreakMinutes;   // Mặc định 5

    @Column(name = "long_break_minutes")
    private Integer longBreakMinutes;    // Mặc định 15

    @Column(name = "long_break_interval")
    private Integer longBreakInterval;   // Mặc định 4 pomodoro

    // --- Cấu hình Flowmodoro ---
    @Column(name = "flowmodoro_denominator")
    private Integer flowmodoroDenominator; // Mặc định 5 (Time / 5)

    // --- Tự động ---
    @Column(name = "auto_start_break")
    private Boolean autoStartBreak;

    @Column(name = "auto_start_pomodoro")
    private Boolean autoStartPomodoro;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
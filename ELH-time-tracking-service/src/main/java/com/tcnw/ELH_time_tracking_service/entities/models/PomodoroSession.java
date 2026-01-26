package com.tcnw.ELH_time_tracking_service.entities.models;


import com.tcnw.ELH_time_tracking_service.entities.enums.PomodoroMode;
import com.tcnw.ELH_time_tracking_service.entities.enums.PomodoroStatus;
import com.tcnw.ELH_time_tracking_service.entities.enums.TimerStyle;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pomodoro_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PomodoroSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PomodoroStatus status; // RUNNING, COMPLETED...

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PomodoroMode mode;     // WORK, BREAK...

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TimerStyle style;      // COUNTDOWN, STOPWATCH (New)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
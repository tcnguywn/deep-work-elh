package com.tcnw.ELH_time_tracking_service.dto;

import com.tcnw.ELH_time_tracking_service.entities.enums.PomodoroMode;
import com.tcnw.ELH_time_tracking_service.entities.enums.TimerStyle;
import lombok.Data;

@Data
public class StartRequest {
    private Long taskId;
    private PomodoroMode mode; // WORK, SHORT_BREAK, LONG_BREAK
    private TimerStyle style;  // COUNTDOWN (Pomodoro), STOPWATCH (Flowmodoro)
}
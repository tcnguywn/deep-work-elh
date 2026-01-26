package com.tcnw.ELH_time_tracking_service.dto;

import com.tcnw.ELH_time_tracking_service.entities.models.PomodoroSession;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StopSessionResponse {
    private PomodoroSession session;
    private Long earnedBreakSeconds;
}
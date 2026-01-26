package com.tcnw.ELH_time_tracking_service.entities.enums;

public enum PomodoroStatus {
    RUNNING,    // Đang chạy
    PAUSED,     // Tạm dừng (Giữ nguyên thời gian)
    STOPPED,    // Dừng sớm (Người dùng bấm stop)
    COMPLETED   // Hoàn thành đủ thời gian (hết giờ)
}
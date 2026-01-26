package com.tcnw.ELH_time_tracking_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStats {
    private Long totalDurationSeconds; // Tổng thời gian (số to đùng trên dashboard)
    private Long deepWorkSeconds;      // Thời gian Deep Work
    private Long activeDays;           // Số ngày làm việc
    // Có thể thêm List<DailyStat> để vẽ biểu đồ cột
}
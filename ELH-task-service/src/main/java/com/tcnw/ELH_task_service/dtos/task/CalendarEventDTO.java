package com.tcnw.ELH_task_service.dtos.task;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CalendarEventDTO {
    private Long id;
    private String title;       // Tên nhiệm vụ hiển thị trên lịch
    private LocalDateTime start; // Thời gian bắt đầu (dựa vào dueDate)
    private LocalDateTime end;   // Thời gian kết thúc (dựa vào start + estimatedTime)
    private String color;       // Màu sắc (lấy từ Project hoặc Priority)
    private boolean allDay;     // Sự kiện cả ngày hay có giờ cụ thể?

    // Các thông tin bổ sung để khi click vào hiện popup
    private String description;
    private String status;
}
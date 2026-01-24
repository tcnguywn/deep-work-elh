package com.tcnw.ELH_task_service.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "Tên dự án không được để trống")
    private String name;

    private String description;
    private String color;
}
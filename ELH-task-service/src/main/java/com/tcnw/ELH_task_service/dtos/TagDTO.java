package com.tcnw.ELH_task_service.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagDTO {
    private Long id;

    @NotBlank(message = "Tên tag không được để trống")
    private String name;

    private String color;
}
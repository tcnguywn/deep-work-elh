package com.tcnw.ELH_user_service.dto;

import com.tcnw.ELH_user_service.model.constant.Status;
import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    @Nullable
    private String avatarUrl;

    private String timezone;

    private String authId;

    private Status status;
}

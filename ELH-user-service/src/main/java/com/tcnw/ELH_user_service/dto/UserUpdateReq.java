package com.tcnw.ELH_user_service.dto;

import com.tcnw.ELH_user_service.model.constant.Status;
import lombok.Data;

@Data
public class UserUpdateReq {
    private Status status;
}

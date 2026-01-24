package com.tcnw.ELH_user_service.controller;

import com.tcnw.ELH_user_service.dto.UserDTO;
import com.tcnw.ELH_user_service.dto.UserUpdateReq;
import com.tcnw.ELH_user_service.service.KeycloakUserService;
import com.tcnw.ELH_user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final KeycloakUserService keycloakUserService;

    @PostMapping(value = "/register")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO request) {
        log.info("Creating user with {}", request.toString());
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PatchMapping(value = "/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long userId, @RequestBody @Valid UserUpdateReq userUpdateRequest) {
        log.info("Updating user with {}", userUpdateRequest.toString());
        return ResponseEntity.ok(userService.updateUser(userId, userUpdateRequest));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> readUsers(Pageable pageable) {
        log.info("Reading all users from API");
        return ResponseEntity.ok(userService.readUsers(pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> readUser(@PathVariable("id") Long id) {
        log.info("Reading user by id {}", id);
        return ResponseEntity.ok(userService.readUser(id));
    }
}

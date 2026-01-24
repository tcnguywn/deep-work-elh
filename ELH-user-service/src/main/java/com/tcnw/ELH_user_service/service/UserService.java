package com.tcnw.ELH_user_service.service;

import com.tcnw.ELH_user_service.dto.UserDTO;
import com.tcnw.ELH_user_service.dto.UserUpdateReq;
import com.tcnw.ELH_user_service.model.constant.Status;
import com.tcnw.ELH_user_service.model.entity.User;
import com.tcnw.ELH_user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final KeycloakUserService keycloakUserService;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

//    public UserDTO createUser(UserDTO user) {
//        List<UserRepresentation> userRepresentations = keycloakUserService.readUserByEmail(user.getEmail());
//        if(!userRepresentations.isEmpty()) {
//            throw new RuntimeException("This email is already registered. Please check and retry!");
//        }
//        UserRepresentation userRepresentation = modelMapper.map(user, UserRepresentation.class);
//
//        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
//        credentialRepresentation.setValue(user.getPassword());
//        credentialRepresentation.setTemporary(false);
//
//        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
//
//        Integer userCreationResponse = keycloakUserService.createUser(userRepresentation);
//
//        if(userCreationResponse == 201) {
//            log.info("User created under given user name {}", user.getEmail());
//
//            List<UserRepresentation> userRepresentations1 = keycloakUserService.readUserByEmail(user.getEmail());
//            user.setAuthId(userRepresentations1.getFirst().getId());
//            user.setStatus(Status.PENDING);
//            User save = userRepository.save(modelMapper.map(user, User.class));
//            return modelMapper.map(save, UserDTO.class);
//        }
//        throw new RuntimeException("We couldn't find user under given identification. Please check and retry");
//    }
    public UserDTO createUser(UserDTO user) {
        List<UserRepresentation> existingUsers = keycloakUserService.readUserByEmail(user.getEmail());
        if (!existingUsers.isEmpty()) {
            log.warn("Email already exists: {}", user.getEmail());
            throw new RuntimeException("This email is already registered. Please check and retry!");
        }

        UserRepresentation userRepresentation = modelMapper.map(user, UserRepresentation.class);

        userRepresentation.setUsername(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(false);

        // 3. Set password
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(user.getPassword());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);

        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        // Sử dụng cách bên trên bị lỗi nếu phản hồi trễ và không lấy được index của User từ keycloak -> trả về list rỗng -> lỗi
        try {
            String keycloakUserId = keycloakUserService.createUserAndGetId(userRepresentation);

            log.info("User created in Keycloak with ID: {}", keycloakUserId);

            user.setEmail(userRepresentation.getEmail());
            user.setAuthId(keycloakUserId);
            user.setStatus(Status.PENDING);
            user.setPassword(null);

            if(user.getTimezone() == null || user.getTimezone().isEmpty()) {
                user.setTimezone("Asia/Ho_Chi_Minh");
            }

            User savedUser = userRepository.save(modelMapper.map(user, User.class));

            log.info("User saved to database with email: {}", user.getEmail());

            return modelMapper.map(savedUser, UserDTO.class);

        } catch (Exception e) {
            log.error("Failed to create user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    public UserDTO updateProfile(Long userId, UserDTO request) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setTimezone(request.getTimezone());

        return modelMapper.map(userRepository.save(user), UserDTO.class);
    }

    public List<UserDTO> readUsers(Pageable pageable) {
        Page<User> allUsersInDb = userRepository.findAll(pageable);
        List<UserDTO> users = allUsersInDb.getContent().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
        users.forEach(user -> {
            UserRepresentation userRepresentation = keycloakUserService.readUser(user.getAuthId());
            user.setId(user.getId());
            user.setEmail(userRepresentation.getEmail());
        });
        return users;
    }

    public UserDTO readUser(Long userId) {
        return modelMapper.map(userRepository.findById(userId).orElseThrow(EntityNotFoundException::new), UserDTO.class);
    }

    public UserDTO updateUser(Long id, UserUpdateReq userUpdateReq) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if(userUpdateReq.getStatus() == Status.APPROVED) {
            UserRepresentation userRepresentation = keycloakUserService.readUser(user.getAuthId());
            userRepresentation.setEnabled(true);
            userRepresentation.setEmailVerified(true);
            keycloakUserService.updateUser(userRepresentation);
        }

        user.setStatus(userUpdateReq.getStatus());
        return modelMapper.map(userRepository.save(user), UserDTO.class);
    }
}

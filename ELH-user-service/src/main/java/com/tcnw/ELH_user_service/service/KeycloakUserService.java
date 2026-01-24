package com.tcnw.ELH_user_service.service;

import com.tcnw.ELH_user_service.config.keycloak.KeycloakManager;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserService {
    private final KeycloakManager keycloakManager;

    public String createUserAndGetId(UserRepresentation userRepresentation) {
        try {
            Response response = keycloakManager.getKeycloakInstanceWithRealm()
                    .users()
                    .create(userRepresentation);

            int status = response.getStatus();
            if (status == 201) {
                // Lấy userId từ Location header
                String locationHeader = response.getHeaderString("Location");

                if (locationHeader != null && !locationHeader.isEmpty()) {
                    String userId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
                    return userId;
                } else {
                    throw new RuntimeException("Failed to extract user ID from response");
                }
            } else {
                String errorMessage = response.readEntity(String.class);
                throw new RuntimeException("Failed to create user in Keycloak. Status: " + status);
            }

        } catch (Exception e) {
            throw new RuntimeException("Keycloak user creation failed: " + e.getMessage(), e);
        }
    }
    public Integer createUser(UserRepresentation userRepresentation) {
        Response response = keycloakManager.getKeycloakInstanceWithRealm().users().create(userRepresentation);
        return response.getStatus();
    }
    public void updateUser(UserRepresentation userRepresentation) {
        keycloakManager.getKeycloakInstanceWithRealm().users().get(userRepresentation.getId()).update(userRepresentation);
    }


    public List<UserRepresentation> readUserByEmail(String email) {
        return keycloakManager.getKeycloakInstanceWithRealm().users().search(email);
    }


    public UserRepresentation readUser(String authId) {
        try {
            UserResource userResource = keycloakManager.getKeycloakInstanceWithRealm().users().get(authId);
            return userResource.toRepresentation();
        } catch (Exception e) {
            throw new RuntimeException("User not found under given ID");
        }
    }

}

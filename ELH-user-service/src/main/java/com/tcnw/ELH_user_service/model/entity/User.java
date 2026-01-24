package com.tcnw.ELH_user_service.model.entity;

import com.tcnw.ELH_user_service.model.constant.Status;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PACKAGE)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String authId;

    @Column(unique = true, nullable = false)
    String email;

    String firstName;
    
    String lastName;

    @Nullable
    String avatarUrl;

    @Column
    String timezone;

    @Enumerated(EnumType.STRING)
    Status status;
}

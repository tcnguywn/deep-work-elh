package com.tcnw.ELH_task_service.entities.project;

import com.tcnw.ELH_task_service.entities.BaseEntity;
import com.tcnw.ELH_task_service.entities.task.Task;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects")
public class Project extends BaseEntity {
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 7)
    private String color;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;
}

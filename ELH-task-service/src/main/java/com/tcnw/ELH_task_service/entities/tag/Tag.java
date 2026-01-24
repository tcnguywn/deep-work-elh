package com.tcnw.ELH_task_service.entities.tag;

import com.tcnw.ELH_task_service.entities.BaseEntity;
import com.tcnw.ELH_task_service.entities.task.Task;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(length = 7)
    private String color;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Task> tasks;
}
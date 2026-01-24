package com.tcnw.ELH_task_service.repositories.task;

import com.tcnw.ELH_task_service.entities.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    Page<Task> findAllByUserId(String userId, Pageable pageable);

    Optional<Task> findByIdAndUserId(Long id, String userId);

    List<Task> findAllByUserIdAndDueDateBetween(String userId, LocalDateTime start, LocalDateTime end);
}

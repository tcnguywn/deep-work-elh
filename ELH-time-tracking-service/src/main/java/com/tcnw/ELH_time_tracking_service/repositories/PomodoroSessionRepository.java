package com.tcnw.ELH_time_tracking_service.repositories;

import com.tcnw.ELH_time_tracking_service.entities.enums.PomodoroStatus;
import com.tcnw.ELH_time_tracking_service.entities.models.PomodoroSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PomodoroSessionRepository extends JpaRepository<PomodoroSession, Long> {

    Optional<PomodoroSession> findByUserIdAndStatus(String userId, PomodoroStatus status);

    Optional<PomodoroSession> findTopByUserIdOrderByCreatedAtDesc(String userId);
}
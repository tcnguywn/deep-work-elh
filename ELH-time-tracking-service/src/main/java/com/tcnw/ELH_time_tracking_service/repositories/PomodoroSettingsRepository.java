package com.tcnw.ELH_time_tracking_service.repositories;

import com.tcnw.ELH_time_tracking_service.entities.models.PomodoroSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PomodoroSettingsRepository extends JpaRepository<PomodoroSettings, Long> {
    Optional<PomodoroSettings> findByUserId(String userId);
}
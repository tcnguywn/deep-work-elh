package com.tcnw.ELH_task_service.repositories.project;

import aj.org.objectweb.asm.commons.Remapper;
import com.tcnw.ELH_task_service.entities.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findAllByUserId(String userId, Pageable pageable);

    Optional<Project> findByIdAndUserId(Long id, String userId);
}

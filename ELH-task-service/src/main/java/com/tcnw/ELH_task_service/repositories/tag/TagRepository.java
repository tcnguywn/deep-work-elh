package com.tcnw.ELH_task_service.repositories.tag;

import com.tcnw.ELH_task_service.entities.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByUserId(String userId);

    List<Tag> findAllByIdInAndUserId(Iterable<Long> ids, String userId);

    Optional<Tag> findByIdAndUserId(Long id, String userId);
}

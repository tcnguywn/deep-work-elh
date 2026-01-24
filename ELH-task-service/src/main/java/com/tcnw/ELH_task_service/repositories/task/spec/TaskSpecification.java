package com.tcnw.ELH_task_service.repositories.task.spec;

import com.tcnw.ELH_task_service.dtos.task.TaskFilterRequest;
import com.tcnw.ELH_task_service.entities.tag.Tag;
import com.tcnw.ELH_task_service.entities.task.Task;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    public static Specification<Task> getFilterSpec(String userId, TaskFilterRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. BẮT BUỘC: Chỉ lấy task của User hiện tại
            predicates.add(cb.equal(root.get("userId"), userId));

            if (req == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            // 2. Tìm kiếm (Search) theo Title hoặc Description
            if (StringUtils.hasText(req.getSearch())) {
                String searchLike = "%" + req.getSearch().toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), searchLike);
                Predicate descLike = cb.like(cb.lower(root.get("description")), searchLike);
                predicates.add(cb.or(titleLike, descLike));
            }

            // 3. Filter theo Project
            if (req.getProjectId() != null) {
                predicates.add(cb.equal(root.get("project").get("id"), req.getProjectId()));
            }

            // 4. Filter theo Status (List)
            if (req.getStatuses() != null && !req.getStatuses().isEmpty()) {
                predicates.add(root.get("status").in(req.getStatuses()));
            }

            // 5. Filter theo Priority (List)
            if (req.getPriorities() != null && !req.getPriorities().isEmpty()) {
                predicates.add(root.get("priority").in(req.getPriorities()));
            }

            // 6. Filter theo Tags (Khó hơn chút vì phải Join bảng)
            if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
                Join<Task, Tag> tagsJoin = root.join("tags");
                predicates.add(tagsJoin.get("id").in(req.getTagIds()));
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
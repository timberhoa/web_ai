package com.example.web_ai.repository.specification;

import com.example.web_ai.dto.request.ActivityLogQueryRequest;
import com.example.web_ai.entity.ActivityLog;
import com.example.web_ai.enums.Role;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public final class ActivityLogSpecifications {

    private ActivityLogSpecifications() {
    }

    public static Specification<ActivityLog> build(ActivityLogQueryRequest filters) {
        if (filters == null) {
            return Specification.where(null);
        }

        return Specification.where(withUsername(filters.getUsername()))
                .and(withRole(filters.getRole()))
                .and(withMethod(filters.getMethod()))
                .and(withStatus(filters.getStatus()))
                .and(withPath(filters.getPath()))
                .and(withAction(filters.getAction()))
                .and(withKeyword(filters.getKeyword()))
                .and(withFrom(filters.getFrom()))
                .and(withTo(filters.getTo()));
    }

    private static Specification<ActivityLog> withUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        String normalized = username.trim().toLowerCase();
        return (root, query, cb) -> cb.like(cb.lower(root.get("username")), "%" + normalized + "%");
    }

    private static Specification<ActivityLog> withRole(Role role) {
        if (role == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    private static Specification<ActivityLog> withMethod(String method) {
        if (!StringUtils.hasText(method)) {
            return null;
        }
        return (root, query, cb) -> cb.equal(cb.lower(root.get("method")), method.trim().toLowerCase());
    }

    private static Specification<ActivityLog> withStatus(Integer status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private static Specification<ActivityLog> withPath(String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }
        return (root, query, cb) -> cb.like(cb.lower(root.get("path")), "%" + path.trim().toLowerCase() + "%");
    }

    private static Specification<ActivityLog> withAction(String action) {
        if (!StringUtils.hasText(action)) {
            return null;
        }
        return (root, query, cb) -> cb.like(cb.lower(root.get("action")), "%" + action.trim().toLowerCase() + "%");
    }

    private static Specification<ActivityLog> withKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        String normalized = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("message")), normalized),
                cb.like(cb.lower(root.get("action")), normalized),
                cb.like(cb.lower(root.get("path")), normalized)
        );
    }

    private static Specification<ActivityLog> withFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("occurredAt"), from);
    }

    private static Specification<ActivityLog> withTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("occurredAt"), to);
    }
}

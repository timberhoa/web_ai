package com.example.web_ai.service;

import com.example.web_ai.dto.request.ActivityLogQueryRequest;
import com.example.web_ai.dto.response.ActivityLogResponse;
import com.example.web_ai.entity.ActivityLog;
import com.example.web_ai.exception.BadRequestException;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.mapper.ActivityLogMapper;
import com.example.web_ai.repository.ActivityLogRepository;
import com.example.web_ai.repository.specification.ActivityLogSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;

    @Transactional
    public void saveLog(ActivityLog entry) {
        if (entry == null) {
            return;
        }
        try {
            activityLogRepository.save(entry);
        } catch (Exception ex) {
            log.warn("Failed to persist activity log for {} {}: {}", entry.getMethod(), entry.getPath(), ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> searchLogs(ActivityLogQueryRequest filters, Pageable pageable) {
        if (filters != null && filters.getFrom() != null && filters.getTo() != null
                && filters.getFrom().isAfter(filters.getTo())) {
            throw new BadRequestException("INVALID_TIME_RANGE");
        }
        Specification<ActivityLog> spec = ActivityLogSpecifications.build(filters);
        return activityLogRepository.findAll(spec, pageable)
                .map(activityLogMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ActivityLogResponse getLog(UUID id) {
        ActivityLog logEntity = activityLogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ACTIVITY_LOG_NOT_FOUND"));
        return activityLogMapper.toResponse(logEntity);
    }
}

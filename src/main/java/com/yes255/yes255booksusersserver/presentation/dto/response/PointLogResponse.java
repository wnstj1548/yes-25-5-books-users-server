package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.PointLog;
import lombok.Builder;

import java.util.List;

@Builder
public record PointLogResponse(String pointCurrent, List<PointLog> pointLogs) {
}

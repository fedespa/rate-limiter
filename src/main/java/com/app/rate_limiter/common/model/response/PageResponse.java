package com.app.rate_limiter.common.model.response;

import java.util.List;

public record PageResponse(
        List<?> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}

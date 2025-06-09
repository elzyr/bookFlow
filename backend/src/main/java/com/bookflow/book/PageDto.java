package com.bookflow.book;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {}

package com.foodsquad.FoodSquad.model.dto;

import java.util.List;

public class PaginatedResponseDTO<T> {
    private List<T> items;
    private long totalCount;

    public PaginatedResponseDTO(List<T> items, long totalCount) {
        this.items = items;
        this.totalCount = totalCount;
    }

    // Getters and setters
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}

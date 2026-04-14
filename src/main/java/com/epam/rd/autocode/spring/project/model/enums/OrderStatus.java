package com.epam.rd.autocode.spring.project.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
    PENDING("enum.order.pending"),
    CONFIRMED("enum.order.confirmed"),
    REFUNDED("enum.order.refunded");

    private final String messageKey;
}

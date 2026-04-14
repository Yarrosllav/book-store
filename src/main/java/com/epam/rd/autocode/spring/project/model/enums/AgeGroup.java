package com.epam.rd.autocode.spring.project.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AgeGroup {
    CHILD("enum.agegroup.child"),
    TEEN("enum.agegroup.teen"),
    ADULT("enum.agegroup.adult");

    private final String messageKey;
}

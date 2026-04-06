package com.epam.rd.autocode.spring.project.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Genre {
    FICTION("Художня"),
    NON_FICTION("Нон-фікшн"),
    SCIENCE("Наука"),
    HISTORY("Історія"),
    BIOGRAPHY("Біографія"),
    CHILDREN("Дитяча"),
    FANTASY("Фентезі"),
    DETECTIVE("Детектив"),
    ROMANCE("Романтика"),
    HORROR("Жахи"),
    CLASSIC("Класика"),
    POETRY("Поезія"),
    ADVENTURE("Пригоди"),
    PSYCHOLOGY("Психологія"),
    PHILOSOPHY("Філософія");

    private final String displayName;
}

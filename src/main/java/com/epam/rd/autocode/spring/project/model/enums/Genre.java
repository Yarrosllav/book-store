package com.epam.rd.autocode.spring.project.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Genre {
    FICTION("enum.genre.fiction"),
    NON_FICTION("enum.genre.non_fiction"),
    SCIENCE("enum.genre.science"),
    HISTORY("enum.genre.history"),
    BIOGRAPHY("enum.genre.biography"),
    CHILDREN("enum.genre.children"),
    FANTASY("enum.genre.fantasy"),
    DETECTIVE("enum.genre.detective"),
    ROMANCE("enum.genre.romance"),
    HORROR("enum.genre.horror"),
    CLASSIC("enum.genre.classic"),
    POETRY("enum.genre.poetry"),
    ADVENTURE("enum.genre.adventure"),
    PSYCHOLOGY("enum.genre.psychology"),
    PHILOSOPHY("enum.genre.philosophy"),;

    private final String messageKey;
}

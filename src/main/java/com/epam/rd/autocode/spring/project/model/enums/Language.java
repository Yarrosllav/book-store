package com.epam.rd.autocode.spring.project.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Language {
    ENGLISH( "enum.language.english"),
    SPANISH("enum.language.spanish"),
    FRENCH("enum.language.french"),
    GERMAN("enum.language.german"),
    JAPANESE("enum.language.japanese"),
    UKRAINIAN("enum.language.ukrainian"),;

    private final String messageKey;
}

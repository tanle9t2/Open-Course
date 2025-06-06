package com.tp.opencourse.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LocaleUtils {
    VIETNAM("vn"),
    US("us"),
    ;

    private final String code;
}

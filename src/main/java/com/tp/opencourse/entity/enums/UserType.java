package com.tp.opencourse.entity.enums;

public enum UserType {
    DEFAULT, GOOGLE;

    @Override
    public String toString() {
        return this.name();
    }
}


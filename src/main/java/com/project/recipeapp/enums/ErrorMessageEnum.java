package com.project.recipeapp.enums;

import lombok.Getter;

@Getter
public enum ErrorMessageEnum {
    DESCRIPTION_LONG_ERROR_MESSAGE("Description is too long"),
    RECIPE_NOT_FOUND_MESSAGE("not found");

    private final String message;

    ErrorMessageEnum(String message) {
        this.message = message;
    }
}

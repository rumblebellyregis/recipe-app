package com.project.recipeapp.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
@NoArgsConstructor
@Getter
@Setter
public class RecipeAppException extends Exception {

    private String message;
    private HttpStatus httpStatus;

    public RecipeAppException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

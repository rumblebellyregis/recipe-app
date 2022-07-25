package com.project.recipeapp.controller;

import com.project.recipeapp.exception.RecipeAppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecipeAppException.class)
    public ResponseEntity<String> handleException(RecipeAppException recipeAppException) {
        return new ResponseEntity<>(recipeAppException.getMessage(), recipeAppException.getHttpStatus());
    }
}


package com.project.recipeapp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterForm {

    private String recipeName;
    private String instructionSearch;
    private Boolean vegetarian;
    private Integer numberOfServings;
    private List<String> ingredientsContains;
    private List<String> ingredientsNotContains;

}

package com.project.recipeapp.controller;


import com.project.recipeapp.entity.Recipe;
import com.project.recipeapp.exception.RecipeAppException;
import com.project.recipeapp.model.FilterForm;
import com.project.recipeapp.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController()
@RequestMapping("api/v1/recipe")
public class RecipeController {

    private RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Transactional
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody Recipe recipe) {
        return new ResponseEntity<>(recipeService.saveRecipe(recipe), HttpStatus.OK);
    }

    @DeleteMapping(produces = "application/json")
    @Transactional
    public ResponseEntity<String> deleteRecipe(@RequestParam long recipeId) throws RecipeAppException {
        recipeService.deleteRecipe(recipeId);
        return new ResponseEntity<>("recipe deleted succesfully", HttpStatus.OK);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    @Transactional
    public ResponseEntity<Recipe> updateRecipe(@RequestBody Recipe recipe) throws RecipeAppException {
        return new ResponseEntity<>(recipeService.updateRecipe(recipe), HttpStatus.OK);
    }

    @GetMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Recipe>> getRecipes(@RequestBody FilterForm filterForm) throws RecipeAppException {
        return new ResponseEntity<>(recipeService.getRecipes(filterForm), HttpStatus.OK);
    }
}

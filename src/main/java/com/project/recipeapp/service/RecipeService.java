package com.project.recipeapp.service;


import com.project.recipeapp.entity.Ingredient;
import com.project.recipeapp.entity.Recipe;
import com.project.recipeapp.enums.ErrorMessageEnum;
import com.project.recipeapp.exception.RecipeAppException;
import com.project.recipeapp.model.FilterForm;
import com.project.recipeapp.repository.IngredientRepository;
import com.project.recipeapp.repository.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecipeService {

    private RecipeRepository repository;
    private IngredientRepository ingredientRepository;

    @Autowired
    public RecipeService(RecipeRepository repository, IngredientRepository ingredientRepository) {
        this.repository = repository;
        this.ingredientRepository = ingredientRepository;
    }

    public Recipe saveRecipe(Recipe recipe) {
        return repository.save(recipe);
    }

    public void deleteRecipe(Long id) throws RecipeAppException {
        Recipe recipe = getRecipe(id);
        log.info("Deleting recipe with id: {}", id);
        repository.deleteById(recipe.getId());
    }

    public List<Recipe> getRecipes(FilterForm  filterForm) throws RecipeAppException {
        checkContainsAndNotContainsListHaveSAmeValues(filterForm);
        return repository.getRecipess(filterForm.getVegetarian(), filterForm.getNumberOfServings(),
                filterForm.getRecipeName(), filterForm.getInstructionSearch(),
                filterForm.getIngredientsContains(), filterForm.getIngredientsNotContains());

    }

    public Recipe updateRecipe(Recipe updatedRecipe) throws RecipeAppException {
        Recipe recipe = getRecipe(updatedRecipe.getId());
        updateIngredients(recipe, updatedRecipe);
        recipe.setInstructions(updatedRecipe.getInstructions());
        recipe.setName(updatedRecipe.getName());
        recipe.setIsVegetarian(updatedRecipe.getIsVegetarian());
        return repository.save(recipe);
    }

    private void updateIngredients(Recipe recipe, Recipe updatedRecipe) {
        List<Ingredient> currentIngredients = ingredientRepository.findByRecipe(recipe);
        List<Ingredient> updatedIngredients = updatedRecipe.getIngredients();
        List<Ingredient> toAdd = getDifferences(currentIngredients, updatedIngredients);
        List<Ingredient> toRemove = getDifferences(updatedIngredients, currentIngredients);
        toAdd.forEach(recipe::addIngredient);
        toRemove.forEach(recipe::removeIngredient);
    }

    private List<Ingredient> getDifferences(List<Ingredient> currentIngredients, List<Ingredient> updatedIngredients) {
        return updatedIngredients.stream()
                .filter(element -> !currentIngredients.contains(element))
                .collect(Collectors.toList());
    }

    private Recipe getRecipe(Long recipeId) throws RecipeAppException {
       return repository.findById(recipeId).orElseThrow(() -> {
            log.info("Recipe with id {} not found", recipeId);
            return new RecipeAppException(ErrorMessageEnum.RECIPE_NOT_FOUND_MESSAGE.getMessage(), HttpStatus.NOT_FOUND);
        });
    }

    private void checkContainsAndNotContainsListHaveSAmeValues(FilterForm filterForm) throws RecipeAppException {
        if(!CollectionUtils.isEmpty(filterForm.getIngredientsContains()) && !CollectionUtils.isEmpty(filterForm.getIngredientsNotContains())) {
            List<String> common = filterForm.getIngredientsContains().stream().filter(filterForm.getIngredientsNotContains()::contains).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(common)) {
                throw  new RecipeAppException(ErrorMessageEnum.INGREDIENT_NAME_SHOULD_NOT_BE_IN_BOTH_LISTS.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }
}

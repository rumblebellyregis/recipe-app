package com.project.recipeapp.unittests;

import com.project.recipeapp.entity.Ingredient;
import com.project.recipeapp.entity.Recipe;
import com.project.recipeapp.enums.ErrorMessageEnum;
import com.project.recipeapp.exception.RecipeAppException;
import com.project.recipeapp.model.FilterForm;
import com.project.recipeapp.repository.IngredientRepository;
import com.project.recipeapp.repository.RecipeRepository;
import com.project.recipeapp.service.RecipeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class RecipeServiceShould {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;


    @InjectMocks
    private RecipeService recipeService;


    @Test
    public void save_recipe() {
        Recipe recipe = getRecipe();
        Recipe response = recipeService.saveRecipe(recipe);
        verify(recipeRepository, atLeast(1)).save(any());
    }

    @Test
    public void should_throw_recipe_app_exception_if_recipe_to_delete_not_found() {
        when(recipeRepository.findById(any())).thenReturn(Optional.empty());
        Throwable exception = assertThrows(RecipeAppException.class, () -> recipeService.deleteRecipe(1L));
        verify(recipeRepository, atLeast(1)).findById(any());
        assertEquals(exception.getMessage(), ErrorMessageEnum.RECIPE_NOT_FOUND_MESSAGE.getMessage());
    }

    @Test
    public void should_delete_recipe_if_recipe_found() throws RecipeAppException {
        when(recipeRepository.findById(any())).thenReturn(Optional.of(getRecipe()));
        recipeService.deleteRecipe(1L);
        verify(recipeRepository, atLeast(1)).findById(any());
        verify(recipeRepository, atLeast(1)).deleteById(any());

    }

    @Test
    public void should_throw_recipe_app_exception_if_filter_form_contains_and_not_contains_same_ingredient() {
        Throwable exception = assertThrows(RecipeAppException.class, () -> recipeService.getRecipes(getInvalidFilterForm()));
        assertEquals(exception.getMessage(), ErrorMessageEnum.INGREDIENT_NAME_SHOULD_NOT_BE_IN_BOTH_LISTS.getMessage());
    }

    @Test
    public void should_return_recipe_list_if_filter_form_valid_and_matching_recipe() throws RecipeAppException {
        when(recipeRepository.getRecipess(any(), any(), any(), any(), any(), any())).thenReturn(getRecipeList());
        List<Recipe> recipes = recipeService.getRecipes(getValidFilterForm());
        assertNotNull(recipes);
        assertEquals(1, recipes.size());
        verify(recipeRepository, times(1)).getRecipess(any(), any(), any(), any(), any(), any());
    }

    @Test
    public void should_throw_recipe_app_exception_if_recipe_to_update_not_found() {
        when(recipeRepository.findById(any())).thenReturn(Optional.empty());
        Throwable exception = assertThrows(RecipeAppException.class, () -> recipeService.updateRecipe(getRecipe()));
        verify(recipeRepository, atLeast(1)).findById(any());
        assertEquals(exception.getMessage(), ErrorMessageEnum.RECIPE_NOT_FOUND_MESSAGE.getMessage());
    }

    @Test
    public void should_update_recipe_if_recipe_to_update_found() throws RecipeAppException {
        when(recipeRepository.findById(any())).thenReturn(Optional.of(getRecipe()));
        when(ingredientRepository.findByRecipe(any())).thenReturn(getIngredients());
        recipeService.updateRecipe(getUpdatedRecipe());
        verify(recipeRepository, atLeast(1)).findById(any());
        verify(recipeRepository,times(1)).save(any());
    }

    private List<Recipe> getRecipeList() {
        return  Arrays.asList(getRecipe());
    }

    private Recipe getRecipe() {
        Recipe recipe = new Recipe();
        recipe.setIsVegetarian(false);
        recipe.setName("soup");
        recipe.setInstructions("cook me");
        recipe.setNumberOfServings(3);
        recipe.setIngredients(getIngredients());
        return  recipe;
    }

    private Recipe getUpdatedRecipe() {
        Recipe recipe = new Recipe();
        recipe.setId(1);
        recipe.setIsVegetarian(false);
        recipe.setName("soup");
        recipe.setInstructions("cook the soup");
        recipe.setNumberOfServings(3);
        recipe.setIngredients(getUpdatedIngredients());
        return  recipe;
    }
    private List<Ingredient> getIngredients() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("tomato");
        ingredient.setAmount(BigDecimal.valueOf(3));
        ingredient.setUnit("pieces");
        return  new LinkedList<>(Arrays.asList(ingredient));
    }

    private List<Ingredient> getUpdatedIngredients() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("tomato");
        ingredient.setAmount(BigDecimal.valueOf(5));
        ingredient.setUnit("pieces");
        Ingredient ingredientTwo = new Ingredient();
        ingredientTwo.setName("potato");
        ingredientTwo.setAmount(BigDecimal.valueOf(5));
        ingredientTwo.setUnit("pieces");
        return new LinkedList<>(Arrays.asList(ingredient, ingredientTwo));
    }

    private FilterForm getInvalidFilterForm() {
        FilterForm filterForm = new FilterForm();
        filterForm.setIngredientsContains(Arrays.asList("fish"));
        filterForm.setIngredientsNotContains(Arrays.asList("fish"));
        return filterForm;
    }

    private FilterForm getValidFilterForm() {
        return new FilterForm();
    }
}

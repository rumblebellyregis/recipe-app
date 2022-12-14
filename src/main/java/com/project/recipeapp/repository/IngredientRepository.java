package com.project.recipeapp.repository;

import com.project.recipeapp.entity.Ingredient;
import com.project.recipeapp.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findByRecipe(Recipe recipe);

}

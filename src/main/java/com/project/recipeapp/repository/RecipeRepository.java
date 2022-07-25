package com.project.recipeapp.repository;

import com.project.recipeapp.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT DISTINCT r FROM Recipe r LEFT JOIN r.ingredients i   on ( i.recipe = r.id ) " +
            "WHERE (:isVegetarian is null or r.isVegetarian = CAST(CAST(:isVegetarian AS string)AS boolean)) " +
            "and (:numberOfServings is null or r.numberOfServings = CAST(CAST(:numberOfServings AS string)AS integer)) " +
            "and (:name is null or r.name = :name) and (:instructions is null or r.instructions like %:instructions%) " +
            "and (COALESCE(:contains) is null or i.name in :contains)" +
            " and (COALESCE(:notContains) is null or " +
            "i.recipe not in (SELECT r.id from Recipe r LEFT JOIN r.ingredients i   on ( i.recipe = r.id ) where i.name in :notContains ))")
    List<Recipe> getRecipess(@Param("isVegetarian") Boolean isVegetarian, @Param("numberOfServings") Integer numberOfServings,
                             @Param("name") String name, @Param("instructions") String instructions,
                             @Param("contains") List<String> contains, @Param("notContains") List<String> notContains);

}
//and (COALESCE(:contains) is null or i.name  :contains) //@Param("contains") List<String> contains,
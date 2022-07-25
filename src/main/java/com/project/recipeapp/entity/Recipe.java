package com.project.recipeapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(max = 64)
    @NotBlank
    private String name;

    @Size(max = 1000)
    @NotBlank
    private String instructions;

    @Min(1)
    private int numberOfServings;

    @NotNull
    private boolean isVegetarian;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipe_id")
    @JsonManagedReference
    @NotEmpty(message = "Should not be empty")
    private Set<Ingredient> ingredients;

    public void addIngredient(Ingredient ingredient)
    {
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(Ingredient ingredient)
    {
        this.ingredients.remove(ingredient);
    }
}

package com.project.recipeapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
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
    @NotBlank(message = "Field is required")
    private String name;

    @Size(max = 1000)
    @NotBlank(message = "Field is required")
    private String instructions;

    @Min(1)
    @NotNull(message = "Field is required")
    private int numberOfServings;

    @NotNull(message = "Field is required")
    private Boolean isVegetarian;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipe_id")
    @JsonManagedReference
    @NotEmpty(message = "Should not be empty")

    private List<@Valid Ingredient> ingredients;

    public void addIngredient(Ingredient ingredient)
    {
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(Ingredient ingredient)
    {
        this.ingredients.remove(ingredient);
    }
}

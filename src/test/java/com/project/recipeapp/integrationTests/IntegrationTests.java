package com.project.recipeapp.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.recipeapp.entity.Recipe;
import com.project.recipeapp.enums.ErrorMessageEnum;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class IntegrationTests {

    private static final String RECIPE_CONTROLLER_ENDPOINT = "/api/v1/recipe";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @After
    public void clearDB() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "ingredient", "recipe");
    }

    @Test
    public void return_ok_when_recipe_created() throws Exception {
        String text = getRequestJSon("validRecipes/validRecipeChocolateTart.json");
        final MvcResult result = this.mockMvc.perform(post(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)).andReturn();
        assertThat(result.getResponse().getStatus(), is(200));
    }

    @Test
    public void return_bad_request_when_recipe_missing_fields() throws Exception {
        String text = getRequestJSon("invalidRecipes/invalidRecipeCafeLatte.json");
        final MvcResult result = this.mockMvc.perform(post(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)).andReturn();
        assertThat(result.getResponse().getStatus(), is(400));
    }

    @Test
    public void return_not_found_when_recipe_to_delete_not_exists() throws Exception {
        final MvcResult result = this.mockMvc.perform(delete(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .param("recipeId", String.valueOf(1000L))).andReturn();
        assertThat(result.getResponse().getStatus(), is(404));
        assertEquals(result.getResponse().getContentAsString(), ErrorMessageEnum.RECIPE_NOT_FOUND_MESSAGE.getMessage());
    }

    @Test
    public void return_ok_when_delete_successful() throws Exception {
        String response = saveRecipe("validRecipes/validRecipeChocolateTart.json");
        Recipe recipe = new ObjectMapper().readValue(response, Recipe.class);
        final MvcResult result = this.mockMvc.perform(delete(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .param("recipeId", String.valueOf(recipe.getId()))).andReturn();
        assertThat(result.getResponse().getStatus(), is(200));
    }

    @Test
    public void return_ok_when_recipe_update_succeeds() throws Exception {
        String response = saveRecipe("validRecipes/validRecipeFries.json");
        Recipe recipe = new ObjectMapper().readValue(response, Recipe.class);
        String text = getRequestJSon("validRecipes/validRecipeUpdateFries.json");
        String newString = addIdToRequestJson(recipe, text);
        final MvcResult result = this.mockMvc.perform(put(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newString)).andReturn();
        assertThat(result.getResponse().getStatus(), is(200));
        Recipe updatedRecipe = new ObjectMapper().readValue(result.getResponse().getContentAsString(), Recipe.class);
        assertEquals("Cut the potato into sticks. Deep fry in vegetable oil.", updatedRecipe.getInstructions());
        assertEquals(2, updatedRecipe.getIngredients().size());
    }



    @Test
    public void return_not_found_when_recipe_to_update_not_exists() throws Exception {
        String text = getRequestJSon("validRecipes/validRecipeSalamiTosti.json");
        String newString = text.charAt(0)
                + " \"id\":10000,"
                + text.substring(1);
        final MvcResult result = this.mockMvc.perform(put(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)
                .param("recipeId", String.valueOf(1000L))).andReturn();
        assertThat(result.getResponse().getStatus(), is(404));
        assertEquals(result.getResponse().getContentAsString(), ErrorMessageEnum.RECIPE_NOT_FOUND_MESSAGE.getMessage());
    }

    @Test
    public void return_bad_request_when_recipe_to_update_have_invalid_fields() throws Exception {
        saveRecipe("validRecipes/validRecipeChocolateTart.json");
        String text = getRequestJSon("invalidRecipes/invalidRecipeCafeLatte.json");
        final MvcResult result = this.mockMvc.perform(put(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)).andReturn();
        assertThat(result.getResponse().getStatus(), is(400));
    }

    @Test
    public void return_all_recipes_when_filter_params_null() throws Exception {
        saveRecipe("validRecipes/validRecipeChocolateTart.json");
        saveRecipe("validRecipes/validRecipeNachos.json");
        saveRecipe("validRecipes/validRecipeSalamiTosti.json");
        saveRecipe("validRecipes/validRecipeTruffles.json");
        String text = getRequestJSon("filterRequests/filterRequest1.json");
        final MvcResult result = this.mockMvc.perform(get(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)).andReturn();
        assertThat(result.getResponse().getStatus(), is(200));
        List<Recipe> recipes = extractRecipesFromResponse(result);
        assertEquals(4, recipes.size());
    }

    @Test
    public void return_correct_recipe_when_serving_selected() throws Exception {
        saveRecipe("validRecipes/validRecipeChocolateTart.json");
        saveRecipe("validRecipes/validRecipeNachos.json");
        saveRecipe("validRecipes/validRecipeSalamiTosti.json");
        saveRecipe("validRecipes/validRecipeTruffles.json");
        String text = getRequestJSon("filterRequests/filterRequest2.json");
        final MvcResult result = this.mockMvc.perform(get(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)).andReturn();
        assertThat(result.getResponse().getStatus(), is(200));
        List<Recipe> recipes = extractRecipesFromResponse(result);
        assertEquals(1, recipes.size());
        assertEquals("salami tosti", recipes.get(0).getName());
    }

    @Test
    public void return_correct_recipe_when_contains_selected() throws Exception {
        saveRecipe("validRecipes/validRecipeChocolateTart.json");
        saveRecipe("validRecipes/validRecipeNachos.json");
        saveRecipe("validRecipes/validRecipeSalamiTosti.json");
        saveRecipe("validRecipes/validRecipeTruffles.json");
        String text = getRequestJSon("filterRequests/filterRequest3.json");
        final MvcResult result = this.mockMvc.perform(get(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)).andReturn();
        assertThat(result.getResponse().getStatus(), is(200));
        List<Recipe> recipes = extractRecipesFromResponse(result);
        assertEquals(2, recipes.size());
        Assertions.assertThat(recipes)
                .extracting(Recipe::getName)
                .anyMatch(value -> value.matches("chocolate tart")).anyMatch(val -> val.matches("truffles"));
    }

    @Test
    public void return_correct_recipe_when_multiple_options_selected() throws Exception {
        saveRecipe("validRecipes/validRecipeChocolateTart.json");
        saveRecipe("validRecipes/validRecipeNachos.json");
        saveRecipe("validRecipes/validRecipeSalamiTosti.json");
        saveRecipe("validRecipes/validRecipeTruffles.json");
        String text = getRequestJSon("filterRequests/filterRequest4.json");
        final MvcResult result = this.mockMvc.perform(get(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)).andReturn();
        assertThat(result.getResponse().getStatus(), is(200));
        List<Recipe> recipes = extractRecipesFromResponse(result);
        assertEquals(1, recipes.size());
        assertEquals("truffles", recipes.get(0).getName());
    }

    @Test
    public void return_correct_recipe_when_both_contains_and_not_contains_selected() throws Exception {
        saveRecipe("validRecipes/validRecipeChocolateTart.json");
        saveRecipe("validRecipes/validRecipeNachos.json");
        saveRecipe("validRecipes/validRecipeSalamiTosti.json");
        saveRecipe("validRecipes/validRecipeTruffles.json");
        String text = getRequestJSon("filterRequests/filterRequest5.json");
        final MvcResult result = this.mockMvc.perform(get(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)).andReturn();
        assertThat(result.getResponse().getStatus(), is(200));
        List<Recipe> recipes = extractRecipesFromResponse(result);
        assertEquals(1, recipes.size());
        assertEquals("nachos", recipes.get(0).getName());
    }

    @Test
    public void return_correct_recipe_when_search_by_instruction() throws Exception {
        saveRecipe("validRecipes/validRecipeChocolateTart.json");
        saveRecipe("validRecipes/validRecipeNachos.json");
        saveRecipe("validRecipes/validRecipeSalamiTosti.json");
        saveRecipe("validRecipes/validRecipeTruffles.json");
        String text = getRequestJSon("filterRequests/filterRequest6.json");
        final MvcResult result = this.mockMvc.perform(get(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)).andReturn();
        assertThat(result.getResponse().getStatus(), is(200));
        List<Recipe> recipes = extractRecipesFromResponse(result);
        assertEquals(2, recipes.size());
        Assertions.assertThat(recipes)
                .extracting(Recipe::getName)
                .anyMatch(value -> value.matches("chocolate tart")).anyMatch(val -> val.matches("truffles"));
    }



    @Test
    public void return_bad_request_when_contains_and_not_contains_same_item() throws Exception {
        saveRecipe("validRecipes/validRecipeChocolateTart.json");
        saveRecipe("validRecipes/validRecipeNachos.json");
        saveRecipe("validRecipes/validRecipeSalamiTosti.json");
        saveRecipe("validRecipes/validRecipeTruffles.json");
        String text = getRequestJSon("filterRequests/filterRequest7.json");
        final MvcResult result = this.mockMvc.perform(get(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)).andReturn();
        assertThat(result.getResponse().getStatus(), is(400));
    }

    private String saveRecipe(String path) throws Exception {
        String text = getRequestJSon(path);
        final MvcResult result = this.mockMvc.perform(post(RECIPE_CONTROLLER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(text)).andReturn();
        return result.getResponse().getContentAsString();
    }

    private List<Recipe> extractRecipesFromResponse(MvcResult result) throws com.fasterxml.jackson.core.JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result.getResponse().getContentAsString(), mapper.getTypeFactory().constructCollectionType(List.class, Recipe.class));
    }

    private String getRequestJSon(String s) throws IOException {
        File resource = new ClassPathResource(s).getFile();
        return new String(Files.readAllBytes(resource.toPath()));
    }

    private String addIdToRequestJson(Recipe recipe, String text) {
        return text.charAt(0)
                + " \"id\":" + recipe.getId() + ","
                + text.substring(1);
    }
}

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Endpoints](#endpoints)

## General info
This project is to create recipes and display them. Each recipe holds ingredients with the amounts, how much portions can be served with that recipe, information about whether recipe is vegatarian or not, and instructions about how to cook it. It is possible to update each recipe among with the delete option. Based on filter options, it's also possible to get specific recipes by choice. 
## Technologies
Technologies used:
* Spring Boot 2.3.10.RELEASE
* Java 11
* Maven

## Setup
To run the project locally:
For now projects run with an in memory database so does not need to pull or install any database. 
$ mvn spring-boot:run
```

## Endpoints 

  Path                          | Method    | Description                                                                                                                                                  |
--------------------------------|-----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
`api/v1/recipe`                 | POST      | To add a new recipe, request body must contain a valid recipe. If recipe not valid, then the response will be a bad request.                                 |
`api/v1/recipe`                 | GET       | To receive a recipe. If any filter is not selected, then all recipes will return. If the filter form is filled, the results will be filtered accordingly.    |
`api/v1/recipe` 	        | PUT       | To update a recipe. It must send a valid recipe which is holding the recipe id to update. If recipe is not found, then the response will be Not Found Error. |
`api/v1/recipe` 	        | DELETE    | To delete a recipe. Recipe id must also be passed with as a request parameter. If recipe is not found, then the response will be Not Found Error.            |
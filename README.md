# URL shortener
## Setup
*   Docker
*   JDK 8 or higher version
*   Maven 3.5.X
*   Run application using command line from the project directory - mvn spring-boot:run
    or docker run -p 127.0.0.1:8080:8080/tcp shijinraj/urlshortner
*   URL Shortner Swagger URL- http://localhost:8080/swagger-ui/index.html

## Application Features
*   Spring Boot project with Spring Dev tools, Spring Security, Spring Data JPA, H2 DB, JUnit 5 and Swagger
*   In Memory Authentication used
*   Normal user credentials - user name : user \ password : user
*   Admin user credentials - user name : admin \ password : admin
*   Please use separate browser \ incognito window to test multiple user using swagger.
*   Centralised Exception handling using @RestControllerAdvice - please refer the package de.test.url.shortener.exception
*   Service, Controller and Integration Test cases are available
*   Dockerized app

## Create Tiny URL
*   HTTP METHOD - POST
*   URL - /api​/tinyurl
*   Request body - a valid URL
*   Response body - user statistics for the respective URL
    ```json
    {
      "callCount": 0,
      "creationCount": 0,
      "id": "string",
      "url": "string",
      "userId": "string"
    }
    ```
## Get Tiny URL
*   HTTP METHOD - GET
*   URL - /api/tinyurl/{id}
*   Path Variable - Tiny URL Id
*   Response body - Complete URL
## Get a user Statistics
*   Provides statistics for the respective user
*   HTTP METHOD - GET
*   URL - /api/tinyurl/user/statistics
*   Response body -
    ```json
    [
      {
        "callCount": 0,
        "creationCount": 0,
        "url": "string"
      }
    ]
    ```
## Get All Statistics
*   only permits admin users
*   HTTP METHOD - GET
*   URL - /api/tinyurl/statistics
*   Response body -
    ```json
    [
      {
        "callCount": 0,
        "creationCount": 0,
        "url": "string"
      }
    ]
    ```
## Get a user Statistics
*   only permits admin users
*   HTTP METHOD - GET
*   URL - /api/tinyurl/user/{userId}/statistics
*   Path Variable - userId
*   Response body -
    ```json
    [
      {
        "callCount": 0,
        "creationCount": 0,
        "url": "string"
      }
    ]
    ```
## Assumption
*   Abbreviations provide a separate URL for each user
*   Data base generate uuid is considered as tiny URL id
*   Complete URL will be given upon calling REST service /api/tinyurl/{id}
*   In Memory Authentication details available in SecurityConfig class
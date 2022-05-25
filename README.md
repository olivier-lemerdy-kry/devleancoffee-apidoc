# Dev Lean Coffee: API Doc

[![Java CI with Gradle](https://github.com/olivier-lemerdy-kry/devleancoffee-apidoc/actions/workflows/gradle.yml/badge.svg)](https://github.com/olivier-lemerdy-kry/devleancoffee-apidoc/actions/workflows/gradle.yml)

## Requirements

You need a JDK 17+ to run this project

## Launch the application locally

Run `SPRING_SECURITY_USER_PASSWORD=password ./gradlew bootRun`

You can access the main API by opening http://localhost:8080/events

## Building / launching the Docker Image

Run `./gradlew bootBuildImage`

Then run `docker run -p 8080:8080 -e SPRING_SECURITY_USER_PASSWORD=password -t docker.io/library/devleancoffee-apidoc:0.0.1-SNAPSHOT`

## Logging in the application

You can log in the application with `user / password` couple of credentials.

## Swagger / OpenAPI 3

> The OpenAPI Specification, previously known as the Swagger Specification, is a specification for machine-readable interface files for describing, producing, consuming, and visualizing RESTful web services. 
> Previously part of the Swagger framework, it became a separate project in 2016, overseen by the OpenAPI Initiative, an open-source collaboration project of the Linux Foundation.
> Swagger and some other tools can generate code, documentation, and test cases given an interface file.

You can access local Swagger at http://localhost:8080/swagger-ui/index.html

## Spring Rest Docs

> Spring REST Docs helps you to document RESTful services.
> It combines hand-written documentation written with Asciidoctor and auto-generated snippets produced with Spring MVC Test. This approach frees you from the limitations of the documentation produced by tools like Swagger.
> It helps you to produce documentation that is accurate, concise, and well-structured. This documentation then allows your users to get the information they need with a minimum of fuss.

We want to produce an API Documentation like the one from Auth0: https://auth0.com/docs/api/authentication#login

Our template API Documentation Guide is [available here](src/docs/asciidoc/api-guide.adoc)

Our main test class generating the snippets used in the code is [available here](src/test/java/se/kry/dev/leancoffee/apidoc/ApplicationTest.java)

The generated build is deployed in the application here: http://localhost:8080/docs/api-guide.html

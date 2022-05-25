# Dev Lean Coffee: API Doc

[![Java CI with Gradle](https://github.com/olivier-lemerdy-kry/devleancoffee-apidoc/actions/workflows/gradle.yml/badge.svg)](https://github.com/olivier-lemerdy-kry/devleancoffee-apidoc/actions/workflows/gradle.yml)

## Requirements

You need a JDK 17+ to run this project

## Launch the application

Run `SPRING_SECURITY_USER_PASSWORD=password ./gradlew bootRun`

You can access the main API by opening http://localhost:8080/events

You can log in the application with `user / password` couple of credentials.

## Swagger / OpenAPI 3

> The OpenAPI Specification, previously known as the Swagger Specification, is a specification for machine-readable interface files for describing, producing, consuming, and visualizing RESTful web services. 
> Previously part of the Swagger framework, it became a separate project in 2016, overseen by the OpenAPI Initiative, an open-source collaboration project of the Linux Foundation.
> Swagger and some other tools can generate code, documentation, and test cases given an interface file.

You can access local Swagger at http://localhost:8080/swagger-ui/index.html

## Spring Rest Docs


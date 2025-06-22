# general business requirements 

the web app will facilitate the scheduling of leisure time activities for a group of friends.
there will be no user authentication. instead, each user that have the link to a created activity will be able to modify it.

# general technical requirements

## development requirements 
the app will be developed using spring boot using a strict TDD test first approach, and use JGiven to make the test much more human readable.
also the app should have a hexagonal architecture. 

## deployment requirements 
* the app should be made to be deployed on aws-lambda, and using s3 to store the state, we do NOT use spring boot sessions or similar.
  * we want to use "Serverless Java container for Spring Boot Web" with using  the built-in Lambda function handler that serves as an entrypoint
* during development and testing lambda function locally use LocalStack , docker is already installed on this machine 
* for JTE set the "developmentMode" to "true" , for the template use the folder "src/main/jte" and use the `@ImportAutoConfiguration(JteAutoConfiguration.class)` for all `@WebMvcTest` to make it work with JTE templates
* 
# general business requirements 

the web app will facilitate the scheduling of leisure time activities for a group of friends.
there will be no user authentication. instead, each user that have the link to a created activity will be able to modify it.

# general technical requirements

## development requirements 
the app will be developed using spring boot using a strict TDD test first approach.
also the app should have a hexagonal architecture. 

## deployment requirements 
* the app should be made to be deployed on aws-lambda, and using s3 to store the state
  * Let’s start by adding the Lambda core dependency to our project’s pom.xml file
  * Next, we’ll need to add the Maven Shade Plugin. The Maven Shade Plugin is essential when building AWS Lambda functions with Java. It allows us to package our application and its dependencies into a single, self-contained JAR file, also known as an “uber” or “fat” JAR.
* during development and testing lambda function locally use LocalStack , docker is already installed on this machine 
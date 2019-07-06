# coolgisttest

This project is a test setup developed on top of Maven + RestAssured + JUnit , to test Github Gist API's for authenticated and unauthenticated api calls

## System Requirements

a) _maven_ : You can follow steps mentioned [here](https://maven.apache.org/install.html) or use [sdkman](https://sdkman.io/install) , preffered way is using sdkman which allows you to set local context in your terminal.

_sdkman commands to use maven_ : 

- open your terminal window and type : `sdk install maven 3.6.0`
this will install maven in your machine and if already present it switch to that context

1. Expand zip and open it in your editor : preffered editor IntelliJ IDEA
1. make sure you have correct access tokens in `src/test/respources/config.properties` file
1. from your terminal where you executed sdkman command to install maven execute `cd <PROJECT_PATH> && mvn clean test` where `PROJECT_PATH` is the folder path on your directory where you have placed the project 

  
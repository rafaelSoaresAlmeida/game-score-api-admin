# game-score-api-admin

This program is an Async API to administrate users (create, update and delete) to access Game Score project :

## Technologies(Frameworks and Plugins)
This project I have developed using Intellij IDE and these technologies and frameworks:

	-Java 15 (OPEN JDK)
    -Springboot,
    -Spring Webflux
    -Spring Security,
    -JWT,
    -Mongo DB,
    -Flapdoodle Embed Mongo,
    -Gradle,
    -Lombok,
    -Actuator,
	-PMD plugin,
	-Checkstyle,
    -Spring rest.

### Additional Links
These additional references should also help you:

* [Swagger URL for endpoints documentation](http://localhost:8091/swagger-ui.html)

## About project
	This project is formed per one SpringBoot Application.
        Notes about application:
            -It is configured to listen 8091 port;
            -It is configured to use Spring Security with JWT;
			-There are unit tests for service layer;
			-There are integration tests for API that simulate the complete flows;
			-These tests are configured to use Flapdoodle Embed Mongo, I mean, you don't need to has a Mongo instance configured to execute the tests.
			-This project is using PMD (https://maven.apache.org/plugins/maven-pmd-plugin/) and Checkstyle (https://maven.apache.org/plugins/maven-checkstyle-plugin/) plugins to keep a good quality in -the code.
			-During every build process, these process are executed:
				Execute unit tests for service layer
				Execute integration tests
				Execute Checkstyle verification
				Execute PMD verification	
				build jar file

## Run
To run application you need to set two environment variables:

           -SPRING.PROFILES.ACTIVE= web
           -SPRING_DATA_MONGODB_URI= <Mongo DB uri configuration>
           -SPRING_JWT_SECRET= <String>
           -SPRING_JWT_EXPIRATION= <ex: 28800>
           -PWD_SECRET_ENCODER=the_Vaca_Jairo_is_super_star
           -ITERATION=<Number>
           -KEY_LENGTH=<Number>
           -IV=<String>
           -SALT=<String>

Also, you need to set an administrator user in your Mongo database to access the endpoint that are protected by Spring Security with JWT, see the example below of a document that belongs an administrator user:
 ```bash
{
    "_id":{"$oid":"602ed94c1839b4e6acb6a01b"},
    "email":"userTest@test.com",
    "name":"Cansado",
    "password":"<password encoded by AESEncoder class>",
    "role":"ROLE_ADMIN"
}
```
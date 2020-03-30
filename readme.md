# REST version of Spring PetClinic Sample Application migrated to Quarkus

# Migration of Spring Pet Clinic to Quarkus

## Libraries used

1. Spring
   1. actuator
   1. cache
   1. data-jpa
   1. web
   1. test
   1. devtools
   1. documentation ( swagger )
   1. h2
   1. postgresql
   1. cache-api
   1. ehcache
   
1. Quarkus
    1. smallrye-health
    1. smallrye-metrics
    1. hibernate panache
    1. resteasy ( JAXRS )
    1. quarkus test
    1. openapi
    1. h2
    1. postgresql 


## Steps

1. Add Quarkus dependencies
2. Replaced PropertyComparator and MutableSortDefinition by a Java Stream
3. Replace Autowired by Inject
4. Removed @Profile and @Repository
5. Repository interfaces implement PanacheRepository
6. Rename findAll to listAll on repositories
7. Changed return type for listAll
8. Removed SpringApplication
9. Changing @RestController, @RequestMapping, @PathVariable -> @Path , @GET,@POST,@PUT,@DELETE, @PathParam, @Produces
10. Changing ResponseEntity by Response.entity
11. Changing BindingResult by ConstraintValidation
12. Removed JDBC repositories
13. Addded ApplicationScoped to repository implemented class
14. Added REST Security through @RolesAllowed
15. Replaced hard coded roles with Roles constants
16. CORS configured on application.properties file
17. Removed DataAccessException throw clause
18. Removed 2 levels repository implementation *Repository + * RepositoryImpl -> *Repository
19. Moved to JUnit 5
20. Removed in tests : SpringBootTest , ContextConfiguration, WebAppConfiguration
21. Added QuarkusTest
22. Removed tests classes for JDBC and SpringDataJPA
23. Migrated REST tests to RestAssured and Junit 5
24. Added OpenAPI support and removed ApplicationSwaggerConfig
25. Removed security classes as we do with Elytron conf
26. Added properties for security
27. Added microprofile metrics with smallrye-metrics extension
28. Modified tests to use QuarkusTest, removing Mocking and removing the abstract test class
29. Migrated @ControllerAdvice as global exception handler to JAX-RS ExceptionMapper
30. Converted PropertyComparator and MutableSortDefinition to Stream.sorted
31. Replace Spring AOP with CDI Interceptor and adding the annotation to few methods
32. Configuring properties file with attributes for Datasource
33. Creating import-test.sql with SQL command to init the database and removed the rest of sql files
34. As PanacheRepository takes Long as id, refactored to implement PanacheRepositoryBase<Entity, Integer>
35. Removed properties for databases not used
36. Created 2 import-test.sql to init the database (1 for prod 1 for test)
37. Changed to Quarkus 999-SNAPSHOT (local build) because issue : https://github.com/quarkusio/quarkus/issues/6349
38. Modified repository methods that save , and use persist instead of em.persist
39. Added @Consumes to all resources PUT/POST
40. replace resteasy-jsonb to resteasy-jackson 
41. Added Metrics annotations to Services methods 

## TODO

1. Cache
  
## NOT MIGRATED

1. JDBC repositories ( Use Agroal commands)
2. Spring Data repositories
3. JMX for the Audit ( Not available JMX on GraalVM )

## Tests
At the moment there's an issue and tests do not succeed.[WIP]

## Build
Currently using the master ( 999-SNAPSHOT ) branch of Quarkus due to recent changes that could help on the test issue.  
You need to clone Quarkus and build it locally  
```
$ git clone git@github.com:quarkusio/quarkus.git
$ mvn clean install -DskipTests
```
( tests usually take > 30 minutes )
### JVM Interactive
```
$ sdk use java 8.0.181-open  
$ mvn clean compile quarkus:dev
```  
### JVM 
```
$ sdk use java 8.0.181-open  
$ mvn clean package -Dquarkus.package.uber-jar=true  
$ java -jar ./target/spring-petclinic-rest-2.1.5-runner.jar
```  
### Graal
```
$ sdk use java 19.2.1-grl  
$ mvn clean package -Pnative  
$ ./target/spring-petclinic-rest-2.1.5 -runner
```

### Docker
type = [jvm,native,uberjar] select depending on the previous mvn package you selected  
```
$ docker build -f src/main/docker/Dockerfile.{type} -t quarkus/spring-petclinic-rest .  
$ docker run -i --rm -p 8080:8080 quarkus/spring-petclinic-rest
```

## Presentation Slides ( BarcelonaJUG, MadridJUG, DevNexus )
https://es.slideshare.net/jonathanvila/migration-spring-boot-petclinic-rest-to-quarkus-120



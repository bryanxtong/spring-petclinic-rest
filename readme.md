Problems are as below

1. petType cannot be deleted
2. Visit cannot be deleted
3. pets cannot be deleted
4. security is not implemented as It is using the jdbc and maybe two datasources needed
5. tests needs to be added


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
    2. quarkus cache


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
42. Upgraded to Quarkus 1.7.0.Final
43. Commented out wrong import-pg.sql sentences
44. Added Cache support
45. Improved tests

  
## NOT MIGRATED

1. JDBC repositories ( Use Agroal commands)
2. Spring Data repositories
3. JMX for the Audit ( Not available JMX on GraalVM )

## Tests
[WIP] Some tests work, in the process of adapting the rest of the tests

### JVM Interactive 
```
$ mvn clean compile quarkus:dev
```  

### JVM 
Install podman : https://podman.io/getting-started/installation
```
$ mvn clean package -DskipTests 
$ podman run --name petclinic -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres 
$ java -jar ./target/spring-petclinic-rest-2.1.5-runner.jar
```  
Currently tests are not 100% completed

### Graal
```
$ mvn clean package -DskipTests -Pnative -Dquarkus.native.container-build=true 
$ podman run --name petclinic -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres
$ ./target/spring-petclinic-rest-2.1.5-runner
```

### Container
type = [jvm,native,uberjar] select depending on the previous mvn package you selected  
```
$ podman build -f src/main/docker/Dockerfile.{type} -t quarkus/spring-petclinic-rest .  
$ podman run -i --rm -p 8080:8080 quarkus/spring-petclinic-rest
```

## Presentation Slides ( BarcelonaJUG, MadridJUG, DevNexus, ... )
Slides : bit.ly/sb2qks

Article : https://dzone.com/articles/migrating-a-spring-boot-application-to-quarkus-cha

Video : https://www.youtube.com/watch?v=BHuQ_9jATk0


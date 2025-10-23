Project dependencies (from pom.xml)

- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-validation
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-devtools (runtime, optional)
- org.springframework.boot:spring-boot-starter-test (test)
- org.springframework.boot:spring-boot-testcontainers (test)
- org.testcontainers:junit-jupiter (test)
- com.h2database:h2 (runtime)
- com.mysql:mysql-connector-j (runtime)

Note: Use Maven to manage and install these dependencies. To build and run the project locally:

1. mvn -B package
2. java -jar target/book-management-0.0.1-SNAPSHOT.jar

# Travel way

## local setup

- add file `application-dev.properties` in resources files
- add `--spring.config.location=classpath:application.properties,classpath:application-dev.properties` as startup
  argument
- in `application-dev.properties` add database and jwt properties e.g
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/travelWay
spring.datasource.username=admin
spring.datasource.password=passwd
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
travel.way.auth.secret=super-secret
spring.profiles.active=dev
```
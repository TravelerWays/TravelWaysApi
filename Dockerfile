FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN /bin/sh ./mvnw dependency:go-offline
COPY ./src ./src
RUN /bin/sh ./mvnw clean install


FROM eclipse-temurin:17-jre-jammy

ARG CONNECTION_STRING
ARG DB_PASSWORD
ARG DB_USER
ARG MAIL_PASSWORD

ENV APP_ENV=production
ENV spring_profiles_active = production
ENV travel_ways_mail_send=true
ENV travel_ways_mail_password=${MAIL_PASSWORD}
ENV spring_datasource_url=${CONNECTION_STRING}
ENV spring_datasource_password=${DB_PASSWORD}
ENV spring.datasource.username=${DB_USER}

WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
ENTRYPOINT ["java", "-jar", "/opt/app/*.jar" ]
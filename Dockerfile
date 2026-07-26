ENTRYPOINT ["java","-jar","app.jar"]

FROM eclipse-temurin:21-jre

LABEL maintainer="Raja Gokul"
LABEL application="employee-management-system"

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

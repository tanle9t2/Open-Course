# Stage 1: Build the WAR
FROM maven:3.8.7-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk17


# Remove default webapps (optional)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the built WAR from build stage into Tomcat webapps directory
COPY --from=build /app/target/OpenCourse.war /usr/local/tomcat/webapps/OpenCourse.war

# Expose Tomcat default port
EXPOSE 8080
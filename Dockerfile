# ---------- Stage 1: Build the WAR ----------
FROM maven:3.8.8-eclipse-temurin-17 AS build


# Set working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Package the WAR without running tests
RUN mvn clean package -DskipTests


# ---------- Stage 2: Deploy to Tomcat ----------
FROM tomcat:10.1-jdk17

# Clean default webapps (optional but recommended)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR from build stage
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/OpenCourse.war

# Expose Tomcat default HTTP port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]

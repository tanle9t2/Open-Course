FROM tomcat:10.1-jdk17


# Optional: clean default apps
RUN rm -rf /usr/local/tomcat/webapps/*

COPY ./target/OpenCourse.war /usr/local/tomcat/webapps/OpenCourse.war

CMD ["catalina.sh", "run"]
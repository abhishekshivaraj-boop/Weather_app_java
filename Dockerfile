FROM tomcat:10.1-jdk17
COPY deploy/weatherapp /usr/local/tomcat/webapps/weatherapp
EXPOSE 8080
CMD ["sh", "-c", "env && catalina.sh run"]
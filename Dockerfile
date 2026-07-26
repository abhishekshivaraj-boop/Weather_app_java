FROM tomcat:10.1-jdk17
COPY deploy/weatherapp /usr/local/tomcat/webapps/weatherapp
EXPOSE 8080
CMD ["sh", "-c", "echo TEST_VAR=$TEST_VAR && echo CHECKING_ENV_VAR=$OPENWEATHER_API_KEY && catalina.sh run"]
FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

COPY UserService.java .

RUN javac UserService.java

EXPOSE 8090

CMD ["java", "UserService"]

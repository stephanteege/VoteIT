FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

COPY . .

RUN mkdir -p images
RUN javac *.java

EXPOSE 8089

CMD ["java", "Main"]

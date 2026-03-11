FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY *.java ./
COPY index.html ./
COPY logo.png ./
COPY posts_data.csv ./
RUN mkdir -p images && chmod 777 posts_data.csv && javac *.java
EXPOSE 8089
CMD ["java", "Main"]
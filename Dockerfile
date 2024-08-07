FROM eclipse-temurin:17.0.8.1_1-jre-alpine
COPY ./build/libs/deerear.jar /app/deerear.jar
ENTRYPOINT ["java","-jar","/app/deerear.jar"]
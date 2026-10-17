# Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Se o Render informar DB_HOST/DB_PORT/DB_NAME, monta a URL JDBC automaticamente
ENTRYPOINT ["sh", "-c", "if [ -n \"$DB_HOST\" ]; then export DB_URL=\"jdbc:postgresql://$DB_HOST:${DB_PORT:-5432}/$DB_NAME\"; fi; exec java -XX:MaxRAMPercentage=75 -jar app.jar"]

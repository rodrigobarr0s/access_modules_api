# Etapa de build
# docker build -t access-modules-api .
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B -DskipTests clean package

# Etapa de execução
# docker run -p 8080:8080 access-modules-api (container em foreground)
# docker run -d -p 8080:8080 access-modules-api (container em background)
# docker logs -f <nome ou id do container>
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xms256m -Xmx512m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
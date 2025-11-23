# Para construção da build
# docker build -t access-modules-api .

# Etapa de build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY . .
RUN mvn -B -DskipTests clean package

# Etapa de execução
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xms256m -Xmx512m"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Para execuçao e logs
# docker run -p 8080:8080 access-modules-api (container em foreground)
# docker run -d -p 8080:8080 access-modules-api (container em background)
# docker logs -f <nome ou id do container>
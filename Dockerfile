# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-25 AS build
WORKDIR /app

# Cache de dependencias
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Compilar
COPY src ./src
RUN mvn -q -DskipTests clean package

# ---------- Run stage ----------
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render usa la variable PORT
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT} -jar /app/app.jar"]

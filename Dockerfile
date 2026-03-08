# ---------- Build stage (JDK 25 + Maven) ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Instala Maven en el build stage
RUN apt-get update && apt-get install -y --no-install-recommends maven \
    && rm -rf /var/lib/apt/lists/*

# Cache de dependencias
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Compila el proyecto
COPY src ./src
RUN mvn -q -DskipTests clean package

# ---------- Run stage (JRE 25) ----------
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT} -jar /app/app.jar"]

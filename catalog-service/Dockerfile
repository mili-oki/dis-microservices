# === Build stage ===
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
# (opciono) ako je multi-module, kopiraj i parent/child poms za cache:
# COPY */pom.xml ./
RUN mvn -q -e -DskipTests dependency:go-offline

# Kopija izvornog koda samo za AKTUELNI modul (Dockerfile neka bude u root-u modula)
COPY . .
RUN mvn -q -e -DskipTests package

# === Runtime stage ===
FROM eclipse-temurin:17-jre
ENV TZ=Europe/Belgrade \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
WORKDIR /app
COPY --from=build /workspace/target/*-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app/backend

COPY backend/.mvn/ .mvn/
COPY backend/mvnw backend/pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY backend/src/ src/

RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/backend/target/project-pulse-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar"]

FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app

# Copy maven executable and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Build all dependencies 
RUN ./mvnw dependency:go-offline -B

# Copy and build source code
COPY src src
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Production stage
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp

# Copy dependency by layers for better caching
ARG DEPENDENCY=/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Set up user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Add health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8761/actuator/health || exit 1

# JVM Tuning
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication -XX:+ExitOnOutOfMemoryError"

# Run application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -cp app:app/lib/* com.example.eureka.EurekaServerApplication"]

# Expose ports
EXPOSE 8761

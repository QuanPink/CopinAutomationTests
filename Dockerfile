FROM maven:3.9.6-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy pom.xml trước để cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build project
RUN mvn clean compile test-compile -B

# Copy dependencies (bao gồm aspectjweaver)
RUN mvn dependency:copy-dependencies -DoutputDirectory=target/libs -DincludeScope=test -B

# ============================================
FROM eclipse-temurin:11-jre

WORKDIR /app

# Copy built artifacts
COPY --from=builder /app/target/classes ./classes
COPY --from=builder /app/target/test-classes ./test-classes
COPY --from=builder /app/target/libs ./libs
COPY --from=builder /app/src/test/resources/testSuites ./testSuites

# Copy run script
COPY run-tests.sh .
RUN chmod +x run-tests.sh

ENTRYPOINT ["./run-tests.sh"]
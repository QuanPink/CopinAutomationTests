# Use OpenJDK 11 as base image
FROM openjdk:11-jdk-slim

# Install necessary tools
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    xvfb \
    chromium \
    chromium-driver \
    firefox-esr \
    && rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV JAVA_HOME=/usr/local/openjdk-11
ENV CHROME_BIN=/usr/bin/chromium
ENV CHROMEDRIVER_PATH=/usr/bin/chromedriver
ENV DISPLAY=:99

# Create app directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for better caching)
COPY mvnw ./
COPY .mvn ./.mvn
COPY pom.xml ./

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN ./mvnw dependency:resolve

# Copy source code
COPY src ./src

# Copy test resources and configuration
COPY .env* ./
COPY src/test/resources ./src/test/resources

# Build the project
RUN ./mvnw clean compile test-compile

# Create script to start Xvfb and run tests
RUN echo '#!/bin/bash\n\
Xvfb :99 -screen 0 1024x768x24 -ac &\n\
export DISPLAY=:99\n\
sleep 2\n\
exec "$@"' > /app/start.sh && chmod +x /app/start.sh

# Default command
CMD ["/app/start.sh", "./mvnw", "test"]

# Expose port if needed (optional)
EXPOSE 4444
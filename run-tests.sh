#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🐳 Copin Test Runner Docker Setup${NC}"

# Function to run API tests
run_api_tests() {
    echo -e "${YELLOW}🚀 Running API Data Tests...${NC}"
    docker-compose run --rm copin-tests /app/start.sh ./mvnw test \
        -Dsurefire.suiteXmlFiles=src/test/resources/testSuites/CopinApiDataTest.xml
}

# Function to run UI tests
run_ui_tests() {
    echo -e "${YELLOW}🚀 Running UI Smoke Tests...${NC}"
    docker-compose run --rm copin-tests /app/start.sh ./mvnw test \
        -Dsurefire.suiteXmlFiles=src/test/resources/testSuites/CopinSmokeTests.xml
}

# Function to generate Allure report
generate_report() {
    echo -e "${YELLOW}📊 Generating Allure Report...${NC}"
    docker-compose run --rm copin-tests /app/start.sh ./mvnw allure:serve
}

# Function to clean up
cleanup() {
    echo -e "${YELLOW}🧹 Cleaning up...${NC}"
    docker-compose down
    docker system prune -f
}

# Function to build image
build_image() {
    echo -e "${YELLOW}🔨 Building Docker image...${NC}"
    docker-compose build --no-cache
}

# Main menu
case "$1" in
    "build")
        build_image
        ;;
    "api")
        run_api_tests
        ;;
    "ui")
        run_ui_tests
        ;;
    "all")
        run_all_tests
        ;;
    "report")
        generate_report
        ;;
    "clean")
        cleanup
        ;;
    *)
        echo -e "${GREEN}Usage: $0 {build|api|ui|all|report|clean}${NC}"
        echo ""
        echo -e "${BLUE}Commands:${NC}"
        echo -e "  ${YELLOW}build${NC}  - Build Docker image"
        echo -e "  ${YELLOW}api${NC}    - Run API data tests only"
        echo -e "  ${YELLOW}ui${NC}     - Run UI smoke tests only"
        echo -e "  ${YELLOW}all${NC}    - Run all tests"
        echo -e "  ${YELLOW}report${NC} - Generate Allure report"
        echo -e "  ${YELLOW}clean${NC}  - Clean up containers and images"
        echo ""
        echo -e "${BLUE}Examples:${NC}"
        echo -e "  ${GREEN}./run-tests.sh build${NC}  # Build image first time"
        echo -e "  ${GREEN}./run-tests.sh api${NC}    # Run only API tests"
        echo -e "  ${GREEN}./run-tests.sh all${NC}    # Run all tests"
        exit 1
        ;;
esac

echo -e "${GREEN}✅ Command completed!${NC}"
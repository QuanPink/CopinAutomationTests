#!/bin/bash

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
COMPOSE_FILE="docker-compose.yml"
ENV_FILE=".env"
PROFILE=${1:-api}  # Default to API tests

print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${GREEN}   Copin Automation - Docker Runner${NC}"
    echo -e "${BLUE}========================================${NC}"
}

check_prerequisites() {
    # Check Docker
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}❌ Docker is not installed${NC}"
        exit 1
    fi

    if ! docker info &> /dev/null; then
        echo -e "${RED}❌ Docker daemon is not running${NC}"
        exit 1
    fi

    # Check .env file
    if [ ! -f "${ENV_FILE}" ]; then
        echo -e "${YELLOW}⚠️  Creating .env from .env.example${NC}"
        cp .env.example ${ENV_FILE}
        echo -e "${YELLOW}Please update ${ENV_FILE} with your credentials${NC}"
        read -p "Press Enter after updating .env file..."
    fi

    echo -e "${GREEN}✓ Prerequisites checked${NC}"
}

build_image() {
    echo -e "${YELLOW}Building Docker image...${NC}"
    docker-compose build

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Build successful${NC}"
    else
        echo -e "${RED}❌ Build failed${NC}"
        exit 1
    fi
}

run_tests() {
    local profile=${1}

    echo -e "${YELLOW}Running ${profile} tests...${NC}"

    # Stop any existing containers
    docker-compose down

    # Run tests with specific profile
    docker-compose run --rm tests mvn test -P${profile}

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Tests completed successfully${NC}"
    else
        echo -e "${RED}❌ Some tests failed${NC}"
    fi
}

run_with_allure() {
    local profile=${1}

    echo -e "${YELLOW}Starting tests with Allure reporting...${NC}"

    # Stop existing containers
    docker-compose down

    # Start Allure service in background
    docker-compose up -d allure

    echo -e "${GREEN}✓ Allure report available at: http://localhost:5050${NC}"
    echo -e "${YELLOW}Running ${profile} tests...${NC}"

    # Run tests
    docker-compose run --rm tests mvn test -P${profile}

    echo -e "${GREEN}Check report at: http://localhost:5050${NC}"
    echo -e "${YELLOW}Press Ctrl+C to stop Allure service${NC}"

    # Keep Allure running
    docker-compose logs -f allure
}

quick_test() {
    # For quick testing without rebuilding image
    local profile=${1}

    echo -e "${YELLOW}Quick test mode (no rebuild)...${NC}"

    docker-compose run --rm \
        -v $(pwd)/src:/app/src:ro \
        -v $(pwd)/pom.xml:/app/pom.xml:ro \
        tests mvn test -P${profile}
}

cleanup() {
    echo -e "${YELLOW}Cleaning up...${NC}"
    docker-compose down -v
    docker system prune -f
    echo -e "${GREEN}✓ Cleanup complete${NC}"
}

# Main menu
print_header

case "${PROFILE}" in
    build)
        check_prerequisites
        build_image
        ;;
    api|ui|smoke|all)
        check_prerequisites
        run_tests ${PROFILE}
        ;;
    report-api|report-ui|report-smoke)
        check_prerequisites
        SUITE=${PROFILE#report-}
        run_with_allure ${SUITE}
        ;;
    quick-api|quick-ui)
        SUITE=${PROFILE#quick-}
        quick_test ${SUITE}
        ;;
    allure)
        echo -e "${YELLOW}Starting Allure service only...${NC}"
        docker-compose up allure
        ;;
    logs)
        docker-compose logs --tail=100 -f
        ;;
    clean)
        cleanup
        ;;
    stop)
        docker-compose down
        echo -e "${GREEN}✓ Services stopped${NC}"
        ;;
    *)
        echo "Usage: $0 {command}"
        echo ""
        echo "Commands:"
        echo "  ${GREEN}build${NC}         - Build Docker image"
        echo "  ${GREEN}api${NC}           - Run API tests"
        echo "  ${GREEN}ui${NC}            - Run UI tests"
        echo "  ${GREEN}smoke${NC}         - Run smoke tests"
        echo "  ${GREEN}all${NC}           - Run all tests"
        echo ""
        echo "With Allure Report:"
        echo "  ${GREEN}report-api${NC}    - Run API tests with live Allure report"
        echo "  ${GREEN}report-ui${NC}     - Run UI tests with live Allure report"
        echo ""
        echo "Quick Mode (no rebuild):"
        echo "  ${GREEN}quick-api${NC}     - Quick API test run"
        echo "  ${GREEN}quick-ui${NC}      - Quick UI test run"
        echo ""
        echo "Utilities:"
        echo "  ${GREEN}allure${NC}        - Start Allure report server only"
        echo "  ${GREEN}logs${NC}          - Show container logs"
        echo "  ${GREEN}stop${NC}          - Stop all services"
        echo "  ${GREEN}clean${NC}         - Clean up everything"
        echo ""
        echo "Examples:"
        echo "  $0 build              # Build image first"
        echo "  $0 api                # Run API tests"
        echo "  $0 report-api         # Run API tests with Allure"
        echo "  $0 quick-api          # Quick test without rebuild"
        exit 1
        ;;
esac
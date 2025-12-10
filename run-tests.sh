#!/bin/bash

PROFILE=${TEST_PROFILE:-api}

echo "=========================================="
echo "Running tests with profile: $PROFILE"
echo "=========================================="

if [ "$PROFILE" = "api" ]; then
    SUITE_FILE="testSuites/CopinApiDataTest.xml"
elif [ "$PROFILE" = "ui" ]; then
    SUITE_FILE="testSuites/CopinUITests.xml"
else
    SUITE_FILE="testSuites/CopinSmokeTests.xml"
fi

# Tìm aspectjweaver trong libs
ASPECTJ_JAR=$(find libs -name "aspectjweaver-*.jar" 2>/dev/null | head -1)

if [ -z "$ASPECTJ_JAR" ]; then
    echo "WARNING: aspectjweaver not found, running without it"
    java -cp "classes:test-classes:libs/*" \
         org.testng.TestNG \
         "$SUITE_FILE"
else
    echo "Using aspectjweaver: $ASPECTJ_JAR"
    java -javaagent:"$ASPECTJ_JAR" \
         -cp "classes:test-classes:libs/*" \
         org.testng.TestNG \
         "$SUITE_FILE"
fi

EXIT_CODE=$?

echo "=========================================="
echo "Tests completed with exit code: $EXIT_CODE"
echo "=========================================="

exit $EXIT_CODE
# Test-runner image: builds the framework and executes the TestNG suite
# against whatever GRID_URL/BASE_URL/credentials are supplied at `docker run`
# / docker-compose time. Used locally, in Jenkins, and on the AWS EC2 host
# (see docs/AWS_EC2_SETUP.md).

FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Cache dependencies separately from source so code-only changes don't
# re-download the internet on every build.
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY . .

FROM maven:3.9-eclipse-temurin-21
WORKDIR /workspace
COPY --from=builder /root/.m2 /root/.m2
COPY --from=builder /workspace /workspace

# GRID_URL is set by docker-compose to point at the Selenium Grid hub
# service; BASE_URL/TEST_USER_EMAIL/TEST_USER_PASSWORD come from the .env
# file. Overridable at `docker run -e ...` time.
ENV GRID_URL=""
ENV TEST_BROWSER="chrome"
ENV HEADLESS="true"

ENTRYPOINT ["mvn", "-B", "test"]
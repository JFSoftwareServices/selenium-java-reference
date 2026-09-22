FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /workspace

# Copy pom first to maximise Docker layer caching
COPY pom.xml .

RUN mvn -q -B dependency:go-offline

# Copy project source and test files
COPY . .


FROM maven:3.9-eclipse-temurin-21

WORKDIR /workspace

# Copy Maven dependencies from builder
COPY --from=builder /root/.m2 /root/.m2

# Copy project
COPY --from=builder /workspace /workspace

# Runtime configuration
ENV GRID_URL="http://selenium-hub:4444/wd/hub"
ENV TEST_BROWSER="chrome"
ENV HEADLESS="true"

ENTRYPOINT ["mvn", "-B", "test"]
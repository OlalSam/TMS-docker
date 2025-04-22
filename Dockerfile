# Stage 1: Build with Maven on JDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Download dependencies first (cacheable)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src/ src/
RUN mvn -B clean package

# Stage 2: Runtime on Payara Server Full
FROM payara/server-full:6.2025.4-jdk21

# Set environment variables
ENV CONFIG_DIR=/opt/payara/appserver/glassfish/config
ENV DEPLOY_DIR=/opt/payara/appserver/glassfish/domains/domain1/autodeploy


# Copy the PostgreSQL driver
COPY --from=build /root/.m2/repository/org/postgresql/postgresql/42.7.3/postgresql-42.7.3.jar \
     /opt/payara/appserver/glassfish/lib/

# Copy Payara post-boot commands into the config directory
COPY post-boot-commands.asadmin $CONFIG_DIR/post-boot-commands.asadmin

# Deploy your application
COPY --from=build /app/target/*.war $DEPLOY_DIR/TMS.war

# Expose HTTP and Admin ports
EXPOSE 8080 4848

# Use Payara’s foreground entrypoint, which runs PRE and POST boot scripts
ENTRYPOINT ["/opt/payara/scripts/startInForeground.sh"]


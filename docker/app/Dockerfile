ARG JAVA_VERSION=17
ARG MAVEN_VERSION=3.8.6

#
# Building application stage
#
FROM maven:${MAVEN_VERSION}-amazoncorretto-${JAVA_VERSION} AS builder
COPY src /opt/app/src
COPY pom.xml /opt/app
# Maven command meaning: clean: cleans up target folder, package: creates .jar file with all dependencies
RUN mvn -f /opt/app/pom.xml clean package

#
# Preparing container to be ready to run application
#
FROM amazoncorretto:${JAVA_VERSION} AS runner
# Install security patches
RUN yum update -y --security

WORKDIR /opt/app
# Install additional required packages
RUN yum -y install jq.x86_64 dos2unix
RUN yum install -y curl && yum clean all

# Copy in ready .jar file
COPY --from=builder /opt/app/target/code-challenge*-spring-boot.jar /opt/app/code-challenge-be.jar

# Set CMD to run java application from prepared .jar file
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "/opt/app/code-challenge-be.jar"]
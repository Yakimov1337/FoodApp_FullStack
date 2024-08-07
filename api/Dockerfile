# Step 1: Use Maven to build the application
# Use a Maven image to compile the Java application
FROM maven:3.9.3-amazoncorretto-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy the rest of the application
COPY src ./src

# Build the application and skip tests
RUN mvn package -DskipTests

# Step 2: Create the runtime image
# Use a slim OpenJDK image to run the application
FROM amazoncorretto:17

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/FoodSquad-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]

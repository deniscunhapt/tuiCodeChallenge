# TUI Code Challenge Project

## Introduction
This project is a Spring Boot application designed to showcase integration with GitHub services, providing a REST API to fetch repository information. 
It utilizes Spring Boot for the backend, integrating with GitHub's REST API to retrieve data about repositories, branches, and more.

## Requirements
- JDK 11 or later
- Maven 3.6 or later
- Docker (optional, for containerization and deployment)

## Installation

### Clone the Repository
Start by cloning the repository to your local machine:
```
git clone [repository-url]
cd code-challenge
```

### Build the Project
Use Maven to build the project:
```
./mvnw clean install
```

## Running the Application

### Run as a Spring Boot Application
```
./mvnw spring-boot:run
```

### Run with Docker
If you prefer using Docker, you can use the provided `docker-compose.yml` file to build and run the application:
```
docker-compose -f docker-compose.yml up --build
```

## Usage
Once the application is running, you can access the API documentation via Swagger UI at:
```
http://localhost:8098/challenge/swagger-ui/index.html
```

To download the api spec in yaml file you can access it at:
```
http://localhost:8098/challenge/v3/api-docs.yaml
```

To fetch repositories, make a GET request to the following endpoint:
```
http://localhost:8098/challenge/api/v1/repositories
```

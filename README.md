# StepIn Job Opening Service

Job Opening Service is responsible for managing job postings created by companies participating in recruitment campaigns.

## Features

- Create, update, and delete job openings
- List job openings by campaign and company
- Eligibility verification before any mutation operation
- Integration with Campaign Service for access control

## Technology Stack

- Java 21
- Spring Boot 3.x
- Maven
- WebClient for HTTP communication
- In-memory persistence (ConcurrentHashMap)

## Prerequisites

The **Campaign Service** must be running on `http://localhost:8080` for this service to function properly.

## Build Instructions

To build the project, run:

```bash
mvn clean install
```

This will compile the code, run all tests, and create an executable JAR file in the `target` directory.

## Run Instructions

### Option 1: Using Maven

```bash
mvn spring-boot:run
```

### Option 2: Using the JAR file

After building the project:

```bash
java -jar target/stepin-job-opening-service-0.0.1-SNAPSHOT.jar
```

## API Documentation

Once the service is running, you can access the interactive API documentation (Swagger UI) at:

**http://localhost:8081/swagger-ui.html**

The OpenAPI specification is available at:

**http://localhost:8081/api-docs**

## Health Check

The service exposes a health endpoint via Spring Boot Actuator:

**http://localhost:8081/actuator/health**

## Configuration

The service runs on port **8081** by default. The Campaign Service base URL can be configured in `src/main/resources/application.yml`:

```yaml
campaign-service:
  base-url: http://localhost:8080
```

## Key Endpoints

- `GET /campaigns/{campaignId}/companies/{companyId}/job-openings` - List job openings
- `POST /campaigns/{campaignId}/companies/{companyId}/job-openings` - Create a job opening
- `PUT /campaigns/{campaignId}/companies/{companyId}/job-openings/{jobId}` - Update a job opening
- `DELETE /campaigns/{campaignId}/companies/{companyId}/job-openings/{jobId}` - Delete a job opening

## Eligibility Checks

Before any create, update, or delete operation, the service verifies with the Campaign Service that:

- The campaign is not locked
- The campaign deadline has not passed
- The company has accepted the invitation to participate

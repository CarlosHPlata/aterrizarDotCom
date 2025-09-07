# Aterrizar Punto Com - Check-in Microservice

## Overview

This project is a microservice developed for **Aterrizar Punto Com**, an airline company. The microservice is responsible for managing the **check-in process** for flights, ensuring a smooth and efficient experience for users. It is built using **Java**, **Spring Boot**, and **Gradle**, and is designed to integrate seamlessly within a microservices architecture.

### Purpose and Intent

The primary goal of this service is to handle the check-in process for passengers. It ensures that all required fields are provided, validates the input data, and manages the session lifecycle for the check-in process. By isolating this functionality into a dedicated microservice, the system achieves better scalability, maintainability, and modularity.

### Modules

This project is divided into two main modules:

1. **HTTP Module**:
    - **Purpose**: Handles the HTTP layer of the application, including request routing and API endpoints.
    - **Key Responsibilities**:
        - Exposes RESTful endpoints for session initialization and check-in requests.
        - Maps incoming HTTP requests to the appropriate service layer methods.
        - Handles request validation and error responses.

2. **Service Module**:
    - **Purpose**: Contains the core business logic and models for the check-in process.
    - **Key Responsibilities**:
        - Manages the session lifecycle and validates check-in requests.
        - Implements the business rules for the check-in process.
        - Provides services consumed by the HTTP module.

## How to Run

### Prerequisites
Ensure the following tools are installed on your system:

| Tool           | Required Version |
|----------------|------------------|
| `Java`         | `24`     |
| `Gradle`       | `8.14.3` |

### Steps to Build and Run the Project

1. **Generate Code from OpenAPI Specification**  
   Run the following command to generate the necessary code:
```bash
./gradlew openApiGenerate
```

2. **Build the Project**  
   Use Gradle to build the project:
```bash  
./gradlew build
```

3. **Run the Application**  
   After building, start the application with:
   ```bash
   ./gradlew bootRun
   ```
## API Documentation

The API documentation is generated using OpenAPI and can be accessed at:
```
http://localhost:8080/aterrizar/swagger-ui/index.html
```

## Ways of Working

### Opening a Pull Request (PR)

When contributing to this repository, follow the **Git Flow** convention for branch naming and workflows. Ensure your branch name reflects the feature, bugfix, or hotfix you are working on. For example:
- `feature/implement-checkin-api`
- `bugfix/fix-session-timeout`
- `hotfix/fix-critical-bug`

Additionally, use **Conventional Commits** for your commit messages. This ensures consistency and clarity in the commit history. Examples of commit messages:
- `feat: add endpoint for check-in process`
- `fix: resolve null pointer exception in session validation`
- `chore: update Gradle wrapper to 8.14.3`

Once your work is complete, open a PR to the `main` branch, providing a clear description of the changes and linking any relevant issues.

### Unit Testing

Before pushing your changes, ensure that you have written unit tests for all new functionality. Run the test suite locally to verify that all tests pass:
```bash
./gradlew test
```
Do not push changes unless all tests pass successfully.

### Code Styling and Formatting 


This project adheres to a strict code style to maintain consistency. Before opening a PR, ensure your code meets the style guidelines by running the following commands:

1. Checkstyle:
```bash
./gradlew checkstyleMain checkstyleTest
```

2. Spotless (to automatically format your code):
```bash
./gradlew spotlessApply
```
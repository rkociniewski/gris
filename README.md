# GitHub Repository Info Service

This is a simple RESTful service built in **Kotlin + Ktor** that fetches details of a public GitHub repository. The data
is retrieved using GitHub's REST API and exposed through a single HTTP GET endpoint.

## 🔍 Features

- Fetches repository metadata from GitHub:
    - Full name
    - Description
    - Git clone URL
    - Number of stargazers
    - Creation date (localized)
- Returns data in a structured JSON format
- Fully tested with **unit** and **end-to-end (E2E)** test coverage
- Written in **Kotlin 2.1.20**, using **Gradle Kotlin DSL** and **Ktor 3.1.3**

## 📦 Technologies Used

- Kotlin **2.1.20**
- Java **21**
- Ktor **3.1.3**
- Jackson for JSON (de)serialization
- Koin for dependency injection
- MockK for mocking
- JUnit 5 & Kotlin Test
- Gradle

## 🔧 Build and Run

Make sure you have **Java 21** and **Gradle** installed. Then:

```bash
./gradlew build
````

To run the server locally:

```bash
./gradlew run
```

The service will be available at:
**`http://localhost:8080`**

## 📘 API Specification

### Get Repository Details

```
GET /repositories/{owner}/{repositoryname}
```

#### Parameters:

- `owner`: GitHub username or organization name
- `repositoryname`: Name of the repository

#### Response Format:

```json
{
  "fullName": "owner/repositoryname",
  "description": "Repository description",
  "cloneUrl": "https://github.com/owner/repositoryname.git",
  "stars": 42,
  "createdAt": "2023-01-01"
}
```

### Error Responses

| Code | Description                   |
|------|-------------------------------|
| 400  | Invalid or missing parameters |
| 404  | Repository not found          |
| 500  | Internal server error         |

---

## 📄 API Documentation

This project includes **OpenAPI** and **Swagger UI** integration for clear and interactive API exploration.

### 🔍 Swagger UI

You can access the Swagger interface in your browser at:

```
http://localhost:8080/swagger
```

It provides an interactive documentation page where you can test endpoints and inspect request/response schemas.

### 📦 OpenAPI Specification

The OpenAPI specification is available in YAML format at:

```
http://localhost:8080/openapi
```

The source file is located in:

```
src/main/resources/openapi/documentation.yaml
```

You can edit this file to keep the API contract in sync with the application logic.

---

## ✅ Running Tests

To run the full test suite (unit and E2E):

```bash
./gradlew test
```

All tests are located under:

* `src/test/kotlin/unitTests` – logic and deserialization tests
* `src/test/kotlin/e2eTests` – controller & integration tests

## 🗃 Structure

```
.
├── controller/          # HTTP routing and endpoints
├── service/             # GitHub API integration
├── model/               # DTOs and response classes
├── config/              # Ktor + DI setup
├── testUtils/           # Reusable test entities and helpers
└── e2eTests/            # End-to-end tests using Ktor Test Application
```

## 🔄 Future Improvements

* Authentication via GitHub token
* Rate limit awareness and error handling
* Caching of repeated results
* Docker support

## 📄 License

This project is provided as-is under the MIT License.

---

**Recruitment Task Summary:**

> A simple REST service that returns information about a given GitHub repository. Delivered as a Gradle-based Kotlin
> project with test coverage and API compliance.

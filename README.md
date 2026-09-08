# Hospital Management System – Microservices Architecture

A Hospital Management System built using **Spring Boot** and **Spring Cloud**, designed around a microservices architecture with centralized configuration, service discovery, an API gateway, and inter-service communication.

## Architecture

```text
                        ┌──────────────────┐
                        │   Config Server   │  (8888)
                        └─────────▲─────────┘
                                  │ config
                        ┌─────────┴─────────┐
                        │ Discovery Server   │  (8761)
                        │     (Eureka)       │
                        └─────────▲─────────┘
                                  │ register / discover
              ┌───────────────────┼───────────────────┐
              │                   │                    │
      ┌───────┴───────┐  ┌────────┴────────┐  ┌────────┴────────┐
      │ Doctor Service │  │ Patient Service │  │Appointment Service│
      │     (8082)     │  │     (8081)      │  │      (8083)       │
      └───────┬───────┘  └────────┬────────┘  └────────┬──────────┘
              │                   │                     │
              └─────────┬─────────┴──────────┬──────────┘
                        │  REST calls (WebClient, load-balanced)
                        │
                ┌───────┴────────┐
                │   API Gateway  │  (8080)
                └────────────────┘
                        ▲
                        │
                     Client
```

Each service has its own MySQL database and runs independently. Services communicate over REST rather than sharing a database.

## Modules

| Module                  | Port | Responsibility                                                                                                                                                                    |
| ----------------------- | ---: | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Config Server**       | 8888 | Serves externalized configuration (ports, database credentials, and routes) to every other service from a native `classpath:/config` source, so nothing is hardcoded per service. |
| **Discovery Server**    | 8761 | Eureka server. Every service registers here and looks up other services by name instead of hardcoded hosts and ports.                                                             |
| **API Gateway**         | 8080 | Single entry point for all client requests. Built on Spring Cloud Gateway Server WebMVC, it routes requests to the correct service through Eureka.                                |
| **Doctor Service**      | 8082 | Manages doctor records.                                                                                                                                                           |
| **Patient Service**     | 8081 | Manages patient records.                                                                                                                                                          |
| **Appointment Service** | 8083 | Manages appointments, verifies doctor and patient existence in real time before confirming a booking, and can return appointments enriched with full doctor and patient details.  |

## Features

- **Centralized configuration** — all service configurations (ports, datasource, and Eureka URL) are managed in one place via Spring Cloud Config Server.
- **Service discovery** — services find each other dynamically through Eureka instead of hardcoded URLs.
- **API Gateway routing** — clients only talk to one entry point (`localhost:8080`); the gateway forwards requests to the correct downstream service.
- **Client-side load balancing** — inter-service calls use a `@LoadBalanced` `WebClient`, so requests are automatically balanced across instances if a service is scaled.
- **Cross-service validation** — Appointment Service calls Doctor Service and Patient Service before confirming a booking, rejecting the appointment if either does not exist.
- **Data aggregation** — a dedicated endpoint returns appointments joined with full doctor and patient details fetched live from their respective services.
- **Independent databases per service** — each service owns its own MySQL schema, following the database-per-service pattern.
- **Full CRUD** on doctors, patients, and appointments.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Cloud (Config Server, Eureka, Gateway Server WebMVC, LoadBalancer)
- Spring Data JPA + Hibernate
- MySQL
- Maven
- Lombok

## API Reference

All requests below can be made directly to each service, or through the gateway at `http://localhost:8080` using the same paths.

### Doctor Service — `/doctor`

| Method | Endpoint       | Description        | Request Body                                     |
| ------ | -------------- | ------------------ | ------------------------------------------------ |
| POST   | `/doctor`      | Create a doctor    | `{ "name", "specialization", "email", "phone" }` |
| GET    | `/doctor`      | Get all doctors    | —                                                |
| GET    | `/doctor/{id}` | Get a doctor by ID | —                                                |
| PUT    | `/doctor/{id}` | Update a doctor    | `{ "name", "specialization", "email", "phone" }` |
| DELETE | `/doctor/{id}` | Delete a doctor    | —                                                |

### Patient Service — `/patient`

| Method | Endpoint        | Description         | Request Body                                               |
| ------ | --------------- | ------------------- | ---------------------------------------------------------- |
| POST   | `/patient`      | Create a patient    | `{ "name", "age", "gender", "phone", "email", "address" }` |
| GET    | `/patient`      | Get all patients    | —                                                          |
| GET    | `/patient/{id}` | Get a patient by ID | —                                                          |
| PUT    | `/patient/{id}` | Update a patient    | `{ "name", "age", "gender", "phone", "email", "address" }` |
| DELETE | `/patient/{id}` | Delete a patient    | —                                                          |

### Appointment Service — `/appointment`

| Method | Endpoint                           | Description                                                               | Request Body                                                                  |
| ------ | ---------------------------------- | ------------------------------------------------------------------------- | ----------------------------------------------------------------------------- |
| POST   | `/appointment`                     | Create an appointment (validates that the doctor and patient exist first) | `{ "doctorId", "patientId", "appointmentDate", "appointmentTime", "reason" }` |
| GET    | `/appointment`                     | Get all appointments (raw, with `doctorId`/`patientId` only)              | —                                                                             |
| GET    | `/appointment/viewAll`             | Get all appointments enriched with full doctor and patient objects        | —                                                                             |
| GET    | `/appointment/{id}`                | Get an appointment by ID                                                  | —                                                                             |
| DELETE | `/appointment/{id}`                | Delete an appointment                                                     | —                                                                             |
| GET    | `/appointment/doctor/{doctorId}`   | Fetch a doctor's details (proxied through Appointment Service)            | —                                                                             |
| GET    | `/appointment/patient/{patientId}` | Fetch a patient's details (proxied through Appointment Service)           | —                                                                             |

**Example — create an appointment:**

```http
POST /appointment
Content-Type: application/json

{
  "doctorId": 1,
  "patientId": 1,
  "appointmentDate": "2026-09-15",
  "appointmentTime": "10:30:00",
  "reason": "General checkup"
}
```

On success, `status` is set to `"Confirmed"` automatically once both the doctor and patient are verified to exist.

**Example — `/appointment/viewAll` response:**

```json
[
  {
    "id": 1,
    "doctor": {
      "id": 1,
      "name": "Dr. Smith",
      "specialization": "Cardiology",
      "email": "...",
      "phone": "..."
    },
    "patient": {
      "id": 1,
      "name": "John Doe",
      "age": 35,
      "gender": "Male",
      "phone": "...",
      "email": "...",
      "address": "..."
    },
    "appointmentDate": "2026-09-15",
    "appointmentTime": "10:30:00",
    "reason": "General checkup",
    "status": "Confirmed"
  }
]
```

## Running Locally

Each module is a separate Spring Boot application and must be started **in this order**:

1. **Config Server** (`configServer`) — `mvn spring-boot:run`
2. **Discovery Server** (`discoveryServer`) — `mvn spring-boot:run`
3. **Doctor Service**, **Patient Service**, and **Appointment Service** — any order, once the above two are up
4. **API Gateway** (`apiGateway`)

Requirements:

- Java 17+
- Maven
- MySQL running locally with a database created for each service (see each service's config in `configServer/src/main/resources/config`)

Once everything is running:

- Eureka dashboard: `http://localhost:8761`
- All APIs accessible through the gateway: `http://localhost:8080`

## Roadmap

- [ ] Add a circuit breaker (Resilience4j) so Appointment Service degrades gracefully if Doctor Service or Patient Service is down
- [ ] Move database credentials out of plaintext config into environment variables
- [ ] Add centralized logging and tracing across services

## Author

**Govind Dangi**  
GitHub: [govinddangi4564](https://github.com/govinddangi4564)  
Portfolio: [govinddangi.vercel.app](https://govinddangi.vercel.app/)

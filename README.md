🚕 FlashRide

[![Java](https://img.shields.io/badge/Java-Spring%20Boot-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-Event--Driven-231F20?logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![Redis](https://img.shields.io/badge/Redis-GEO-DC382D?logo=redis&logoColor=white)](https://redis.io/)
[![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)

**FlashRide** is a production-oriented, real-time ride-hailing backend built with Spring Boot microservices. It tracks driver locations, finds nearby available drivers through Redis GEO, and coordinates ride processing through Kafka-based events.

> Designed as a portfolio project that demonstrates practical microservices patterns for real-time, location-aware systems.

## ✨ Highlights

- 📍 Real-time driver location updates every **3 seconds**
- 🧭 Redis GEO–powered nearby-driver discovery
- ⚡ Reduced nearby-driver lookup latency from **~50 ms to ~5 ms**
- 🔄 Kafka-based asynchronous communication between ride-processing components
- 🗃️ MySQL-backed service data
- 🧩 Focused services for location, rides, matching, and users

## 📊 Engineering Impact

| Area | Earlier approach | Current approach | Verified impact |
|---|---|---|---:|
| Nearby-driver lookup | ~50 ms lookup | Redis GEO lookup (~5 ms) | **~90% lower latency** |
| Driver location freshness | ~30 sec stale-location window | Updates every 3 sec | **~90% smaller stale-location window** |
| Service communication | Direct/synchronous coupling | Kafka events | Decoupled asynchronous processing |
| Proximity search | Database-style lookup | Redis GEO spatial index | Efficient geospatial querying |

## 🏗️ Architecture

```text
                       ┌──────────────────┐
                       │      Clients     │
                       └────────┬─────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
┌───────▼────────┐     ┌────────▼───────┐      ┌────────▼───────┐
│ Location       │     │ Ride Service   │      │ User Service   │
│ Service        │     │                │      │ JWT + roles    │
└───────┬────────┘     └────────┬───────┘      └────────────────┘
        │                       │
        │                 ride-related events
        │                       │
        ▼                       ▼
┌───────────────┐      ┌────────────────┐
│ Redis GEO     │      │ Apache Kafka   │
│ driver index  │◄────►│ event backbone │
└───────────────┘      └───────┬────────┘
                               │
                       ┌───────▼────────┐
                       │ Matching        │
                       │ Service         │
                       └───────┬────────┘
                               │
                       ┌───────▼────────┐
                       │ MySQL          │
                       │ persistent data│
                       └────────────────┘
```

## 🧩 Services

| Service | Responsibility |
|---|---|
| **Location Service** | Receives driver location updates and maintains the real-time geospatial driver index in Redis GEO. |
| **Ride Service** | Handles ride-related processing and publishes/consumes ride lifecycle events. |
| **Matching Service** | Discovers nearby drivers and participates in matching rides to drivers. |
| **User Service** | Manages registration and login, issues JWTs, and enforces rider and driver role-based access. |

## 🔄 Ride Lifecycle & Event Flow

```text
Driver sends location
        │
        ▼
Location Service ──► Redis GEO updates driver position
        │
        ▼
Ride request enters Ride Service
        │
        ▼
Kafka event ──► Matching Service ──► Redis GEO nearby-driver lookup
        │                                      │
        └──────────────────────────────────────┘
                  asynchronous ride processing
```

The event-driven approach lets services react to ride activity without tightly coupling every step of the flow to a synchronous request chain.

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Backend | Java, Spring Boot |
| Architecture | Microservices |
| Messaging | Apache Kafka |
| Real-time geospatial search | Redis GEO |
| Primary database | MySQL |

## 🚀 Getting Started

### Prerequisites

- Java and the build tool configured by this repository
- MySQL
- Redis with GEO commands available
- Apache Kafka

### Configure infrastructure

1. Start MySQL, Redis, and Kafka locally or point the services to existing instances.
2. Configure each service’s Spring Boot configuration with the appropriate MySQL, Redis, and Kafka connection details.
3. Create any required MySQL databases before starting the related service.

### Run the services

Start the services individually, following the build wrapper included in the relevant service directory.

```bash
# Maven wrapper repositories
./mvnw spring-boot:run

# Gradle wrapper repositories
./gradlew bootRun
```

Start the Location Service, Ride Service, Matching Service, and User Service.

> Use the wrapper and configuration files that are present in your checkout; this README intentionally does not assume service ports, database names, Kafka topics, or endpoints that are not documented in the repository.

## 🔌 API Overview

FlashRide’s backend interactions center on these capabilities:

| Capability | Owning service |
|---|---|
| Driver location updates | Location Service |
| Nearby-driver discovery | Location Service / Matching Service via Redis GEO |
| Ride-related processing | Ride Service |
| Driver matching | Matching Service |
| Registration and login | User Service |
| JWT authentication and rider/driver role-based access | User Service |

Concrete routes, request bodies, response formats, and authentication behavior should be documented from the implemented controllers as the API evolves.

## 📁 Project Structure

```text
FlashRide/
├── location-service/       # Driver location ingestion and Redis GEO indexing
├── ride-service/           # Ride-related processing
├── matching-service/       # Nearby-driver lookup and matching workflow
├── user-service/           # Registration, login, JWT authentication, and user roles
└── README.md
```

> Directory names above describe the service layout. If this repository uses different module names, update this tree to match the checked-in structure.

## 🧠 Why Redis GEO?

Ride matching is fundamentally a location problem: the system needs to discover drivers close to a rider quickly and frequently. Redis GEO keeps driver coordinates in a geospatial index, enabling low-latency proximity queries while location updates continue every three seconds.

This reduced nearby-driver lookup latency from approximately **50 ms** to **5 ms**—about a **90% reduction**—and reduced the stale-location window from roughly **30 seconds** to **3 seconds**.

## 🗺️ Future Improvements

- Document implemented REST endpoints with examples
- Add authentication and authorization documentation when implemented
- Add containerized local development and deployment guidance
- Add automated integration and performance tests
- Add observability for cross-service event flows
- Expand matching rules as product requirements evolve

## 📌 Portfolio Notes

FlashRide focuses on the engineering challenges behind real-time mobility platforms: rapidly changing geospatial data, low-latency proximity queries, and asynchronous coordination across services. It uses Redis GEO and Kafka where they fit those constraints, alongside Spring Boot and MySQL for service development and persistence.

---

Built with Spring Boot, Kafka, Redis GEO, and MySQL.

# E-commerce Microservices App

This is a Spring Boot-based e-commerce application using microservices architecture.

## 🚀 Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Cloud (Eureka, Gateway)
- Kafka
- Redis
- JWT Auth
- MySQL / PostgreSQL
- Docker & Docker Compose
- GitHub Actions (CI/CD)

## 🧩 Microservices

| Service           | Description                    | Port |
|------------------|--------------------------------|------|
| API Gateway       | Central entry point            | 8080 |
| User Service      | Handles registration/login     | 8081 |
| Product Service   | Manages product catalog        | 8082 |
| Order Service     | Processes customer orders      | 8083 |
| Service Registry  | Eureka discovery server        | 8761 |

## 🐳 Docker Setup

To run all services locally using Docker Compose:

```bash
docker-compose up --build

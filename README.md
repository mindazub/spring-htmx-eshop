<div align="center">

# TechEcom

Server-rendered e-commerce app using Spring Boot, Thymeleaf, HTMX, and Spring Security.

[![Java 21](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 4.0.5](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Build: Maven](https://img.shields.io/badge/Build-Maven-blue)](https://maven.apache.org/)

</div>

## Quick Links

- [What This Project Is](#what-this-project-is)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Run Locally](#run-locally)
- [Run with Docker](#run-with-docker)
- [Demo Accounts and URLs](#demo-accounts-and-urls)
- [Endpoint Map](#endpoint-map)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)
- [Roadmap](#roadmap)

## What This Project Is

TechEcom shows how to build a modern web app without SPA complexity:

- Thymeleaf templates for server rendering
- HTMX for partial page updates
- Spring Security for login and role checks
- Spring Data JPA with H2 (dev) and PostgreSQL (docker)
- WebSocket + SockJS for real-time notifications

## Features

| Area | Details |
| --- | --- |
| Product Catalog | List, details, category filtering, search |
| Cart | Add, remove, update quantity, checkout flow |
| Authentication | Login, register, session-based auth |
| Authorization | Role-based access, admin-only routes |
| Admin Panel | Dashboard, user management, audit logs |
| Deployment | Local Maven run or Docker Compose stack |

## Tech Stack

- Java 21
- Spring Boot 4.0.5
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA + Hibernate
- HTMX + Tailwind CSS
- WebSocket, STOMP, SockJS
- H2 (development), PostgreSQL 16 (docker)
- Maven

## Run Locally

### Windows PowerShell

```powershell
Set-Location "C:\Users\mindaz\OneDrive - UAB Edislab\Documents\projects\INTELLIJEA\simple-crud\simple-htmx-crud-auth"
.\mvnw.cmd spring-boot:run
```

### Bash/Zsh

```bash
cd simple-htmx-crud-auth
./mvnw spring-boot:run
```

Application URL: `http://localhost:8087`

## Run with Docker

```powershell
Set-Location "C:\Users\mindaz\OneDrive - UAB Edislab\Documents\projects\INTELLIJEA\simple-crud\simple-htmx-crud-auth"
docker-compose up --build -d
docker-compose logs -f app
```

Stop services:

```powershell
docker-compose down
```

## Demo Accounts and URLs

### Demo Accounts

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin@admin.com` | `admin000` |
| Manager | `manager@example.com` | `manager000` |
| User | `user1@example.com` | `user1000` |

### Useful URLs

| Page | URL |
| --- | --- |
| Home | `http://localhost:8087/` |
| Products | `http://localhost:8087/products` |
| Cart | `http://localhost:8087/cart` |
| Admin | `http://localhost:8087/admin` |
| H2 Console | `http://localhost:8087/h2-console` |

## Project Structure

```text
simple-htmx-crud-auth/
|- src/main/java/mindaz/simplehtmxcrudauth/
|  |- domain/
|  |- infrastructure/
|  |- presentation/
|  |- shared/
|- src/main/resources/
|  |- templates/
|  |- static/
|  |- application.properties
|  |- application-postgres.properties
|- docker-compose.yml
|- Dockerfile
|- pom.xml
```

## Endpoint Map

### Public and Auth

| Method | Path |
| --- | --- |
| GET | `/` |
| GET | `/login` |
| GET | `/register` |
| POST | `/register` |
| GET | `/login-success` |
| GET | `/api/user-info` |

### Products

| Method | Path |
| --- | --- |
| GET | `/products` |
| GET | `/products/{slug}` |
| GET | `/products/category/{categorySlug}` |
| GET | `/products/search?query=...` |

### Cart (Authenticated)

| Method | Path |
| --- | --- |
| GET | `/cart` |
| POST | `/cart/add/{productId}` |
| POST | `/cart/update/{productId}` |
| POST | `/cart/remove/{productId}` |
| GET | `/cart/checkout` |
| POST | `/cart/checkout` |

### Admin (Role: ADMIN)

| Method | Path |
| --- | --- |
| GET | `/admin` |
| GET | `/admin/users` |
| POST | `/admin/users/create` |
| POST | `/admin/users/{id}/role/{role}` |
| POST | `/admin/users/{id}/delete` |
| GET | `/admin/audit` |

### WebSocket

- Endpoint: `/ws/notifications`
- Endpoint: `/ws/audit`
- Topic: `/topic/audit-log`

## Configuration

### Default (`application.properties`)

- `server.port=8087`
- `spring.h2.console.enabled=true`
- `spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- `server.servlet.session.timeout=30m`

### PostgreSQL profile

```powershell
$env:SPRING_PROFILES_ACTIVE="postgres"
.\mvnw.cmd spring-boot:run
```

## Troubleshooting

<details>
<summary><strong>Tailwind CDN warning in browser console</strong></summary>

Some templates load `https://cdn.tailwindcss.com`. This is fine for development. For production, compile Tailwind and serve a static CSS file.

</details>

<details>
<summary><strong>WebSocket POST 500 (`xhr_streaming` / `xhr_send`)</strong></summary>

- Restart the app after WebSocket configuration changes.
- Ensure SockJS endpoints are allowed with `setAllowedOriginPatterns("*")`.
- Ensure Spring Security does not block `/ws/**`.

</details>

<details>
<summary><strong>`favicon.ico` 404</strong></summary>

Add a favicon at `src/main/resources/static/favicon.ico`.

</details>

<details>
<summary><strong>Port 8087 already in use</strong></summary>

Update `server.port` in `src/main/resources/application.properties`, or remap docker ports in `docker-compose.yml`.

</details>

## Roadmap

- Improve checkout and order history
- Add payment gateway integration
- Add email notifications
- Add product reviews and ratings
- Add Redis caching
- Continue microservice extraction

## License

No `LICENSE` file is currently included in this repository.
